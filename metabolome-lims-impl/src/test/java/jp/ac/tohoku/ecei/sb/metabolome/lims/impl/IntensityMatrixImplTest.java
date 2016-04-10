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

package jp.ac.tohoku.ecei.sb.metabolome.lims.impl;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManagerTest;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Plate;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by yasu on 15/06/09.
 */
@Slf4j
public class IntensityMatrixImplTest {


    DataManager referenceDataManager;
    IntensityMatrix intensityMatrix;

    @Before
    public void setUp() throws Exception {
        referenceDataManager = DataManagerTest.prepareData("jdbc:h2:mem:");
        intensityMatrix = IntensityMatrixImpl.loadFromCSV(new InputStreamReader(getClass().getResourceAsStream("../dataset1/intensity.csv")), referenceDataManager);
    }

    @After
    public void after() throws Exception {
        referenceDataManager.close();
    }

    @Test
    public void testConstructor1() throws Exception {
        IntensityMatrixImpl intensityMatrix = new IntensityMatrixImpl(new double[]{1, 2, 3, 4, 5, 6}, 3);
        Assert.assertArrayEquals(new int[]{2, 3}, intensityMatrix.getSize());
        Assert.assertArrayEquals(new Double[]{1.0,2.0,3.0}, intensityMatrix.getRow(0));
        Assert.assertArrayEquals(new Double[]{4.0,5.0,6.0}, intensityMatrix.getRow(1));
    }

    @Test
    public void testConstructor2() throws Exception {
        IntensityMatrixImpl intensityMatrix1 = new IntensityMatrixImpl(new double[]{1, 2, 3, 4, 5, 6}, 3);
        IntensityMatrixImpl intensityMatrix2 = new IntensityMatrixImpl(intensityMatrix1);
        Assert.assertArrayEquals(new int[]{2, 3}, intensityMatrix2.getSize());
        Assert.assertArrayEquals(new Double[]{1.0,2.0,3.0}, intensityMatrix2.getRow(0));
        Assert.assertArrayEquals(new Double[]{4.0,5.0,6.0}, intensityMatrix2.getRow(1));
    }


    @Test
    public void testLoadFromCSV() throws Exception {
        Assert.assertEquals(0.0, intensityMatrix.get(0, 0), 0.00000001);
        Assert.assertEquals(1.0, intensityMatrix.get(1, 0), 0.00000001);
        Assert.assertEquals(1.0, intensityMatrix.get(0, 1), 0.00000001);
    }

    @Test(expected = RuntimeException.class)
    public void testLoadFromCSV2() throws Exception {
        referenceDataManager.getCompounds().deleteById(1);
        IntensityMatrixImpl.loadFromCSV(new InputStreamReader(getClass().getResourceAsStream("../dataset1/intensity.csv")), referenceDataManager);
    }

    @Test(expected = RuntimeException.class)
    public void testLoadFromCSV3() throws Exception {
        referenceDataManager.getInjections().deleteById(1);
        IntensityMatrixImpl.loadFromCSV(new InputStreamReader(getClass().getResourceAsStream("../dataset1/intensity.csv")), referenceDataManager);
    }

    @Test(expected = RuntimeException.class)
    public void testLoadFromCSV4() throws Exception {
        PlateImpl plate = referenceDataManager.getPlates().queryForId(1);
        SampleImpl sample = referenceDataManager.getSamples().queryForId(1);
        referenceDataManager.getInjections().create(new InjectionImpl(plate, "Hello", "FileName", sample, 200, false, 0));
        IntensityMatrixImpl.loadFromCSV(new InputStreamReader(getClass().getResourceAsStream("../dataset1/intensity.csv")), referenceDataManager);
    }

    @Test(expected = RuntimeException.class)
    public void testLoadFromCSV5() throws Exception {
        referenceDataManager.getCompounds().create(new CompoundImpl(200, 100, null, 2));
        IntensityMatrixImpl.loadFromCSV(new InputStreamReader(getClass().getResourceAsStream("../dataset1/intensity.csv")), referenceDataManager);
    }

    @Test
    public void testGetNormalInjections() throws Exception {
        QueryBuilder<SampleImpl, Integer> sampleQueryBuilder = referenceDataManager.getSamples().queryBuilder();
        sampleQueryBuilder.where().eq("sampleType", Sample.SampleType.NORMAL);
        QueryBuilder<InjectionImpl, Integer> injectionQueryBuilder = referenceDataManager.getInjections().queryBuilder();
        injectionQueryBuilder.join(sampleQueryBuilder);
        injectionQueryBuilder.orderBy("runIndex", true);
        log.info("Query {}", injectionQueryBuilder.prepare().getStatement());
        Assert.assertArrayEquals(injectionQueryBuilder.query().toArray(), intensityMatrix.getNormalInjections().toArray());
    }

    @Test
    public void testGetInjectionsBySample() throws Exception {
        SampleImpl sample = referenceDataManager.getSamples().queryForId(1);
        Assert.assertArrayEquals(referenceDataManager.getInjections().queryForEq("sample_id", sample).toArray(),
                intensityMatrix.getInjectionsBySample(sample).toArray());

        sample = referenceDataManager.getSamples().queryForId(3);
        Assert.assertArrayEquals(referenceDataManager.getInjections().queryForEq("sample_id", sample).toArray(),
                intensityMatrix.getInjectionsBySample(sample).toArray());
    }

    @Test
    public void testGetQCInjections() throws Exception {
        Map<Sample, List<Injection>> sampleListMap = intensityMatrix.getQCInjections();

        Set<Sample> expectedSamples = new HashSet<>(referenceDataManager.getSamples().queryForEq("SampleType", Sample.SampleType.QC));
        Assert.assertEquals(expectedSamples, sampleListMap.keySet());

        for (Sample sample : expectedSamples) {
            QueryBuilder<InjectionImpl, Integer> injectionQueryBuilder = referenceDataManager.getInjections().queryBuilder();
            injectionQueryBuilder.where().eq("sample_id", sample);
            injectionQueryBuilder.orderBy("runIndex", true);
            Assert.assertArrayEquals(injectionQueryBuilder.query().toArray(),
                    sampleListMap.get(sample).toArray());
        }
    }

    @Test
    public void testGetGlobalQCSamples() throws Exception {
        List<Sample> globalQCs = intensityMatrix.getGlobalQCSamples();
        Assert.assertArrayEquals(new Object[]{referenceDataManager.getSamples().queryForId(1)}, globalQCs.toArray());
    }

    @Test
    public void testGetInjectionsByPlate() throws Exception {
        Map<Plate, List<Injection>> plateListMap = intensityMatrix.getInjectionsByPlate();

        Set<Plate> expectedPlates = new HashSet<>(referenceDataManager.getPlates().queryForAll());
        Assert.assertEquals(plateListMap.keySet(), expectedPlates);

        for (Plate one : expectedPlates) {
            QueryBuilder<InjectionImpl, Integer> injectionQueryBuilder = referenceDataManager.getInjections().queryBuilder();
            injectionQueryBuilder.where().eq("plate_id", one);
            injectionQueryBuilder.orderBy("runIndex", true);
            Assert.assertArrayEquals(injectionQueryBuilder.query().toArray(),
                    plateListMap.get(one).toArray());
        }
    }

    @Test
    public void testGetInjectionsByPlateAndType() throws Exception {
        Map<Plate, Map<Sample.SampleType, List<Injection>>> map = intensityMatrix.getInjectionsByPlateAndType();

        Set<Plate> expectedPlates = new HashSet<>(referenceDataManager.getPlates().queryForAll());
        Assert.assertEquals(map.keySet(), expectedPlates);

        for (Plate one : expectedPlates) {
            QueryBuilder<InjectionImpl, Integer> injectionQueryBuilder = referenceDataManager.getInjections().queryBuilder();
            injectionQueryBuilder.where().eq("plate_id", one);
            injectionQueryBuilder.orderBy("runIndex", true);
            Assert.assertEquals(injectionQueryBuilder.query().stream().collect(Collectors.groupingBy(i -> i.getSample().getSampleType())),
                    map.get(one));
        }
    }
}