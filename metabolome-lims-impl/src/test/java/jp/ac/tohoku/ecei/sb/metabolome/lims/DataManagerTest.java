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

import com.j256.ormlite.dao.Dao;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DataManagerTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static DataManager prepareData(String jdbcUrl) throws Exception {
        DataManager dataManager = new DataManager(jdbcUrl);

        StudyImpl masterStudy = new StudyImpl("Master");
        dataManager.getStudies().create(masterStudy);

        SampleImpl[] samples = new SampleImpl[] {
                new SampleImpl(Sample.SampleType.QC, UUID.fromString("E4EDC22F-869E-48E7-A7C7-5C50FC8939D8"), "GlobalQC"), // 0
                new SampleImpl(Sample.SampleType.QC, UUID.fromString("24D1C34F-C6B9-45CC-BF00-F694F48DFB8F"), "LocalQC1"), // 1
                new SampleImpl(Sample.SampleType.QC, UUID.fromString("2DE640A4-C282-40E5-A133-D7E7CB47B5EF"), "LocalQC2"), // 2

                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("4207232B-B378-49A1-B572-6E2206133B75"), "Cohort1"), // 3
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("686330FE-AED9-405C-8FBD-7E05664BBF35"), "Cohort2"), // 4
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("BD427562-1C89-43CA-97D4-9C75A6A1CAA4"), "Cohort3"), // 5
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("68A0B9BA-5E0E-4CA5-8090-B1F682918A8C"), "Cohort4"), // 6
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("765DA956-AE57-4C19-9742-848D82BE4707"), "Cohort5"), // 7
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("3A726EFC-B0EF-4A8A-9990-F759BA786D62"), "Cohort6"), // 8

                new SampleImpl(Sample.SampleType.BLANK, UUID.fromString("C7772A67-3345-4444-A3AF-14D8A0FB77F4"), "Blank"), // 9
                new SampleImpl(Sample.SampleType.UNKNOWN, UUID.fromString("E40BA6AE-9D2A-4D69-A9F3-A1CBB0AFFE4D"), "Unknown"), // 10
        };
        for (SampleImpl one : samples)
            dataManager.getSamples().create(one);

        PlateImpl[] plates = new PlateImpl[] {
                new PlateImpl(masterStudy, "First", DATE_FORMAT.parse("2015-06-02 16:50:36.058")),
                new PlateImpl(masterStudy, "Second", DATE_FORMAT.parse("2015-06-02 16:50:36.058")),
        };
        for (PlateImpl one : plates)
            dataManager.getPlates().create(one);

        InjectionImpl[] injections = new InjectionImpl[] {
                new InjectionImpl(plates[0], null, "SQC1_1", samples[0], 0, true, 1),
                new InjectionImpl(plates[0], null, "SQC2_1", samples[0], 1, true, 2),
                new InjectionImpl(plates[0], null, "SQC3_1", samples[0], 2, true, 3),
                new InjectionImpl(plates[0], null, "SQC4_1", samples[0], 3, true, 4),
                new InjectionImpl(plates[0], null, "SQC5_1", samples[0], 4, false, 5),
                new InjectionImpl(plates[0], null, "RQC5_1", samples[1], 5, false, 5),
                new InjectionImpl(plates[0], null, "Cohort1", samples[3], 6, false, null),
                new InjectionImpl(plates[0], null, "Cohort2", samples[4], 7, false, null),
                new InjectionImpl(plates[0], null, "SQC6_1", samples[0], 8, false, 6),
                new InjectionImpl(plates[0], null, "RQC6_1", samples[1], 9, false, 6),
                new InjectionImpl(plates[0], null, "Cohort3", samples[5], 10, false, null),
                new InjectionImpl(plates[0], null, "SQC7_1", samples[0], 11, false, 7),
                new InjectionImpl(plates[0], null, "RQC7_1", samples[1], 12, false, 7),
                new InjectionImpl(plates[0], null, "BK1", samples[9], 13, false, null),
                new InjectionImpl(plates[0], null, "Unknown", samples[10], 14, false, null),

                new InjectionImpl(plates[1], null, "SQC1_2", samples[0], 15, true, 1),
                new InjectionImpl(plates[1], null, "SQC2_2", samples[0], 16, true, 2),
                new InjectionImpl(plates[1], null, "SQC3_2", samples[0], 17, true, 3),
                new InjectionImpl(plates[1], null, "SQC4_2", samples[0], 18, true, 4),
                new InjectionImpl(plates[1], null, "SQC5_2", samples[0], 19, false, 5),
                new InjectionImpl(plates[1], null, "RQC5_2", samples[2], 20, false, 5),
                new InjectionImpl(plates[1], null, "Cohort4", samples[6], 21, false, null),
                new InjectionImpl(plates[1], null, "Cohort5", samples[7], 22, false, null),
                new InjectionImpl(plates[1], null, "SQC6_2", samples[0], 23, false, 6),
                new InjectionImpl(plates[1], null, "RQC6_2", samples[2], 24, false, 6),
                new InjectionImpl(plates[1], null, "Cohort6", samples[8], 25, false, null),
                new InjectionImpl(plates[1], null, "SQC7_2", samples[0], 26, false, 7),
                new InjectionImpl(plates[1], null, "RQC7_2", samples[2], 27, false, 7),
                new InjectionImpl(plates[1], null, "BK2", samples[9], 28, false, null),
        };
        for (InjectionImpl one : injections)
            dataManager.getInjections().create(one);

        CompoundImpl[] compounds = new CompoundImpl[] {
                new CompoundImpl(10, 20, null, 1),
                new CompoundImpl(12, 22, 11., 1),
                new CompoundImpl(13, 29, null, 2),
        };
        for (CompoundImpl one : compounds)
            dataManager.getCompounds().create(one);

        IntensityMatrixImpl intensityMatrix = new IntensityMatrixImpl(compounds.length, injections.length);
        intensityMatrix.setColumnKeys(Arrays.asList(injections));
        intensityMatrix.setRowKeys(Arrays.asList(compounds));
        for (int i = 0; i < compounds.length; i++) {
            for (int j = 0; j < injections.length; j++) {
                intensityMatrix.put(i, j, (double)(i+j));
            }
        }
        dataManager.setIntensityMatrix(intensityMatrix);

        return dataManager;
    }

    @Test
    public void testDataManager() throws Exception {
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        File testDatabase = new File(testOutput, "data-manager-test.h2");
        File testDatabaseWithSuffix = new File(testOutput, "data-manager-test.h2" + ".mv.db");
        if (testDatabaseWithSuffix.exists())
            testDatabaseWithSuffix.delete();

        DataManager dataManager = prepareData("jdbc:h2:" + testDatabase.getAbsolutePath());


        File testXlsx = new File(testOutput, "data-manager-test-csv");
        CSVDataLoader.storeToCSVData(testXlsx, dataManager);
        dataManager.close();

        DataManager loadedDataManager = CSVDataLoader.loadFromCSVData(testXlsx);

        Assert.assertTrue(checkEqualData(dataManager.getInjections(), loadedDataManager.getInjections()));
        Assert.assertTrue(checkEqualData(dataManager.getCompounds(), loadedDataManager.getCompounds()));
        Assert.assertTrue(checkEqualData(dataManager.getPlates(), loadedDataManager.getPlates()));
        Assert.assertTrue(checkEqualData(dataManager.getSamples(), loadedDataManager.getSamples()));
        Assert.assertTrue(checkEqualData(dataManager.getStudies(), loadedDataManager.getStudies()));
        Assert.assertEquals(dataManager.getIntensityMatrix(), loadedDataManager.getIntensityMatrix());
    }

    public static <T> boolean checkEqualData(Dao<T, Integer> data1, Dao<T, Integer> data2) throws SQLException {
        List<T> list1 =  data1.query(data1.queryBuilder().orderBy("ID", false).prepare());
        List<T> list2 =  data2.query(data2.queryBuilder().orderBy("ID", false).prepare());
        log.info("Compare {} {}", list1, list2);

        return list1.equals(list2);
    }
}