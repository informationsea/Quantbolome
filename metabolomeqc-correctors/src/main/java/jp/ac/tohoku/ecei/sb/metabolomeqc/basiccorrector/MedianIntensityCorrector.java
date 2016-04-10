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

import info.informationsea.dataclustering4j.matrix.aggregate.NonZeroMedian;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import jp.ac.tohoku.ecei.sb.metabolomeqc.IntensityCorrector;

import java.util.Arrays;

/**
 * Created by yasu on 14/11/30.
 */
public class MedianIntensityCorrector extends LoggingIntensityCorrector {
    public static final String MEDIAN_OFFSET = "MEDIAN_OFFSET";

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        Double[] medians = new Double[original.getSize()[1]];
        original.aggregateByColumn(new NonZeroMedian<>(), medians);
        double medianOfMedian = new NonZeroMedian<Double>().process(Arrays.asList(medians));

        IntensityMatrix corrected = new IntensityMatrixImpl(original);
        int[] matrixSize = corrected.getSize();

        double[] medianOffsets = new double[matrixSize[1]];
        for (int j = 0; j < matrixSize[1]; j++) {
        	medianOffsets[j] = medianOfMedian - medians[j];
            corrected.getColumnKeys().get(j).setAttribute(MEDIAN_OFFSET, medianOffsets[j]);
        }

        for (int i = 0; i < matrixSize[0]; i++) {
            for (int j = 0; j < matrixSize[1]; j++) {
                corrected.put(i, j, corrected.get(i, j) + medianOffsets[j]);
            }
        }

        return corrected;
    }
}
