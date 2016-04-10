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

import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.OperationHistoryImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVDataLoader {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final String INJECTION_FILE = "injection.csv";
    public static final String SAMPLE_FILE = "sample.csv";
    public static final String STUDY_FILE = "study.csv";
    public static final String PLATE_FILE = "plate.csv";
    public static final String COMPOUND_FILE = "compound.csv";
    public static final String INTENSITY_FILE = "intensity.csv";
    public static final String HISTORY_FILE = "history.csv";

    public static final String INJECTION_HUMAN_FRIENDLY = "injection-human-friendly.csv";

    public static final String[] MAIN_FILENAMES = new String[]{
            STUDY_FILE, PLATE_FILE, SAMPLE_FILE, INJECTION_FILE, COMPOUND_FILE, INTENSITY_FILE
    };

    public static final String[] ADDITIONAL_FILENAMES = new String[] {
            HISTORY_FILE, INJECTION_HUMAN_FRIENDLY
    };


    public static void storeToCSVData(File csvDirPath, DataManager dataManager) throws SQLException, IOException {
        OperationHistoryImpl operationHistory = new OperationHistoryImpl(CSVDataLoader.class, "storeToCSVData");
        operationHistory.setAttribute("Export Dir", csvDirPath.getAbsolutePath());
        dataManager.getOperationHistories().create(operationHistory);

        csvDirPath.mkdirs();
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Injection')", new File(csvDirPath, INJECTION_FILE).getAbsolutePath());
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Sample')", new File(csvDirPath, SAMPLE_FILE).getAbsolutePath());
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Study')", new File(csvDirPath, STUDY_FILE).getAbsolutePath());
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Plate')", new File(csvDirPath, PLATE_FILE).getAbsolutePath());
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Compound')", new File(csvDirPath, COMPOUND_FILE).getAbsolutePath());
        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM History')", new File(csvDirPath, HISTORY_FILE).getAbsolutePath());

        dataManager.getInjections().executeRaw("CALL CSVWRITE(?, 'SELECT * FROM Injection LEFT OUTER JOIN Sample ON Injection.Sample_ID = Sample.ID')", new File(csvDirPath, INJECTION_HUMAN_FRIENDLY).getAbsolutePath());

        if (dataManager.getIntensityMatrix() != null) {
            try (Writer writer = new FileWriter(new File(csvDirPath, INTENSITY_FILE))) {
                dataManager.getIntensityMatrix().writeCSV(writer);
            }
        }

        try (OutputStream outputStream = new FileOutputStream(new File(csvDirPath, "README.md"))) {
            try (InputStream inputStream = CSVDataLoader.class.getResourceAsStream("README.md")) {
                copyData(inputStream, outputStream);
            }
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.printf("Exported date: %s\n", DATE_FORMAT.format(new Date()));
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(csvDirPath, "sha256sum.txt")))) {
            for (String one : new String[]{INJECTION_FILE, SAMPLE_FILE, STUDY_FILE, PLATE_FILE, COMPOUND_FILE, INJECTION_HUMAN_FRIENDLY, INTENSITY_FILE, HISTORY_FILE, "README.md"}) {
                if (!new File(csvDirPath, one).isFile())
                    continue;

                try (InputStream inputStream = new FileInputStream(new File(csvDirPath, one))) {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

                    byte[] buffer = new byte[1024];
                    int readBytes;
                    do {
                        readBytes = inputStream.read(buffer);
                        if (readBytes > 0)
                            messageDigest.update(buffer, 0, readBytes);
                    } while (readBytes == buffer.length);


                    byte[] digest = messageDigest.digest();
                    for (byte b : digest) {
                        writer.printf("%02x", b);
                    }
                    writer.printf("  %s\n", one);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataManager loadFromCSVData(File csvDirPath) throws SQLException, IOException {
        return loadFromCSVData(csvDirPath, new DataManager());
    }

    public static DataManager loadFromCSVData(File csvDirPath, DataManager dataManager) throws SQLException, IOException {
        for (String one : MAIN_FILENAMES) {
            File expectedFile = new File(csvDirPath, one);
            if (!expectedFile.isFile())
                throw new FileNotFoundException(expectedFile.getAbsolutePath());
        }

        dataManager.getInjections().executeRaw("INSERT INTO Study SELECT * FROM CSVREAD('"+new File(csvDirPath, STUDY_FILE).getAbsolutePath().replace("'", "\\'")+"')");
        dataManager.getInjections().executeRaw("INSERT INTO Plate SELECT * FROM CSVREAD('" + new File(csvDirPath, PLATE_FILE).getAbsolutePath().replace("'", "\\'")+"')");
        dataManager.getInjections().executeRaw("INSERT INTO Sample SELECT * FROM CSVREAD('" + new File(csvDirPath, SAMPLE_FILE).getAbsolutePath().replace("'", "\\'")+"')");
        dataManager.getInjections().executeRaw("INSERT INTO Injection SELECT * FROM CSVREAD('" + new File(csvDirPath, INJECTION_FILE).getAbsolutePath().replace("'", "\\'")+"')");
        dataManager.getInjections().executeRaw("INSERT INTO History SELECT * FROM CSVREAD('" + new File(csvDirPath, HISTORY_FILE).getAbsolutePath().replace("'", "\\'")+"')");

        dataManager.getInjections().executeRaw("INSERT INTO Compound SELECT * FROM CSVREAD('" + new File(csvDirPath, COMPOUND_FILE).getAbsolutePath().replace("'", "\\'")+"')");
        try (Reader reader = new FileReader(new File(csvDirPath, INTENSITY_FILE))) {
            dataManager.setIntensityMatrix(IntensityMatrixImpl.loadFromCSV(reader, dataManager));
        }

        OperationHistoryImpl operationHistory = new OperationHistoryImpl(CSVDataLoader.class, "loadFromCSVData");
        operationHistory.setAttribute("Load Dir", csvDirPath.getAbsolutePath());
        dataManager.getOperationHistories().create(operationHistory);
        return dataManager;
    }


    public static void copyData(InputStream is, OutputStream os) throws IOException {
        byte[] data = new byte[1024*100];
        do {
            int readBytes = is.read(data);
            if (readBytes < 0) break;
            os.write(data, 0, readBytes);
        } while (true);
    }
}
