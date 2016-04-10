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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolomeqc.aggregate.ZeroOrNaNCount;

import java.util.ArrayList;
import java.util.List;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/10
 */
public class BadInjectionFinder {
    public static final int DEFAULT_BAD_SAMPLE_THRESHOLD = 300;
    public static final String BAD_SAMPLE_MANY_ZERO = "BAD_SAMPLE_MANY_ZERO";
    public static final String ZERO_COUNT = "ZERO_COUNT";

    private BadInjectionFinder(){};

    public static List<Injection> findBadSamples(IntensityMatrix mim) {
        return findBadSamples(mim, DEFAULT_BAD_SAMPLE_THRESHOLD);
    }

    public static List<Injection> findBadSamples(IntensityMatrix mim, int badThreshold) {
        ArrayList<Injection> badInjections = new ArrayList<>();

        Integer[] zeroCount = new Integer[mim.getSize()[1]];
        List<Injection> sampleList = mim.getColumnKeys();
        mim.aggregateByColumn(new ZeroOrNaNCount<>(), zeroCount);

        for (int i = 0; i < sampleList.size(); i++) {
            sampleList.get(i).setAttribute(ZERO_COUNT, zeroCount[i]);
            if (zeroCount[i] >= badThreshold) {
                badInjections.add(sampleList.get(i));
                sampleList.get(i).setAttribute(BAD_SAMPLE_MANY_ZERO, true);
            }
        }

        return badInjections;
    }
}
