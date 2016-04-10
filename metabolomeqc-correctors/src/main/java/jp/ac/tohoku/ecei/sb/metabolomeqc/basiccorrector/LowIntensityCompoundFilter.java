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
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.IntensityMatrixImpl;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yasu on 15/08/05.
 */
@AllArgsConstructor @NoArgsConstructor @Slf4j
public class LowIntensityCompoundFilter extends LoggingIntensityCorrector {

    @Getter @Setter
    private long numberThresholdOfLowIntensity = 100;

    @Setter @Getter
    private double lowIntensityThreshold = 0.001;

    @Setter @Getter
    private Sample.SampleType sampleType = null;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {

        List<Compound> newCompounds = new ArrayList<>();

        List<Injection> injectionList = original.getColumnKeys();
        if (sampleType != null)
            injectionList = injectionList.stream().filter(injection -> injection.getSample().getSampleType().equals(sampleType)).collect(Collectors.toList());

        for (Compound oneCompound : original.getRowKeys()) {
            long lowIntensityCount = injectionList.stream().filter(i -> !i.isIgnored()).mapToDouble(i -> original.get(oneCompound, i)).filter(value -> value < lowIntensityThreshold).count();
            if (lowIntensityCount < numberThresholdOfLowIntensity) {
                oneCompound.setAttribute("NumberOfLowIntensity", lowIntensityCount);
                newCompounds.add(oneCompound);
            }
        }

        IntensityMatrixImpl newMatrix = new IntensityMatrixImpl(newCompounds.size(), original.getSize()[1]);
        newMatrix.setRowKeys(newCompounds);
        newMatrix.setColumnKeys(original.getColumnKeys());

        for (Compound oneCompound : newCompounds) {
            for (Injection oneInjection : original.getColumnKeys()) {
                newMatrix.put(oneCompound, oneInjection, original.get(oneCompound, oneInjection));
            }
        }

        return newMatrix;
    }
}
