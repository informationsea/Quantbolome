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

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class CSVDataLoaderTest {

    DataManager referenceDataManager;

    @Before
    public void setUp() throws Exception {
        referenceDataManager = DataManagerTest.prepareData("jdbc:h2:mem:");
    }

    @Test
    public void testStoreToCSVData() throws Exception {
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        CSVDataLoader.storeToCSVData(new File(testOutput, "test-csv-output"), referenceDataManager);

        DataManager dataManager = CSVDataLoader.loadFromCSVData(new File(testOutput, "test-csv-output"));

        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getInjections(), dataManager.getInjections()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getCompounds(), dataManager.getCompounds()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getPlates(), dataManager.getPlates()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getSamples(), dataManager.getSamples()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getStudies(), dataManager.getStudies()));
        Assert.assertEquals(referenceDataManager.getIntensityMatrix(), dataManager.getIntensityMatrix());
    }

    @Test
    public void testLoadFromCSVData() throws Exception {

        log.info("{}", getClass().getResource("dataset1").getFile());

        DataManager dataManager = CSVDataLoader.loadFromCSVData(new File(getClass().getResource("dataset1").getFile()));

        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getInjections(), dataManager.getInjections()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getCompounds(), dataManager.getCompounds()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getPlates(), dataManager.getPlates()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getSamples(), dataManager.getSamples()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getStudies(), dataManager.getStudies()));
        Assert.assertEquals(referenceDataManager.getIntensityMatrix(), dataManager.getIntensityMatrix());
    }

    @Test
    public void testLoadFromZipData() throws Exception {
        DataManager dataManager = ZipDataLoader.loadFromZip(new File(getClass().getResource("dataset1.zip").getFile()));

        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getInjections(), dataManager.getInjections()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getCompounds(), dataManager.getCompounds()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getPlates(), dataManager.getPlates()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getSamples(), dataManager.getSamples()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getStudies(), dataManager.getStudies()));
        Assert.assertEquals(referenceDataManager.getIntensityMatrix(), dataManager.getIntensityMatrix());
    }

    @Test
    public void testStoreToZip() throws Exception {
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        ZipDataLoader.storeToZip(new File(testOutput, "test.zip"), referenceDataManager);
    }

    @Test
    public void testStoreToExcel() throws IOException, SQLException {
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        ExcelDataLoader.storeToExcel(new File(testOutput, "test.xlsx"), referenceDataManager);

        /*
        DataManager dataManager = ExcelDataLoader.loadFromExcel(new File(testOutput, "test.xlsx"));

        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getInjections(), dataManager.getInjections()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getCompounds(), dataManager.getCompounds()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getPlates(), dataManager.getPlates()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getSamples(), dataManager.getSamples()));
        Assert.assertTrue(DataManagerTest.checkEqualData(referenceDataManager.getStudies(), dataManager.getStudies()));
        Assert.assertEquals(referenceDataManager.getIntensityMatrix(), dataManager.getIntensityMatrix());
        */
    }
}