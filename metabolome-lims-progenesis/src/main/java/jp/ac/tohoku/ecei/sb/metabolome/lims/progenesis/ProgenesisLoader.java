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

package jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis;

import au.com.bytecode.opencsv.CSVReader;
import info.informationsea.tableio.TableCell;
import info.informationsea.tableio.TableReader;
import info.informationsea.tableio.TableRecord;
import info.informationsea.tableio.csv.TableCSVReader;
import info.informationsea.tableio.excel.ExcelSheetReader;
import info.informationsea.tableio.impl.TableCellHelper;
import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.*;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yasu on 15/06/02.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) @Slf4j
public class ProgenesisLoader {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static XSSFWorkbook createTemplateXlsxFile(InputStream is) throws IOException, InvalidSampleInfoFormatException{
        XSSFWorkbook xlsx = new XSSFWorkbook(ProgenesisLoader.class.getResourceAsStream("metabolome-dataset-template.xlsx"));
        Sheet injectionSheet = xlsx.getSheet("Injection");
        int numberOfInjections = 0;
        
        try (Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);
            String[] header1 = csvReader.readNext();
            String[] header2 = csvReader.readNext();
            String[] header3 = csvReader.readNext();

            int dataOffset1 = Arrays.asList(header1).indexOf("Raw abundance");
            if (dataOffset1 < 0)
                throw new InvalidSampleInfoFormatException("\"Raw abundance\" is not found in the first line. May be invalid progenesis file");

            int dataOffset2 = Arrays.asList(header2).indexOf("Tags");
            if (dataOffset2 < 0 || dataOffset1 >= dataOffset2)
                throw new InvalidSampleInfoFormatException("\"Tags\" is not found in the second line. May be invalid progenesis file");

            List<String> filenameList = Arrays.asList(header3).subList(dataOffset1, dataOffset2);
            numberOfInjections = filenameList.size();

            for (int i = 0; i < numberOfInjections; i++) {
                Row row = injectionSheet.createRow(i+1);
                row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(i+1);
                row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(filenameList.get(i));
            }
            injectionSheet.autoSizeColumn(0);
            injectionSheet.autoSizeColumn(3);
        }

        {
            // Create plate template
            Sheet plateSheet = xlsx.getSheet("Plate");
            for (int i = 0; i < Math.ceil(numberOfInjections / 117.); i++) {
                Row row = plateSheet.createRow(1+i);
                row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(1+i);
                row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue("Sample plate "+(i+1));
                row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(DATE_FORMAT.format(new Date()));
            }

            for (int i = 0; i < 3; i++)
                plateSheet.autoSizeColumn(i);
        }

        {
            // Create sample template
            Sheet sampleSheet = xlsx.getSheet("Sample");
            Row blankRow = sampleSheet.createRow(1);
            blankRow.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(1);
            blankRow.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("C7772A67-3345-4444-A3AF-14D8A0FB77F4");
            blankRow.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("BLANK");
            blankRow.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("blank");

            for (int i = 0; i < 2; i++) {
                Row qcRow = sampleSheet.createRow(2+i);
                qcRow.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(2+i);
                qcRow.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("QC");
                qcRow.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("Quality Control "+(i+1));
            }

            for (int i = 0; i < 4; i++)
                sampleSheet.autoSizeColumn(i);
        }

        return xlsx;
    }

    public static DataManager loadFromProgenesisAndSampleInfo(InputStream progenesisCSV, InputStream sampleInfoXlsx) throws IOException, SQLException, InvalidSampleInfoFormatException {
        XSSFWorkbook sampleInfo = new XSSFWorkbook(sampleInfoXlsx);
        DataManager dataManager = new DataManager();

        OperationHistoryImpl operationHistory = new OperationHistoryImpl(ProgenesisLoader.class, "loadFromProgenesisAndSampleInfo");
        dataManager.getOperationHistories().create(operationHistory);


        // Start loading
        int processingLine = 1;

        StudyImpl study;

        try {
            // Load study
            Sheet studySheet = sampleInfo.getSheet("Study");
            ExcelSheetReader sheetReader = new ExcelSheetReader(studySheet);
            List<TableCell[]> data = sheetReader.readAll();
            if (!data.get(6)[0].toString().equals("Study name"))
                throw new InvalidSampleInfoFormatException("Column A, Row 7 should be \"Study name\"");
            if (!data.get(7)[0].toString().equals("Comment"))
                throw new InvalidSampleInfoFormatException("Column A, Row 8 should be \"Comment\"");

            study = new StudyImpl(data.get(6)[1].toString());
            String comment = data.get(7)[1].toString();
            if (!comment.isEmpty()) {
                study.setAttribute("COMMENT", comment);
            }

            dataManager.getStudies().create(study);
        } catch (ArrayIndexOutOfBoundsException|NullPointerException e) {
            throw new InvalidSampleInfoFormatException("Cannot load study sheet", e);
        }

        try {
            // Load plate
            Sheet plateSheet = sampleInfo.getSheet("Plate");
            ExcelSheetReader sheetReader = new ExcelSheetReader(plateSheet);
            sheetReader.setUseHeader(true);
            if (!Arrays.equals(sheetReader.getHeader(), new String[]{"Plate ID", "Name", "Run date", "Comment"}))
                throw new InvalidSampleInfoFormatException("Invalid header of plate sheet");

            processingLine = 2;
            for (TableRecord record : sheetReader) {
                PlateImpl plate = new PlateImpl();
                plate.setStudy(study);
                plate.setName(record.get("Name").toString());
                if (record.get("Run date").toString().length() > 0)
                    plate.setDateTime(CSVDataLoader.DATE_FORMAT.parse(record.get("Run date").toString()));
                plate.setId((int)record.get("Plate ID").toNumeric());


                String comment = record.get("Comment").toString();
                if (!comment.isEmpty()) {
                    plate.setAttribute("COMMENT", comment);
                }

                dataManager.getPlates().create(plate);
                processingLine += 1;
            }
        } catch (ArrayIndexOutOfBoundsException|NullPointerException|ParseException|NumberFormatException e) {
            throw new InvalidSampleInfoFormatException(String.format("Cannot load plate sheet  line:%d", processingLine), e);
        }

        try {
            processingLine = 2;
            // Load plate
            Sheet sampleSheet = sampleInfo.getSheet("Sample");
            ExcelSheetReader sheetReader = new ExcelSheetReader(sampleSheet);
            sheetReader.setUseHeader(true);
            if (!Arrays.equals(sheetReader.getHeader(), new String[]{"Sample ID", "UUID", "Sample Type", "Name", "Comment"}))
                throw new InvalidSampleInfoFormatException("Invalid header of sample sheet");

            for (TableRecord record: sheetReader) {
                SampleImpl sample = new SampleImpl();
                sample.setName(record.get("Name").toString());
                sample.setId((int) record.get("Sample ID").toNumeric());
                sample.setSampleType(Sample.SampleType.valueOf(record.get("Sample Type").toString()));

                String uuidString = record.get("UUID").toString();
                UUID uuid;
                if (uuidString.isEmpty())
                    uuid = UUID.randomUUID();
                else
                    uuid = UUID.fromString(uuidString);
                sample.setUuid(uuid);

                String comment = record.get("Comment").toString();
                if (!comment.isEmpty()) {
                    sample.setAttribute("COMMENT", comment);
                }

                dataManager.getSamples().create(sample);
                processingLine += 1;
            }
        } catch (ArrayIndexOutOfBoundsException|NullPointerException|IllegalArgumentException e) {
            throw new InvalidSampleInfoFormatException(String.format("Cannot load sample sheet   line: %d", processingLine), e);
        }


        try {
            // Load injection
            Sheet injectionSheet = sampleInfo.getSheet("Injection");
            ExcelSheetReader sheetReader = new ExcelSheetReader(injectionSheet);
            sheetReader.setUseHeader(true);
            if (!Arrays.equals(new String[]{"Run Index", "Plate ID", "Sample ID", "FileName", "Name", "QC Index", "Ignored", "Dilution Factor", "Comment"}, sheetReader.getHeader()))
                throw new InvalidSampleInfoFormatException("Invalid header of injection sheet");
            processingLine = 2;

            for (TableRecord row : sheetReader) {
                log.info("Injection {} {}", processingLine, row.getContent());
                InjectionImpl injection = new InjectionImpl();

                int runIndex = (int)Double.parseDouble(row.get("Run Index").toString());
                injection.setId(runIndex);
                injection.setRunIndex(runIndex);

                PlateImpl plate = dataManager.getPlates().queryForId((int) row.get("Plate ID").toNumeric());
                if (plate == null) throw new InvalidSampleInfoFormatException(String.format("Invalid Plate ID; Injection sheet; line: %d", processingLine));
                injection.setPlate(plate);

                try {
                    SampleImpl sample = dataManager.getSamples().queryForId((int) row.get("Sample ID").toNumeric());
                    if (sample == null)
                        throw new InvalidSampleInfoFormatException(String.format("Invalid Sample ID; Injection sheet; line: %s", processingLine));
                    injection.setSample(sample);
                    if (sample.getSampleType() == Sample.SampleType.QC)
                        injection.setQCIndex((int)row.get("QC Index").toNumeric());

                } catch (NumberFormatException e) {
                    SampleImpl sample = new SampleImpl(Sample.SampleType.NORMAL, UUID.randomUUID(), "Normal "+processingLine);
                    dataManager.getSamples().create(sample);
                    injection.setSample(sample);
                }

                injection.setName(row.get("Name").toString());
                injection.setFileName(row.get("FileName").toString());
                injection.setIgnored(row.get("Ignored").toBoolean());

                String dilutionFactor = row.get("Dilution Factor").toString();
                try {
                    injection.setAttribute("DILUTION_FACTOR", Double.parseDouble(dilutionFactor));
                } catch (NumberFormatException e) {
                    if (injection.getSample().getSampleType() == Sample.SampleType.QC) {
                        injection.setAttribute("DILUTION_FACTOR", 1.0);
                    }
                }

                String comment = row.get("Comment").toString();
                if (!comment.isEmpty()) {
                    injection.setAttribute("COMMENT", comment);
                }

                dataManager.getInjections().create(injection);
                processingLine += 1;
            }

        } catch (ArrayIndexOutOfBoundsException|NullPointerException|NumberFormatException e) {
            throw new InvalidSampleInfoFormatException(String.format("Cannot load injection sheet  line: %d", processingLine), e);
        }


        try {
            // Load Progenesis File
            processingLine = 1;
            TableReader reader = new TableCSVReader(new InputStreamReader(progenesisCSV));
            List<TableCell[]> data = reader.readAll();

            int dataOffset1 = Arrays.asList(TableCellHelper.convertFromTableCell(data.get(0))).indexOf("Raw abundance");
            if (dataOffset1 < 0)
                throw new InvalidSampleInfoFormatException("\"Raw abundance\" is not found in the first line. May be invalid progenesis file");

            int dataOffset2 = Arrays.asList(TableCellHelper.convertFromTableCell(data.get(1))).indexOf("Tags");
            if (dataOffset2 < 0 || dataOffset1 >= dataOffset2)
                throw new InvalidSampleInfoFormatException("\"Tags\" is not found in the second line. May be invalid progenesis file");

            List<String> filenameList = Stream.of(data.get(2)).map(TableCell::toString).collect(Collectors.toList()).subList(dataOffset1, dataOffset2);
            int numberOfFilename = filenameList.size();
            List<String> filenamesInInjectionTable = dataManager.getInjections().queryForAll().stream().map(InjectionImpl::getFileName).collect(Collectors.toList());
            List<String> missingFilenameInProgenesis = filenamesInInjectionTable.stream().filter(s -> !filenameList.contains(s)).collect(Collectors.toList());
            List<String> missingFilenameInInjectionTable = filenameList.stream().filter(s -> !filenamesInInjectionTable.contains(s)).collect(Collectors.toList());

            if (missingFilenameInInjectionTable.size() > 0) {
                throw new InvalidSampleInfoFormatException(
                        String.format("%d injections are missing in the injection table. They are %s",
                        missingFilenameInInjectionTable.size(), Arrays.toString(missingFilenameInInjectionTable.toArray())));
            }

            if (missingFilenameInProgenesis.size() > 0) {
                throw new InvalidSampleInfoFormatException(
                        String.format("%d injections are missing in the progenesis CSV. They are %s",
                                missingFilenameInProgenesis.size(), Arrays.toString(missingFilenameInProgenesis.toArray())));
            }

            final int PROGENESIS_ROW_OFFSET = 3;
            IntensityMatrixImpl marix = new IntensityMatrixImpl(data.size()-PROGENESIS_ROW_OFFSET, filenameList.size());

            if (!Arrays.equals(
                    new String[]{"Compound", "Neutral mass (Da)", "m/z", "Charge", "Retention time (min)"},
                    Stream.of(data.get(2)).map(TableCell::toString).limit(5).toArray())) {
                throw new InvalidSampleInfoFormatException("Invalid Header of Compound Information");
            }

            List<Compound> compounds = new ArrayList<>();
            for (int i = PROGENESIS_ROW_OFFSET; i < data.size(); i++) {
                TableCell[] row = data.get(i);
                CompoundImpl oneCompound = new CompoundImpl(row[2].toNumeric(), row[4].toNumeric(), row[1].toString().isEmpty() ? null : row[1].toNumeric(), (int)row[3].toNumeric());
                compounds.add(oneCompound);
                dataManager.getCompounds().create(oneCompound);
            }

            List<Injection> injections = filenameList.stream().map(f -> {
                try {
                    List<InjectionImpl> oneInjection = dataManager.getInjections().queryForEq("FileName", f);
                    if (oneInjection.size() == 1)
                        return oneInjection.get(0);
                    else
                        throw new RuntimeException(String.format("Cannot find injection for %s", f));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            Injection[] progenesisCSVInjection = injections.stream().toArray(Injection[]::new);
            injections.sort((x, y) -> Integer.compare(x.getRunIndex(), y.getRunIndex()));

            marix.setRowKeys(compounds);
            marix.setColumnKeys(injections);

            processingLine = PROGENESIS_ROW_OFFSET;
            for (int i = PROGENESIS_ROW_OFFSET; i < data.size(); i++) {
                TableCell[] row = data.get(i);
                for (int j = 0; j < numberOfFilename; j++) {
                    marix.put(compounds.get(i - PROGENESIS_ROW_OFFSET), progenesisCSVInjection[j], row[j + dataOffset1].toNumeric());
                }
            }
            dataManager.setIntensityMatrix(marix);
        } catch (RuntimeException e) {
            throw new InvalidSampleInfoFormatException(String.format("Cannot load intensity matrix line: %d", processingLine), e);
        }

        return dataManager;
    }

    private static String stringOrEmpty(Object str) {
        if (str == null)
            return "";
        return str.toString();
    }
}
