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

import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.OperationHistoryImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by yasu on 15/07/24.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ZipDataLoader {
    public static DataManager loadFromZip(File file) throws IOException, SQLException {
        Path tempDir = Files.createTempDirectory("metabolome-lims");
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        HashSet<String> createdFiles = new HashSet<>();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().contains("/")) continue;

            createdFiles.add(entry.getName());
            try (OutputStream os = new FileOutputStream(new File(tempDir.toFile(), entry.getName())); InputStream is = zipFile.getInputStream(entry)) {
                CSVDataLoader.copyData(is, os);
            }
        }
        DataManager dataManager = CSVDataLoader.loadFromCSVData(tempDir.toFile());

        OperationHistoryImpl operationHistory = new OperationHistoryImpl(ZipDataLoader.class, "loadFromZip");
        operationHistory.setAttribute("Load Dir", file.getAbsolutePath());
        dataManager.getOperationHistories().create(operationHistory);

        for (String one : createdFiles) {
            File f = new File(tempDir.toFile(), one);
            if (!f.delete()) {
                log.error("Cannot delete file {}", f);
            }
        }
        Files.delete(tempDir);

        return dataManager;
    }

    public static void storeToZip(File file, DataManager dataManager) throws IOException, SQLException {
        OperationHistoryImpl operationHistory = new OperationHistoryImpl(CSVDataLoader.class, "storeToZip");
        operationHistory.setAttribute("Export Dir", file.getAbsolutePath());
        dataManager.getOperationHistories().create(operationHistory);

        Path tempDir = Files.createTempDirectory("metabolome-lims");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
            CSVDataLoader.storeToCSVData(tempDir.toFile(), dataManager);
            Files.walk(tempDir, 1).forEachOrdered(p -> {
                if (!Files.isReadable(p) || !Files.isRegularFile(p)) return;
                try (InputStream is = Files.newInputStream(p)) {
                    zipOutputStream.putNextEntry(new ZipEntry(p.getFileName().toString()));
                    CSVDataLoader.copyData(is, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                    new RuntimeException(e);
                }
            });
            zipOutputStream.finish();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw new IOException(e.getCause());
            }
            throw e;
        }

    }
}
