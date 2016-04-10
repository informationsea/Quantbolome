/*
 *  Quantbolome
 *    Copyright (C) 2016 Yasunobu OKAMURA All Rights Reserved.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jp.ac.tohoku.ecei.sb.metabolome.lims.impl;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import info.informationsea.dataclustering4j.matrix.DefaultLabeledMutableMatrix;
import info.informationsea.dataclustering4j.matrix.LabeledMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Created by yasu on 15/06/02.
 */

@Slf4j
public class IntensityMatrixImpl extends DefaultLabeledMutableMatrix<Double, Compound, Injection> implements IntensityMatrix {

    public IntensityMatrixImpl(int nrow, int ncol) {
        super(nrow, ncol, 0.0);
    }

    public IntensityMatrixImpl(LabeledMatrix<Double, Compound, Injection> original) {
        super(original);
    }

    public IntensityMatrixImpl(LabeledMatrix<Double, Compound, Injection> original, int top, int bottom, int left, int right) {
        super(original, top, bottom, left, right);
    }

    public IntensityMatrixImpl(double[] values, int ncol) {
        super(DoubleStream.of(values).mapToObj(Double::valueOf).toArray(Double[]::new), ncol);
    }

    public void writeCSV(Writer writer) throws IOException {
        CSVWriter csvWriter = new CSVWriter(writer);

        // Write header
        List<Injection> injections = getColumnKeys();
        String[] injectionFilenames = new String[injections.size() + 1];
        injectionFilenames[0] = "";
        for (int i = 0; i < injections.size(); i++) {
            injectionFilenames[i + 1] = injections.get(i).getFileName();
        }
        csvWriter.writeNext(injectionFilenames);

        // Write contents
        int rowNum = getSize()[0];
        int columnNum = getSize()[1];

        for (int i = 0; i < rowNum; i++) {
            String[] row = new String[columnNum + 1];
            for (int j = 0; j < columnNum; j++) {
                row[j + 1] = get(i, j).toString();
            }
            row[0] = "COMPOUND" + getRowKeys().get(i).getId();
            csvWriter.writeNext(row);
        }

        csvWriter.close();
    }

    public static IntensityMatrixImpl loadFromCSV(Reader reader, DataManager dataManager) throws IOException {
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();

        IntensityMatrixImpl intensityMatrix = new IntensityMatrixImpl(data.size()-1, data.get(0).length-1);
        List<Injection> injections = new ArrayList<>();
        List<Compound> compounds = new ArrayList<>();

        try {
            // process header
            String[] header = data.get(0);
            for (int i = 1; i < header.length; i++) {
                List<InjectionImpl> foundInjection = dataManager.getInjections().queryForEq("fileName", header[i]);
                if (foundInjection.size() != 1)
                    throw new RuntimeException(String.format("Injection value for %s is not found in the database", header[i]));
                injections.add(foundInjection.get(0));
            }

            // process data
            int rowNum = data.size()-1;
            int columnNum = header.length - 1;
            for (int i = 0; i < rowNum; i++) {
                String[] row = data.get(i+1);
                if (!row[0].startsWith("COMPOUND"))
                    throw new RuntimeException(String.format("Compound ID is invalid %s in line %d", row[0], i+2));
                CompoundImpl foundCompound = dataManager.getCompounds().queryForId(Integer.parseInt(row[0].substring("COMPOUND".length())));
                if (foundCompound == null)
                    throw new RuntimeException(String.format("Compound for %s is not found in the database in line %d", row[0], i+2));
                compounds.add(foundCompound);

                for (int j = 0; j < columnNum; j++) {
                    intensityMatrix.put(i, j, Double.parseDouble(row[j+1]));
                }
            }

            intensityMatrix.setColumnKeys(injections);
            intensityMatrix.setRowKeys(compounds);

            if (dataManager.getCompounds().queryForAll().size() != rowNum)
                throw new RuntimeException(String.format("The number of rows is not equal to # of compounds (%d != %d)", rowNum, dataManager.getCompounds().queryForAll().size()));

            if (dataManager.getInjections().queryForAll().size() != columnNum)
                throw new RuntimeException(String.format("The number of columns is not equal to # of injections (%d != %d)", columnNum, dataManager.getInjections().queryForAll().size()));

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return intensityMatrix;
    }


    @Override
    public List<Injection> getNormalInjections() {
        return getColumnKeys().stream().
                filter(injection -> injection.getSample().getSampleType() == Sample.SampleType.NORMAL).
                collect(Collectors.toList());
    }

    @Override
    public List<Injection> getInjectionsBySample(Sample sample) {
        return getColumnKeys().stream().
                filter(injection -> injection.getSample().equals(sample)).
                collect(Collectors.toList());
    }

    @Override
    public Map<Sample, List<Injection>> getQCInjections() {
        return getColumnKeys().stream().
                filter(injection -> injection.getSample().getSampleType() == Sample.SampleType.QC).
                collect(Collectors.groupingBy(Injection::getSample));
    }

    @Override
    public List<Sample> getGlobalQCSamples() {
        Set<Plate> allPlates = getColumnKeys().stream().
                map(Injection::getPlate).distinct().collect(Collectors.toSet());

        Map<Sample, List<Pair<Plate, Sample>>> qcInPlates = getColumnKeys().stream().
                filter(injection -> injection.getSample().getSampleType() == Sample.SampleType.QC).
                map(injection1 -> new Pair<>(injection1.getPlate(), injection1.getSample())).
                distinct().
                collect(Collectors.groupingBy(Pair::getSecond));

        return qcInPlates.entrySet().stream().
                filter(sampleListEntry ->
                                sampleListEntry.getValue().stream().map(Pair::getFirst).collect(Collectors.toSet()).equals(allPlates)
                ).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Map<Plate, List<Injection>> getInjectionsByPlate() {
        return getColumnKeys().stream().collect(Collectors.groupingBy(Injection::getPlate));
    }

    @Override
    public Map<Plate, Map<Sample.SampleType, List<Injection>>> getInjectionsByPlateAndType() {
        Map<Plate, List<Injection>> plateListMap = getInjectionsByPlate();
        Map<Plate, Map<Sample.SampleType, List<Injection>>> plateMapMap = new HashMap<>();
        for (Map.Entry<Plate, List<Injection>> entry : plateListMap.entrySet()) {
            plateMapMap.put(entry.getKey(), entry.getValue().stream().
            collect(Collectors.groupingBy(injection -> injection.getSample().getSampleType())));
        }
        return plateMapMap;
    }

    public void rehashKeys() {
        setColumnKeys(getColumnKeys());
        setRowKeys(getRowKeys());
    }

    @Value
    private class Pair<A, B> {
        private A first;
        private B second;
    }
}
