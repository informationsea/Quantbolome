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

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yasu on 15/08/05.
 */
public class CoefficientOfVariationTest {

    @Test
    public void testEvaluate() throws Exception {
        double[] values = new double[]{92.49833, 105.19209, 107.28446, 100.76341, 97.52672, 89.43843, 103.56859, 100.16738, 100.47060, 90.03146, 101.88577, 103.09307, 97.76810, 93.72611, 102.89548, 106.07636, 97.54231, 93.41576, 97.62103, 105.41594, 99.73493, 107.87631, 95.40243, 93.28414, 84.26543, 96.32813, 108.28680, 108.86128, 94.79944, 97.09648, 100.46938, 96.31756, 93.62224, 94.99403, 92.19931, 99.18140};
        Assert.assertEquals(0.05927494, CoefficientOfVariation.evaluate(values), 0.000001);

        values = new double[]{6154.978, 6196.199, 5928.990, 5852.069, 5795.487, 5492.209, 5349.547, 5727.824, 6345.933, 6367.642, 5967.644, 5569.688, 6887.097, 5897.049, 5071.612, 5648.689, 5758.681, 6055.889, 5923.502, 5705.994, 5935.291, 5852.234, 5749.106, 6664.491, 6713.700, 5437.670, 5731.655, 6396.349, 5897.061, 5495.956, 6350.573, 6043.137, 5859.189, 5986.369, 5720.952, 5739.368};
        Assert.assertEquals(0.06504662, CoefficientOfVariation.evaluate(values), 0.000001);
    }
}