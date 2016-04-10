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

import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by yasu on 15/06/02.
 */
public class ProgenesisLoaderTest {

    @Test
    public void createTemplateXlsxFile() throws Exception {
        Workbook workbook = ProgenesisLoader.createTemplateXlsxFile(getClass().getResourceAsStream("random-progenesis.csv"));

        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        File templateXlsx = new File(testOutput, "generated-template.xlsx");

        try (OutputStream os = new FileOutputStream(templateXlsx)) {
            workbook.write(os);
        }
    }

    @Test
    public void loadFromProgenesisAndSampleInfo() throws Exception {
        DataManager dataManager = ProgenesisLoader.loadFromProgenesisAndSampleInfo(
                getClass().getResourceAsStream("random-progenesis.csv"),
                getClass().getResourceAsStream("random-sampleinfo.xlsx")
        );

        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        File importedDir = new File(testOutput, "progenesis-imported");
        importedDir.mkdirs();

        CSVDataLoader.storeToCSVData(importedDir, dataManager);
    }
}
