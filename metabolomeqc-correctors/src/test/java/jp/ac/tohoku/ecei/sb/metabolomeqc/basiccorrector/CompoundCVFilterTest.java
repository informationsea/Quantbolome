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

import info.informationsea.tableio.TableRecord;
import info.informationsea.tableio.csv.TableCSVReader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.CompoundImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class CompoundCVFilterTest {

    @Test
    public void testCVFilter() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        final double CV_THRESHOLD = 0.1;
        CompoundCVFilter filter = new CompoundCVFilter(CV_THRESHOLD);
        filter.doCorrection(dataManager);

        // load answer
        TableCSVReader csvReader = new TableCSVReader(new InputStreamReader(getClass().getResourceAsStream("random-measure/compound-cv.csv")));
        csvReader.setUseHeader(true);
        Map<Integer, Double> cvAnswer = new HashMap<>();

        int count = 0;
        for (TableRecord record : csvReader) {
            double cv = record.get(1).toNumeric();
            cvAnswer.put(Integer.parseInt(record.get(0).toString().substring("COMPOUND".length())), cv);
            if (cv < CV_THRESHOLD) count += 1;
        }

        for (Map.Entry<Integer, Double> one : cvAnswer.entrySet()) {
            CompoundImpl compound = dataManager.getCompounds().queryForId(one.getKey());
            if (compound != null) {
                //log.info("compare {} {} {}", compound, compound.getAttribute("MaxCV"), compound.getAttributeJson());
                Assert.assertEquals(one.getValue(), (double) compound.getAttribute("MaxCV"), 0.0000001);

                for (Injection injection : original.getColumnKeys()) {
                    Assert.assertEquals(original.get(compound, injection), dataManager.getIntensityMatrix().get(compound, injection), 0.000001);
                }
            }
        }

        Assert.assertEquals(count, dataManager.getIntensityMatrix().getSize()[0]);
    }

    @Test
    public void testNoFilter() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        final double CV_THRESHOLD = 1;
        CompoundCVFilter filter = new CompoundCVFilter(CV_THRESHOLD);
        filter.doCorrection(dataManager);

        Assert.assertEquals(original, dataManager.getIntensityMatrix());
    }
}