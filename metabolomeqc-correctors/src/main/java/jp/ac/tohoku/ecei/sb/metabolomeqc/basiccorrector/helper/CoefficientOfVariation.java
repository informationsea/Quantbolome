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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.stream.DoubleStream;

/**
 * Created by yasu on 15/08/05.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoefficientOfVariation {

    private static final StandardDeviation standardDeviation = new StandardDeviation();

    public static double evaluate(double[] values) {
        double mean = DoubleStream.of(values).sum()/values.length;
        StandardDeviation standardDeviation = new StandardDeviation();
        double sd = standardDeviation.evaluate(values, mean);
        return sd/mean;
    }
}
