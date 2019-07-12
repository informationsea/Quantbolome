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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.BasicIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LogarithmIntensityCorrector;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by yasu on 14/11/30.
 */
public class BasicIntensityCorrectorTest {

    @Test
    public void testBasicIntensityCorrector() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);

        new LogarithmIntensityCorrector(2, 1).doCorrection(dataManager);
        new BasicIntensityCorrector(50, new File("")).doCorrection(dataManager);
        IntensityMatrix corrected = dataManager.getIntensityMatrix();

        // write data
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);
        CSVDataLoader.storeToCSVData(new File(testOutput, "basic-intensity"), dataManager);

        // check data
        for (Injection oneSample : corrected.getColumnKeys()) {
            Assert.assertEquals(Sample.SampleType.NORMAL, oneSample.getSample().getSampleType());
        }

        int compoundIndex = 0;
        for (Compound compound : corrected.getRowKeys()) {
            double base = ((Number)corrected.getRow(compound)[0]).doubleValue();
            for (Injection sample : corrected.getColumnKeys()) {
                Assert.assertEquals(String.format("[%d,%d]%s/%s", compoundIndex, sample.getRunIndex(), sample.getName(), sample.getFileName()),
                        base, corrected.get(compound, sample), 0.0001);
            }
            compoundIndex += 1;
        }
    }
}
