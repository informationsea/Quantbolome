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

import info.informationsea.dataclustering4j.matrix.aggregate.Median;
import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.MedianIntensityCorrector;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by yasu on 14/11/30.
 */
public class MedianIntensityCorrectorTest {

    @Test
    public void testMedianIntensityCorrection() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrix mim = dataManager.getIntensityMatrix();

        new MedianIntensityCorrector().doCorrection(dataManager);
        IntensityMatrix corrected = dataManager.getIntensityMatrix();
        dataManager.setIntensityMatrix((IntensityMatrixImpl) corrected);

        // wirte data
        File buildDir = new File(System.getProperty("user.dir"), "build");
        File testOutput = new File(buildDir, "test-data");
        testOutput.mkdirs();
        CSVDataLoader.storeToCSVData(new File(testOutput, "median-test"), dataManager);

        // check data
        int[] size = corrected.getSize();

        for (int j = 0; j < size[1]; j++) {
            Assert.assertEquals(mim.getColumnKeys().get(j), corrected.getColumnKeys().get(j));
        }

        for (int i = 0; i < size[0]; i++) {
            Assert.assertEquals(mim.getRowKeys().get(i), corrected.getRowKeys().get(i));
        }

        Double[] medians = new Double[corrected.getSize()[1]];
        corrected.aggregateByColumn(new Median<>(), medians);

        for (double one : medians) {
            Assert.assertEquals(medians[0], one, 0.00000001);
        }
    }
}
