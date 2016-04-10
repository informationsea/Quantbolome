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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Compound;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.GlobalQCMedianCalculator;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.NeighboringGlobalQCFinder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by yasu on 14/12/01.
 */
@Slf4j
@NoArgsConstructor
public class LinearIntensityCorrector extends IntensityCorrectorWithBadSamples {

    public LinearIntensityCorrector(int badQCThreshold) {
        setBadQCThreshold(badQCThreshold);
    }

    public LinearIntensityCorrector(List<Injection> badInjections) {
        setBadInjections(badInjections);
    }

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        updateBadInjections(original);
        List<Injection> normalSamples = original.getNormalInjections();

        // Calculate SQC median
        Map<Compound, Double> compoundIntensityBase = GlobalQCMedianCalculator.calcGlobalQCMedian(original, badInjections);
        Map<Injection, NeighboringGlobalQCFinder.NeighboringGlobalQC> neighboringSQC = NeighboringGlobalQCFinder.findNeighboringSQC(original, badInjections);

        // Main Correction
        IntensityMatrix corrected = new IntensityMatrixImpl(original.getSize()[0], normalSamples.size());
        corrected.setRowKeys(original.getRowKeys());
        corrected.setColumnKeys(normalSamples);
        //corrected.getAttributes().putAll(original.getAttributes());

        for (Injection oneSample : normalSamples) {
            NeighboringGlobalQCFinder.NeighboringGlobalQC neighboring = neighboringSQC.get(oneSample);
            if ((neighboring == null) || (neighboring.leftGlobalQC == null) || (neighboring.rightGlobalQC == null)) {
                log.info("Linear Correction set NaN : {} {}", oneSample, neighboring);
                for (Compound oneCompound : corrected.getRowKeys()) {
                    corrected.put(oneCompound, oneSample, Double.NaN);
                }
                continue;
            }

            double beforeSQCWeight = ((double)neighboring.rightGlobalQC.getRunIndex() - oneSample.getRunIndex())/(neighboring.rightGlobalQC.getRunIndex() - neighboring.leftGlobalQC.getRunIndex());
            double afterSQCWeight = ((double)oneSample.getRunIndex() - neighboring.leftGlobalQC.getRunIndex()) / (neighboring.rightGlobalQC.getRunIndex() - neighboring.leftGlobalQC.getRunIndex());

            for (Compound oneCompound : corrected.getRowKeys()) {
                double base = compoundIntensityBase.get(oneCompound);
                double offsetBefore = (base - original.get(oneCompound, neighboring.leftGlobalQC))*beforeSQCWeight;
                double offsetAfter = (base - original.get(oneCompound, neighboring.rightGlobalQC))*afterSQCWeight;
                double offsetValue =  offsetBefore + offsetAfter;
                corrected.put(oneCompound, oneSample, original.get(oneCompound, oneSample) + offsetValue);
            }
        }

        return corrected;
    }
}
