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

import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/12
 */
public class UtilsTest {

    List<Integer> list1;
    List<Integer> list2;

    @Before
    public void prepare() {
        list1 = Arrays.asList(1, 2, 3, 4, 5);
        list2 = Arrays.asList(3, 4, 5, 6, 7);
    }

    @Test
    public void testIntersect() {
        Assert.assertArrayEquals(new Integer[]{3, 4, 5}, Utils.intersectOfList(list1, list2).toArray());
    }

    @Test
    public void testDifference() {
        Assert.assertArrayEquals(new Integer[]{1, 2}, Utils.differenceOfList(list1, list2).toArray());
    }
}
