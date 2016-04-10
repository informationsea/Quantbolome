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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.CompoundImpl;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/12
 */
public class GlobalQCMedianCalculatorTest {
    @Test
    public void testSQCMedianCalculator() {
        IntensityMatrix mim = new IntensityMatrixImpl(new double[]{
                1., 2., 3., 4., 5.,
                6., 7., 8., 9., 10.,
                0., 3., 0., 5., 8.,
                0., 7., 0., 7., 10.
        }, 5);

        mim.setColumnKeys(Arrays.<Injection>asList(
                BadInjectionFinderTest.injectionFactory(1),
                BadInjectionFinderTest.injectionFactory(2),
                BadInjectionFinderTest.injectionFactory(3),
                BadInjectionFinderTest.injectionFactory(4),
                BadInjectionFinderTest.injectionFactory(5)
        ));

        Compound[] compounds = new CompoundImpl[]{
                new CompoundImpl(10, 20, null, 1),
                new CompoundImpl(10, 25, null, 1),
                new CompoundImpl(15, 20, null, 1),
                new CompoundImpl(15, 25, null, 1)};

        mim.setRowKeys(Arrays.asList(compounds));

        List<Injection> badSamples = BadInjectionFinder.findBadSamples(mim, 1);
        Map<Compound, Double> map = GlobalQCMedianCalculator.calcGlobalQCMedian(mim, badSamples);

        Assert.assertEquals(4., map.get(compounds[0]), 0.0000001);
        Assert.assertEquals(9., map.get(compounds[1]), 0.0000001);
        Assert.assertEquals(5., map.get(compounds[2]), 0.0000001);
        Assert.assertEquals(7., map.get(compounds[3]), 0.0000001);
    }
}
