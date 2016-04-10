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

import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/03.
 */
public class CompoundRemoverTest {

    @Test
    public void testCompoundRemoverTest() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        CompoundRemover all = new CompoundRemover();
        IntensityMatrix none = all.doCorrection(original);
        Assert.assertArrayEquals(new int[]{0, original.getSize()[1]}, none.getSize());
    }

    @Test
    public void testCompoundRemoverTest2() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        CompoundRemover remover = new CompoundRemover();
        remover.setMinimumMZ(400);
        remover.setMaximumMZ(450);
        IntensityMatrix none = remover.doCorrection(original);
        Assert.assertArrayEquals(new int[]{1700-131, original.getSize()[1]}, none.getSize());
    }

}