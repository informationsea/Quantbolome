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

package jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector;

import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.*;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoessIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LogarithmIntensityCorrector;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * metabolomeqc-next
 * Copyright (C) 2015 OKAMURA Yasunobu
 * Created on 15/07/26.
 */
public class LoessIntensityCorrectorTest {

    @Test
    public void createSampleLoessFlat() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);

        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();

        IntensityMatrixImpl intensityMatrix = new IntensityMatrixImpl(dataManager.getIntensityMatrix());
        List<Sample> globalQC = intensityMatrix.getGlobalQCSamples();
        if (globalQC.size() == 0) throw new UnsupportedOperationException("No Global QC");
        Sample selectedGlobalQC = globalQC.get(0);

        LoessInterpolator loessInterpolator = new LoessInterpolator();

        for (HashMap.Entry<Plate, List<Injection>> oneplate : intensityMatrix.getInjectionsByPlate().entrySet()) {
            for (Compound compound : intensityMatrix.getRowKeys()) {
                List<Double> xlist = new ArrayList<>();
                List<Double> ylist = new ArrayList<>();
                for (Injection injection : oneplate.getValue()) {
                    if (injection.isIgnored()) continue;
                    if (!injection.getSample().equals(selectedGlobalQC)) continue;
                    xlist.add((double) injection.getRunIndex());
                    ylist.add(intensityMatrix.get(compound, injection));
                }

                PolynomialSplineFunction fn =
                        loessInterpolator.interpolate(xlist.stream().mapToDouble(x -> x).toArray(),
                                ylist.stream().mapToDouble(x -> x).toArray());
                for (Injection injection : oneplate.getValue()) {
                    if (injection.getSample().getSampleType() != Sample.SampleType.NORMAL) continue;
                    intensityMatrix.put(compound, injection, fn.value(injection.getRunIndex()));
                }
            }
        }

        dataManager.setIntensityMatrix(intensityMatrix);
        CSVDataLoader.storeToCSVData(new File(testOutput, "loess-flat"), dataManager);
    }

    @Test
    public void testLoessIntensityCorrector() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);

        new LogarithmIntensityCorrector(2, 1).doCorrection(dataManager);
        new LoessIntensityCorrector().doCorrection(dataManager);
        IntensityMatrix corrected = dataManager.getIntensityMatrix();

        // write data
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);
        CSVDataLoader.storeToCSVData(new File(testOutput, "loess-intensity"), dataManager);

        // check data
        for (Injection oneSample : corrected.getColumnKeys()) {
            Assert.assertEquals(Sample.SampleType.NORMAL, oneSample.getSample().getSampleType());
        }

        /*
        int compoundIndex = 0;
        for (Compound compound : corrected.getRowKeys()) {
            double base = ((Number)corrected.getRow(compound)[0]).doubleValue();
            for (Injection sample : corrected.getColumnKeys()) {
                Assert.assertEquals(String.format("[%d,%d]%s/%s", compoundIndex, sample.getRunIndex(), sample.getName(), sample.getFileName()),
                        base, corrected.get(compound, sample), 0.0001);
            }
            compoundIndex += 1;
        }
        */
    }
}
