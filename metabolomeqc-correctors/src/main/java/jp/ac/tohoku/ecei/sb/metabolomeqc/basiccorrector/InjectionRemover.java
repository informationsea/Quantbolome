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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/03.
 */
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class InjectionRemover extends LoggingIntensityCorrector {

    /**
     * Injection IDs with comma separators
     */
    @Getter @Setter
    private String injectionIds = "";

    /**
     * Sample IDs with comma separators
     */
    @Getter @Setter
    private String sampleIds = "";

    /**
     * Plate IDs with comma separators
     */
    @Getter @Setter
    private String plateIds = "";

    @Getter @Setter
    private boolean removeIgnored = false;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        Set<Integer> injectionIdSet = Stream.of(injectionIds.split(",")).filter(it -> it.length() > 0).map(String::trim).map(Integer::parseInt).collect(Collectors.toSet());
        Set<Integer> sampleIdSet = Stream.of(sampleIds.split(",")).filter(it -> it.length() > 0).map(String::trim).map(Integer::parseInt).collect(Collectors.toSet());
        Set<Integer> plateIdSet = Stream.of(plateIds.split(",")).filter(it -> it.length() > 0).map(String::trim).map(Integer::parseInt).collect(Collectors.toSet());

        List<Injection> newInjectionList = original.getColumnKeys().stream().
                filter(it -> !injectionIdSet.contains(it.getId()) && !sampleIdSet.contains(it.getSample().getId()) &&
                        !plateIdSet.contains(it.getPlate().getId()) && (!removeIgnored || !it.isIgnored())).collect(Collectors.toList());

        IntensityMatrix matrix = new IntensityMatrixImpl(original.getRowKeys().size(), newInjectionList.size());
        matrix.setColumnKeys(newInjectionList);
        matrix.setRowKeys(original.getRowKeys());

        for (Compound compound : original.getRowKeys()) {
            for (Injection injection : newInjectionList) {
                matrix.put(compound, injection, original.get(compound, injection));
            }
        }

        return matrix;
    }
}
