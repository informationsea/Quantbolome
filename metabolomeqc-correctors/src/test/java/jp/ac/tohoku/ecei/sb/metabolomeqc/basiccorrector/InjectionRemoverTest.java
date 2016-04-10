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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/03.
 */
public class InjectionRemoverTest {

    @Test
    public void testInjectionRemove() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        List<Integer> removeList = Arrays.asList(1, 20, 43);
        IntensityMatrix filtered = new InjectionRemover("1,20,43", "", "", false).doCorrection(original);

        Assert.assertEquals(original.getSize()[1] - 3, filtered.getSize()[1]);
        Assert.assertFalse(filtered.getColumnKeys().stream().filter(it -> removeList.contains(it.getId())).count() > 0);
    }

    @Test
    public void testInjectionRemove2() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        List<Integer> removeList = Arrays.asList(1, 20, 43);
        IntensityMatrix filtered = new InjectionRemover("1,20,43", "", "", true).doCorrection(original);

        Assert.assertEquals(original.getSize()[1] - 30 - 2, filtered.getSize()[1]);
        Assert.assertFalse(filtered.getColumnKeys().stream().filter(it -> removeList.contains(it.getId()) || it.isIgnored()).count() > 0);
    }

    @Test
    public void testInjectionRemove3() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        List<Integer> removeList = Arrays.asList(2, 8);
        IntensityMatrix filtered = new InjectionRemover("", "2, 8", "", false).doCorrection(original);

        Assert.assertEquals(original.getSize()[1] - 47, filtered.getSize()[1]);
        Assert.assertFalse(filtered.getColumnKeys().stream().filter(it -> removeList.contains(it.getSample().getId())).count() > 0);
    }

    @Test
    public void testInjectionRemove4() throws Exception {
        DataManager dataManager = new DataManager();
        CSVDataLoader.loadFromCSVData(new File(getClass().getResource("random-measure").getFile()), dataManager);
        IntensityMatrixImpl original = new IntensityMatrixImpl(dataManager.getIntensityMatrix());

        List<Integer> removeList = Collections.singletonList(2);
        IntensityMatrix filtered = new InjectionRemover("", "", "2 ", false).doCorrection(original);

        Assert.assertEquals(original.getSize()[1] - 117, filtered.getSize()[1]);
        Assert.assertFalse(filtered.getColumnKeys().stream().filter(it -> removeList.contains(it.getPlate().getId())).count() > 0);
    }

}