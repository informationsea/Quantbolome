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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.CompoundImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by yasu on 15/08/05.
 */
public class LowIntensityCompoundFilterTest {

    @Test
    public void testQCFilter() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("flat-measure-nodrift").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        final int NUMBER_OF_LOW_INTENSITY = 3;
        LowIntensityCompoundFilter filter = new LowIntensityCompoundFilter(NUMBER_OF_LOW_INTENSITY, 0.01, Sample.SampleType.QC);
        filter.doCorrection(dataManager);

        // load answer
        TableCSVReader csvReader = new TableCSVReader(new InputStreamReader(getClass().getResourceAsStream("flat-measure-nodrift/lowintensity-count.csv")));
        csvReader.setUseHeader(true);
        Map<Integer, Double> answer = new HashMap<>();

        int count = 0;
        for (TableRecord record : csvReader) {
            double cv = record.get(1).toNumeric();
            answer.put(Integer.parseInt(record.get(0).toString().substring("COMPOUND".length())), cv);
            if (cv < NUMBER_OF_LOW_INTENSITY) count += 1;
        }

        for (Map.Entry<Integer, Double> one : answer.entrySet()) {
            CompoundImpl compound = dataManager.getCompounds().queryForId(one.getKey());
            if (compound != null) {
                //log.info("compare {} {} {}", compound, compound.getAttribute("MaxCV"), compound.getAttributeJson());
                Assert.assertEquals(one.getValue().intValue(), (int) compound.getAttribute("NumberOfLowIntensity"));

                for (Injection injection : original.getColumnKeys()) {
                    Assert.assertEquals(original.get(compound, injection), dataManager.getIntensityMatrix().get(compound, injection), 0.000001);
                }
            }
        }

        Assert.assertEquals(count, dataManager.getIntensityMatrix().getSize()[0]);


    }
}