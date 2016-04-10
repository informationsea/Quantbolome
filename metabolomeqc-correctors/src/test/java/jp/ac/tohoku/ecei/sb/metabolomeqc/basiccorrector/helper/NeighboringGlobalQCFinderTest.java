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

package jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper;

import com.j256.ormlite.dao.Dao;
import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.NeighboringGlobalQCFinder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/13
 */
@Slf4j
public class NeighboringGlobalQCFinderTest {
    @Test
    public void testNeighboringSQCFinder() {
        IntensityMatrix mim = new IntensityMatrixImpl(new double[]{1., 2., 1., 2., 3., 4., 5., 6., 8., 9., 10., 7., 3., 8.}, 14);

        PlateImpl[] plates = new PlateImpl[] {
                new PlateImpl(null, "1", null),
                new PlateImpl(null, "2", null),
                new PlateImpl(null, "3", null),
        };

        SampleImpl[] samples = new SampleImpl[]{
                new SampleImpl(Sample.SampleType.QC, UUID.fromString("A9467C10-8D8A-4565-9621-BA4B0263B883"), "Global QC"),    // 0
                new SampleImpl(Sample.SampleType.QC, UUID.fromString("23290266-AEFC-4A6F-BE68-D0EE9BE5A069"), "Local QC"),     // 1
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("8C5A0E74-DF79-49BD-AF93-C6E1B1E57C15"), "Normal 1"), // 2
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("02249813-35ED-40AB-9FB1-F264DD528501"), "Normal 2"), // 3
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("1B64A53A-B87B-488C-985A-D7998EDC2CE3"), "Normal 3"), // 4
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("33525041-5EDA-44D1-B882-310788ACEEDE"), "Normal 4"), // 5
                new SampleImpl(Sample.SampleType.NORMAL, UUID.fromString("4B62D559-31A2-41F1-98C8-431A2E9E2547"), "Normal 5"), // 6
                new SampleImpl(Sample.SampleType.BLANK, UUID.fromString("6DAA0563-281F-48A8-9610-1E3F35718B81"), "Blank")      // 7
        };

        List<Injection> injectionList = Arrays.asList(
                new InjectionImpl(plates[0], null, "SQC1", samples[0], 1, true, 1),
                new InjectionImpl(plates[0], null, "SQC2", samples[0], 2, true, 2),
                new InjectionImpl(plates[0], null, "SQC3", samples[0], 3, false, 3),
                new InjectionImpl(plates[0], null, "N1", samples[2], 4, false, 0),
                new InjectionImpl(plates[0], null, "N2", samples[3], 5, false, 0),
                new InjectionImpl(plates[0], null, "N3", samples[4], 6, false, 0),
                new InjectionImpl(plates[0], null, "SQC4", samples[0], 7, false, 4),
                new InjectionImpl(plates[1], null, "SQC1", samples[1], 8, false, 1),
                new InjectionImpl(plates[1], null, "RQC1", samples[0], 9, false, 1),
                new InjectionImpl(plates[1], null, "N4", samples[5], 10, false, 0),
                new InjectionImpl(plates[2], null, "N5", samples[6], 11, false, 0),
                new InjectionImpl(plates[2], null, "SQC1", samples[1], 12, false, 1),
                new InjectionImpl(plates[2], null, "RQC1", samples[0], 13, false, 1),
                new InjectionImpl(plates[2], null, "BLANK", samples[7], 14, true, 0));


        log.info("Sample list {}", injectionList);

        mim.setColumnKeys(injectionList);
        mim.setRowKeys(Collections.singletonList(
                new CompoundImpl(10, 20, null, 1)
        ));


        Map<Injection, NeighboringGlobalQCFinder.NeighboringGlobalQC> map = NeighboringGlobalQCFinder.findNeighboringSQC(mim, null);
        Assert.assertEquals(8, map.size());

        for (Injection one : injectionList) {
            Assert.assertEquals(String.format("%s", one.getFileName()), !one.getSample().equals(samples[0]), map.containsKey(one));
        }

        NeighboringGlobalQCFinder.NeighboringGlobalQC answer1 = new NeighboringGlobalQCFinder.NeighboringGlobalQC(injectionList.get(2), injectionList.get(6));
        Assert.assertEquals(answer1, map.get(injectionList.get(3)));
        Assert.assertEquals(answer1, map.get(injectionList.get(4)));
        Assert.assertEquals(answer1, map.get(injectionList.get(5)));
        
        Assert.assertEquals(new NeighboringGlobalQCFinder.NeighboringGlobalQC(injectionList.get(8), null), map.get(injectionList.get(9)));
        Assert.assertEquals(new NeighboringGlobalQCFinder.NeighboringGlobalQC(null, injectionList.get(12)), map.get(injectionList.get(10)));

        Assert.assertEquals(new NeighboringGlobalQCFinder.NeighboringGlobalQC(null, injectionList.get(12)), map.get(injectionList.get(11)));
        Assert.assertEquals(new NeighboringGlobalQCFinder.NeighboringGlobalQC(injectionList.get(12), null), map.get(injectionList.get(13)));
    }

    @Test
    public void testOnRealData() throws SQLException, IOException {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("../flat-measure-nodrift").getFile()), dataManager);
        IntensityMatrix mim = dataManager.getIntensityMatrix();

        Map<Injection, NeighboringGlobalQCFinder.NeighboringGlobalQC> neighboringGlobalQCMap = NeighboringGlobalQCFinder.findNeighboringSQC(mim, null);
        Dao<InjectionImpl, Integer> injections = dataManager.getInjections();
        Assert.assertEquals(injections.queryForId(11), neighboringGlobalQCMap.get(injections.queryForId(12)).getLeftGlobalQC());
        Assert.assertEquals(injections.queryForId(20), neighboringGlobalQCMap.get(injections.queryForId(12)).getRightGlobalQC());
        Assert.assertEquals(injections.queryForId(11), neighboringGlobalQCMap.get(injections.queryForId(13)).getLeftGlobalQC());
        Assert.assertEquals(injections.queryForId(20), neighboringGlobalQCMap.get(injections.queryForId(13)).getRightGlobalQC());
        Assert.assertEquals(injections.queryForId(11), neighboringGlobalQCMap.get(injections.queryForId(14)).getLeftGlobalQC());
        Assert.assertEquals(injections.queryForId(20), neighboringGlobalQCMap.get(injections.queryForId(14)).getRightGlobalQC());
    }
}
