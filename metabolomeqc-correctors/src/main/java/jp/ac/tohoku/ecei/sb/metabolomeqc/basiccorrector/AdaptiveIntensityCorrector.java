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
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.NeighboringGlobalQCFinder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@NoArgsConstructor
public class AdaptiveIntensityCorrector extends IntensityCorrectorWithBadSamples {


    public static final String NO_NEIGHBORING_SQC = "NO_NEIGHBORING_SQC";
    public static final String BAD_NEIGHBORING_SQC = "BAD_NEIGHBORING_SQC";

    public static final String CORRECTION_METHOD = "CORRECTION_METHOD";
    enum CorrectionType {
        LINEAR_COMPLEMENT_CORRECTION,
        REGRESSION_CORRECTION
    }

    public AdaptiveIntensityCorrector(int badQCThreshold) {
        this.badQCThreshold = badQCThreshold;
    }


    @Override
    public IntensityMatrix doCorrection(IntensityMatrix originalMatrix) {
        updateBadInjections(originalMatrix);

        Map<Injection, NeighboringGlobalQCFinder.NeighboringGlobalQC> neighboringSQCMap = NeighboringGlobalQCFinder.findNeighboringSQC(originalMatrix, badInjections);

        List<Injection> regressionSamples = new ArrayList<>();
        for (Map.Entry<Injection, NeighboringGlobalQCFinder.NeighboringGlobalQC> one : neighboringSQCMap.entrySet()) {
            if (one.getValue().rightGlobalQC == null || one.getValue().leftGlobalQC == null) {
                regressionSamples.add(one.getKey());
                one.getKey().setAttribute(NO_NEIGHBORING_SQC, true);
            } else if (badInjections.contains(one.getValue().rightGlobalQC) || badInjections.contains(one.getValue().leftGlobalQC)) {
                regressionSamples.add(one.getKey());
                one.getKey().setAttribute(BAD_NEIGHBORING_SQC, true);
            }
        }

        IntensityMatrix linearCorrected = new LinearIntensityCorrector(badInjections).doCorrection(originalMatrix);
        IntensityMatrix regressionCorrected = new RegressionIntensityCorrector(badInjections).doCorrection(originalMatrix);

        if (linearCorrected.getSize()[1] != regressionCorrected.getSize()[1]) {
            throw new RuntimeException("the dimension of a linear corrected matrix and a regression corrected matrix should be equal");
        }

        IntensityMatrix corrected = new IntensityMatrixImpl(linearCorrected);
        for (Injection oneSample : corrected.getColumnKeys()) {
            if (regressionSamples.contains(oneSample)) {
                oneSample.setAttribute(CORRECTION_METHOD, CorrectionType.REGRESSION_CORRECTION);
                //Compound c = corrected.getRowKeys().get(0);
                //System.out.printf("[%d] %s %f %f\n", oneSample.getColumnIndex(), oneSample.getName(), linearCorrected.get(c, oneSample), regressionCorrected.get(c, oneSample));
                for (Compound oneCompound : corrected.getRowKeys()) {
                    corrected.put(oneCompound, oneSample, regressionCorrected.get(oneCompound, oneSample));
                }
            } else {
                oneSample.setAttribute(CORRECTION_METHOD, CorrectionType.LINEAR_COMPLEMENT_CORRECTION);
            }
        }

        return corrected;
    }
}
