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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.InjectionImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LinearIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LogarithmIntensityCorrector;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/01
 */
public class LinearIntensityCorrectorTest {

    @Test
    public void testLinearIntensityCorrector() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);
        IntensityMatrix mim = dataManager.getIntensityMatrix();
        IntensityMatrixImpl subset = new IntensityMatrixImpl(mim, 0, mim.getSize()[0], 0, 107+117);
        dataManager.setIntensityMatrix(subset);
        new LogarithmIntensityCorrector(2, 1).doCorrection(dataManager);
        new LinearIntensityCorrector(Arrays.asList(new InjectionImpl[]{}), new File("")).doCorrection(dataManager);
        IntensityMatrix corrected = dataManager.getIntensityMatrix();

        // write data
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);
        CSVDataLoader.storeToCSVData(new File(testOutput, "linear-intensity"), dataManager);

        // check data
        int[] size = corrected.getSize();

        for (int j = 0; j < size[1]; j++) {
            Assert.assertEquals(Sample.SampleType.NORMAL, corrected.getColumnKeys().get(j).getSample().getSampleType());
        }

        /*
        ArrayList<Double> answer = new ArrayList<>();
        CSVReader answerReader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("flat-global-median-run1.csv")));
        String[] row;
        boolean isFirst = true;
        while ((row = answerReader.readNext()) != null) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            answer.add(Double.valueOf(row[1]));
        }
        */

        // TODO: USE ANSWER
        for (int i = 0; i < size[0]; i++) {
            Assert.assertFalse(Double.isNaN(((IntensityMatrixImpl) corrected).get(i, 0)));
            for (int j = 0; j < size[1]; j++) {
                Assert.assertEquals(String.format("Failed [%d,%d]", i, j), corrected.get(i, 0), corrected.get(i, j), 0.000000001);
            }
        }
    }
}
