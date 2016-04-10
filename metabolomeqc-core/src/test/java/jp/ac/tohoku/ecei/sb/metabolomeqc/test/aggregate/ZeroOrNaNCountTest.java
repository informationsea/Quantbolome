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

package jp.ac.tohoku.ecei.sb.metabolomeqc.test.aggregate;

import jp.ac.tohoku.ecei.sb.metabolomeqc.aggregate.ZeroOrNaNCount;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/09
 */
public class ZeroOrNaNCountTest {
    @Test
    public void testZeroCount2() {
        ZeroOrNaNCount<Double> zeroOrNaNCount = new ZeroOrNaNCount<>();

        ArrayList<Double> arrayList3 = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            arrayList3.add(0.);

        Assert.assertEquals(2, zeroOrNaNCount.process(Arrays.asList(0., 1., 2., 0., 3.)), 0);
        Assert.assertEquals(0, zeroOrNaNCount.process(Arrays.asList(8., 1., 4. -3., 6.)), 0);
        Assert.assertEquals(5, zeroOrNaNCount.process(arrayList3), 0);

        Assert.assertEquals(2, zeroOrNaNCount.process(Arrays.asList(0., 1., 2., Double.NaN, 3.)), 0);
    }
}
