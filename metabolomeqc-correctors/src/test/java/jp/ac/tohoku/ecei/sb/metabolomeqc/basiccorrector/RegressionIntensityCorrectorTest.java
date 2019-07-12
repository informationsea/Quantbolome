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
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LogarithmIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.RegressionIntensityCorrector;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/11
 */
public class RegressionIntensityCorrectorTest {
    @Test
    public void testRegressionIntensityCorrector() throws Exception {


        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);
        IntensityMatrix mim = dataManager.getIntensityMatrix();

        new LogarithmIntensityCorrector(2, 1).doCorrection(dataManager);
        new RegressionIntensityCorrector(50, new File("")).doCorrection(dataManager);
        IntensityMatrix corrected = dataManager.getIntensityMatrix();

        // write data
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);
        CSVDataLoader.storeToCSVData(new File(testOutput, "regression-intensity"), dataManager);


        //check data
        Assert.assertEquals(mim.getNormalInjections().size(), corrected.getSize()[1]);

        final int REGRESSION_SAMPLE_NUM = 8*2+3;
        int sampleNum = corrected.getSize()[1];
        int compoundNum = corrected.getSize()[0];

        for (int j = 0; j < compoundNum; j++) {
            double answer = corrected.get(j, sampleNum-1);
            for (int i = 0; i < REGRESSION_SAMPLE_NUM; i++) {
                Assert.assertEquals(String.format("[%d,%d]", i, j), answer, corrected.get(j, sampleNum-1-i), 0.000001);
            }
        }
    }
}
