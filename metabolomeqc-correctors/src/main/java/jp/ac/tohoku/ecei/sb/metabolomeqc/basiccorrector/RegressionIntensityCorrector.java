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
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/10
 */

/**
 * This class assume a input matrix is log corrected
 */
@NoArgsConstructor @Slf4j
public class RegressionIntensityCorrector extends IntensityCorrectorWithBadSamples {
    public RegressionIntensityCorrector(List<Injection> badInjections) {
        setBadInjections(badInjections);
    }

    public RegressionIntensityCorrector(int badThreshold) {
        setBadQCThreshold(badThreshold);
    }

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        updateBadInjections(original);
        // Make corrected intensity matrix
        List<Injection> correctedSamples = original.getNormalInjections();

        IntensityMatrix corrected = new IntensityMatrixImpl(original.getSize()[0], correctedSamples.size());
        corrected.setRowKeys(original.getRowKeys());
        corrected.setColumnKeys(correctedSamples);

        List<Sample> globalQCIndexes = original.getGlobalQCSamples();
        if (globalQCIndexes.size() == 0)
            throw new UnsupportedOperationException("No global QC");
        log.info("Global QC {}", globalQCIndexes);
        log.info("Bad injections {}", badInjections);

        // median of SQCs for compounds
        Map<Compound, Double> medianForCompounds = GlobalQCMedianCalculator.calcGlobalQCMedian(original, badInjections);

        // do correction
        Map<Plate, Map<Sample.SampleType, List<Injection>>> map = original.getInjectionsByPlateAndType();
        for (Map.Entry<Plate, Map<Sample.SampleType, List<Injection>>> oneRun : map.entrySet()) {
            Stream<CorrectionResult[]> oneresult = corrected.getRowKeys().parallelStream().map(oneCompound -> {
                SimpleRegression simpleRegression = new SimpleRegression();
                for (Injection oneSqc : oneRun.getValue().get(Sample.SampleType.QC)) {
                    if (!oneSqc.getSample().equals(globalQCIndexes.get(0))) continue; // skip non global QC
                    if (badInjections.indexOf(oneSqc) != -1) continue; // skip bad sample
                    if (oneSqc.isIgnored()) continue; // skip ignored QCs
                    simpleRegression.addData(oneSqc.getRunIndex(), original.get(oneCompound, oneSqc));
                }

                CorrectionResult[] resultArray = new CorrectionResult[oneRun.getValue().get(Sample.SampleType.NORMAL).size()];

                log.info("Simple Regression N : {}", simpleRegression.getN());

                if (simpleRegression.getN() < 3) {
                    // Failed to correct
                    int i = 0;
                    for (Injection oneNormal : oneRun.getValue().get(Sample.SampleType.NORMAL)) {
                        //corrected.put(oneCompound, oneNormal, Double.NaN);
                        resultArray[i++] = new CorrectionResult(oneNormal, oneCompound, Double.NaN);
                    }
                } else {
                    RegressionResults result = simpleRegression.regress();
                    double[] coefficients = result.getParameterEstimates();

                    int i = 0;
                    // correct
                    for (Injection oneNormal : oneRun.getValue().get(Sample.SampleType.NORMAL)) {
                        double offset = coefficients[0] + coefficients[1] * oneNormal.getRunIndex() - medianForCompounds.get(oneCompound);
                        //corrected.put(oneCompound, oneNormal, original.get(oneCompound, oneNormal) - offset);
                        resultArray[i++] = new CorrectionResult(oneNormal, oneCompound, original.get(oneCompound, oneNormal) - offset);
                    }
                }

                //log.info("resultArray: {} {}", oneRun, resultArray);

                return resultArray;
            });

            oneresult.forEachOrdered(correctionResultArray -> {
                for (CorrectionResult oneResult : correctionResultArray) {
                    corrected.put(oneResult.getCompound(), oneResult.getSample(), oneResult.getValue());
                }
            });
        }

        return corrected;
    }

    @Value
    private class CorrectionResult {
        private Injection sample;
        private Compound compound;
        private double value;
    }
}
