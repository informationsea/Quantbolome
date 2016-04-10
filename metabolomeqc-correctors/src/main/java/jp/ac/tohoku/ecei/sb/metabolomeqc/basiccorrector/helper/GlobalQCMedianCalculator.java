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

import info.informationsea.dataclustering4j.matrix.aggregate.Median;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/11
 */
public class GlobalQCMedianCalculator {
    private GlobalQCMedianCalculator(){}

    public static Map<Compound, Double> calcGlobalQCMedian(IntensityMatrix mim, List<Injection> badInjections) {
        HashMap<Compound, Double> result = new HashMap<>();
        List<Sample> globalQCSamples = mim.getGlobalQCSamples();
        if (globalQCSamples.size() == 0)
            throw new UnsupportedOperationException("No global QC");
        
        List<Injection> globalQCInjections = mim.getInjectionsBySample(globalQCSamples.get(0));

        Stream<Result> resultStream = mim.getRowKeys().parallelStream().map(one -> {
            ArrayList<Double> intensities = new ArrayList<>();
            for (Injection oneSample : globalQCInjections) {
                if (badInjections != null && badInjections.indexOf(oneSample) != -1)
                    continue;
                if (oneSample.isIgnored()) continue;

                int row = mim.getRowKeys().indexOf(one);
                int column = mim.getColumnKeys().indexOf(oneSample);

                //intensities.add(mim.get(one, oneSample));
                intensities.add(mim.get(row, column));
            }
            return new Result(one, new Median<Double>().process(intensities));
        });

        resultStream.forEachOrdered(one -> result.put(one.compound, one.value));

        return result;
    }

    @Value
    private static class Result {
        Compound compound;
        double value;
    }
}
