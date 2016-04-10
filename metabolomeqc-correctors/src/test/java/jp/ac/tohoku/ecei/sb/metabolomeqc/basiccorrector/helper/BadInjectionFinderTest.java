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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.BadInjectionFinder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/10
 */
@Slf4j
public class BadInjectionFinderTest {

    private static StudyImpl study = new StudyImpl("Hello");
    private static PlateImpl plate = new PlateImpl(study, "foo", new Date());
    private static SampleImpl sample = new SampleImpl(Sample.SampleType.QC, UUID.fromString("C69BBC10-D4DC-42AF-8E2B-266D214276CF"), null);


    @Test
    public void testBadSampleFinder() {
        IntensityMatrixImpl matrix = new IntensityMatrixImpl(new double[]{
                0., 1., 2., 0.,
                0., 2., 0., 3.,
                0., 2., 4., 0.,
                0., 4., 1., 0.
        }, 4);

        List<Injection> samples = Arrays.asList(injectionFactory(0), injectionFactory(1), injectionFactory(2), injectionFactory(3));
        matrix.setColumnKeys(samples);

        List<Injection> badSamples1 = BadInjectionFinder.findBadSamples(matrix, 3);
        matrix.rehashKeys();

        Assert.assertEquals(2, badSamples1.size(), 0);
        Assert.assertEquals(0, badSamples1.get(0).getRunIndex(), 0);
        Assert.assertEquals(3, badSamples1.get(1).getRunIndex(), 0);

        Assert.assertEquals(4, samples.get(0).getAttribute(BadInjectionFinder.ZERO_COUNT));
        Assert.assertEquals(0, samples.get(1).getAttribute(BadInjectionFinder.ZERO_COUNT));
        Assert.assertEquals(1, samples.get(2).getAttribute(BadInjectionFinder.ZERO_COUNT));
        Assert.assertEquals(3, samples.get(3).getAttribute(BadInjectionFinder.ZERO_COUNT));

        Assert.assertEquals(true, samples.get(0).getAttribute(BadInjectionFinder.BAD_SAMPLE_MANY_ZERO));
        Assert.assertNull(samples.get(1).getAttribute(BadInjectionFinder.BAD_SAMPLE_MANY_ZERO));

        List<Injection> badSamples2 = BadInjectionFinder.findBadSamples(matrix, 1);
        Assert.assertEquals(0, badSamples2.get(0).getRunIndex(), 0);
        Assert.assertEquals(2, badSamples2.get(1).getRunIndex(), 0);
        Assert.assertEquals(3, badSamples2.get(2).getRunIndex(), 0);
    }

    @Test
    public void testBadSampleFinder2() {
        IntensityMatrix matrix = new IntensityMatrixImpl(302, 4);
        matrix.put(0, 0, 1.);
        matrix.put(1, 0, 1.);
        matrix.put(2, 0, 1.);
        matrix.put(0, 2, 1.);
        matrix.put(1, 2, 1.);

        List<Injection> samples = Arrays.asList(injectionFactory(0), injectionFactory(1), injectionFactory(2), injectionFactory(3));
        matrix.setColumnKeys(samples);

        List<Injection> badSamples1 = BadInjectionFinder.findBadSamples(matrix);
        Assert.assertEquals(3, badSamples1.size());
        Assert.assertEquals(1, badSamples1.get(0).getRunIndex(), 0);
        Assert.assertEquals(2, badSamples1.get(1).getRunIndex(), 0);
        Assert.assertEquals(3, badSamples1.get(2).getRunIndex(), 0);
    }

    public static Injection injectionFactory(int id) {
        InjectionImpl injection = new InjectionImpl();
        injection.setId(id);
        injection.setRunIndex(id);
        injection.setPlate(plate);
        injection.setSample(sample);
        return injection;
    }
}
