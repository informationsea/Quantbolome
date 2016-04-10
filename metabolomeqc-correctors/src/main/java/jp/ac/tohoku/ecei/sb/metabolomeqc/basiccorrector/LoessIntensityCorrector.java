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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.*;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.GlobalQCMedianCalculator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * metabolomeqc-next
 * Copyright (C) 2015 OKAMURA Yasunobu
 * Created on 15/07/25.
 */
@Slf4j @NoArgsConstructor @AllArgsConstructor
public class LoessIntensityCorrector extends IntensityCorrectorWithBadSamples {

    @Getter @Setter
    double bandwidth = LoessInterpolator.DEFAULT_BANDWIDTH;
    @Getter @Setter
    int robustnessIters = LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS;
    @Getter @Setter
    double accuracy = LoessInterpolator.DEFAULT_ACCURACY;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        updateBadInjections(original);
        List<Injection> normalSamples = original.getNormalInjections();

        List<Sample> globalQCSamples = original.getGlobalQCSamples();
        if (globalQCSamples.size() == 0)
            throw new UnsupportedOperationException("No global QC");
        Sample selectedGlobalQC = globalQCSamples.get(0);

        // Calculate SQC median
        Map<Compound, Double> compoundIntensityBase = GlobalQCMedianCalculator.calcGlobalQCMedian(original, badInjections);

        // Main Correction
        IntensityMatrix corrected = new IntensityMatrixImpl(original.getSize()[0], normalSamples.size());
        corrected.setRowKeys(original.getRowKeys());
        corrected.setColumnKeys(normalSamples);
        //corrected.getAttributes().putAll(original.getAttributes());

        LoessInterpolator loessInterpolator = new LoessInterpolator(bandwidth, robustnessIters, accuracy);

        for (Compound oneCompound : corrected.getRowKeys()) {
            double base = compoundIntensityBase.get(oneCompound);
            for (Map.Entry<Plate, List<Injection>> plateInjection : original.getInjectionsByPlate().entrySet()) {
                List<Double> xlist = new ArrayList<>();
                List<Double> ylist = new ArrayList<>();
                for (Injection injection : plateInjection.getValue()) {
                    //log.info("injection: {}", injection);
                    if (!injection.getSample().equals(selectedGlobalQC)) continue;
                    if (injection.isIgnored()) continue;
                    if (badInjections != null && badInjections.contains(injection)) continue;

                    xlist.add((double) injection.getRunIndex());
                    ylist.add(original.get(oneCompound, injection));
                }

                //log.info("X : {}", xlist.size());

                PolynomialSplineFunction fn =
                        loessInterpolator.interpolate(xlist.stream().mapToDouble(x -> x).toArray(),
                                ylist.stream().mapToDouble(x -> x).toArray());

                for (Injection injection : plateInjection.getValue()) {
                    if (!normalSamples.contains(injection)) continue;
                    corrected.put(oneCompound, injection, original.get(oneCompound, injection) - base + fn.value(injection.getRunIndex()));
                }
            }
        }

        return corrected;
    }
}
