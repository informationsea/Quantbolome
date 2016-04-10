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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/*
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/03.
 *
 */

/**
 * Remove compounds in the specified range.
 */
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CompoundRemover extends LoggingIntensityCorrector {

    @Getter @Setter
    private double minimumMZ = 0;

    @Getter @Setter
    private double maximumMZ = Double.MAX_VALUE;

    @Getter @Setter
    private double minimumRetentionTime = 0;

    @Getter @Setter
    private double maximumRetentionTime = Double.MAX_VALUE;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        List<Compound> compoundList = original.getRowKeys().stream().
                filter(it -> !(minimumMZ <= it.getMZ() && it.getMZ() <= maximumMZ &&
                        minimumRetentionTime <= it.getRetentionTime() && it.getRetentionTime() <= maximumRetentionTime)).
                collect(Collectors.toList());

        IntensityMatrix matrix = new IntensityMatrixImpl(compoundList.size(), original.getSize()[1]);
        matrix.setColumnKeys(original.getColumnKeys());
        matrix.setRowKeys(compoundList);

        for (Compound compound : compoundList) {
            for (Injection injection : original.getColumnKeys()) {
                matrix.put(compound, injection, original.get(compound, injection));
            }
        }

        return matrix;
    }
}
