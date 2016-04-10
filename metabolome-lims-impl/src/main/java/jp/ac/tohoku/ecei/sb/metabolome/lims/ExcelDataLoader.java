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

package jp.ac.tohoku.ecei.sb.metabolome.lims;

import info.informationsea.tableio.TableRecord;
import info.informationsea.tableio.csv.TableCSVReader;
import info.informationsea.tableio.csv.TableCSVWriter;
import info.informationsea.tableio.excel.ExcelSheetReader;
import info.informationsea.tableio.excel.ExcelSheetWriter;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.OperationHistoryImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by yasu on 15/07/25.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelDataLoader {

    private static final Pattern doublePattern = Pattern.compile("\\d+\\.?\\d*");

    public static void storeToExcel(File excelFile, DataManager dataManager) throws IOException, SQLException {
        OperationHistoryImpl operationHistory = new OperationHistoryImpl(CSVDataLoader.class, "storeToExcelData");
        operationHistory.setAttribute("Export Dir", excelFile.getAbsolutePath());
        dataManager.getOperationHistories().create(operationHistory);

        Path tempDir = Files.createTempDirectory("metabolome");
        CSVDataLoader.storeToCSVData(tempDir.toFile(), dataManager);

        XSSFWorkbook workbook = new XSSFWorkbook();

        for (String one : CSVDataLoader.MAIN_FILENAMES) {
            copyCSV2Excel(tempDir, workbook, one);
        }

        for (String one : CSVDataLoader.ADDITIONAL_FILENAMES) {
            copyCSV2Excel(tempDir, workbook, one);
        }

        try (FileOutputStream os = new FileOutputStream(excelFile)) {
            workbook.write(os);
        }
    }

    // Cannot pass test
    /*
    public static DataManager loadFromExcel(File excelFile) throws IOException, SQLException, InvalidFormatException {
        Path tempDir = Files.createTempDirectory("metabolome");
        XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String sheetName = workbook.getSheetName(i);
            ExcelSheetReader reader = new ExcelSheetReader(workbook.getSheetAt(i));
            try (FileWriter writer = new FileWriter(new File(tempDir.toFile(), sheetName+".csv"))) {
                TableCSVWriter csvWriter = new TableCSVWriter(writer);
                for (TableRecord record : reader) {
                    csvWriter.printRecord((Object[]) record.getContent());
                }
            }
        }
        return CSVDataLoader.loadFromCSVData(tempDir.toFile());
    }
    */

    private static void copyCSV2Excel(Path tempDir, XSSFWorkbook workbook, String one) throws IOException {
        String sheetName = one;
        if (sheetName.endsWith(".csv")) sheetName = one.substring(0, one.length()-4);
        ExcelSheetWriter sheetWriter = new ExcelSheetWriter(workbook.createSheet(sheetName));
        sheetWriter.setPrettyTable(true);
        sheetWriter.setAutoFilter(true);
        sheetWriter.setAutoResizeColumn(true);
        try (FileReader reader = new FileReader(new File(tempDir.toFile(), one))) {
            TableCSVReader csvReader = new TableCSVReader(reader);
            for (TableRecord record : csvReader) {
                sheetWriter.printRecord(Stream.of((Object[]) record.getContent()).map(x -> {
                    Matcher matcher = doublePattern.matcher(x.toString());
                    if (matcher.matches()) {
                        return Double.parseDouble(x.toString());
                    }
                    return x;
                }).toArray());
            }
        }
        try {
            sheetWriter.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


}
