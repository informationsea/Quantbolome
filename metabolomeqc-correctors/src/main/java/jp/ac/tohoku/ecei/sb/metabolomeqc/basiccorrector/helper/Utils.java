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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/12
 */
public class Utils {
    private Utils(){}

    public static <E> List<E> differenceOfList(List<? extends E> a, List<? extends E> b) {
        List<E> result = new ArrayList<>();
        for (E i : a) {
            if (b.contains(i)) continue;
            result.add(i);
        }
        return result;
    }

    public static <E, A extends E, B extends E> List<E> intersectOfList(List<A> a, List<B> b) {
        return a.stream().filter(b::contains).collect(Collectors.toList());
    }
}
