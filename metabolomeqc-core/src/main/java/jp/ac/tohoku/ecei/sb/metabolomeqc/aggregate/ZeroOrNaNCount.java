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

package jp.ac.tohoku.ecei.sb.metabolomeqc.aggregate;

import info.informationsea.dataclustering4j.matrix.aggregate.Aggregate;

import java.util.List;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/09
 */
public class ZeroOrNaNCount<T extends Number> implements Aggregate<T, Integer> {
    @Override
    public Integer process(List<T> array) {
        int count = 0;
        for (T v : array) {
            if (v.doubleValue() == 0)
                count += 1;
            else if (Double.isNaN(v.doubleValue()))
                count += 1;
        }

        return count;
    }
}
