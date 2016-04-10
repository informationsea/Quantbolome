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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LogarithmIntensityCorrector;
import org.junit.Assert;
import org.junit.Test;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/01
 */
public class LogarithmIntensityCorrectorTest {
    @Test
    public void testLogIntensityCorrector() {
        IntensityMatrix mim = new IntensityMatrixImpl(new double[]{1.0, 2.0, 4.0, 3.0}, 2);

        IntensityMatrix log2mim = new LogarithmIntensityCorrector(4).doCorrection(mim);
        Assert.assertEquals(1.0, log2mim.get(1, 1), 0.00000001);
    }

    @Test
    public void testLogIntensityCorrector2() {
        IntensityMatrix mim = new IntensityMatrixImpl(new double[]{1.0, 2.0, 4.0, 3.0}, 2);

        IntensityMatrix log2mim = new LogarithmIntensityCorrector(4, 0).doCorrection(mim);
        Assert.assertEquals(0.0, log2mim.get(0, 0), 0.00000001);
        Assert.assertEquals(0.5, log2mim.get(0, 1), 0.00000001);
        Assert.assertEquals(1.0, log2mim.get(1, 0), 0.00000001);
    }

    @Test
    public void testLogIntensityCorrector3() {
        IntensityMatrix mim = new IntensityMatrixImpl(new double[]{1.0, 2.0, 4.0, 3.0}, 2);

        IntensityMatrix log2mim = new LogarithmIntensityCorrector().doCorrection(mim);
        Assert.assertEquals(1.0, log2mim.get(0, 0), 0.00000001);
        Assert.assertEquals(2.0, log2mim.get(1, 1), 0.00000001);
    }

}
