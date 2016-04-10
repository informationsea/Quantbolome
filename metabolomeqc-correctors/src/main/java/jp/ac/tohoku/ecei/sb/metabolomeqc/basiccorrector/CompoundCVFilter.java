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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Filter by coefficient of variation value.
 *
 * Calculate CV values by plates, and filter by max value of CV.
 */
@AllArgsConstructor @NoArgsConstructor @Slf4j
public class CompoundCVFilter extends LoggingIntensityCorrector {

    @Getter @Setter
    double CVThreshold = 10;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        List<Sample> globalQC = original.getGlobalQCSamples();
        if (globalQC.size() == 0) throw new IllegalArgumentException("No global QC");
        Sample selectedGlobalQC = globalQC.get(0);

        log.info("selected global QC {}", selectedGlobalQC);

        Map<Integer, Map<Plate, Double>> compound2cv = new HashMap<>();
        Map<Integer, Compound> id2compound = new HashMap<>();

        for (Compound oneCompound : original.getRowKeys()) {
            compound2cv.put(oneCompound.getId(), new HashMap<>());
            id2compound.put(oneCompound.getId(), oneCompound);
        }

        for (Compound oneCompound : original.getRowKeys()) {
            for (Map.Entry<Plate, List<Injection>> onePlate : original.getInjectionsByPlate().entrySet()) {
                double[] globalInjectionValues = onePlate.getValue().stream().filter(i -> i.getSample().equals(selectedGlobalQC) && !i.isIgnored()).mapToDouble(i -> original.get(oneCompound, i)).toArray();
                double mean = DoubleStream.of(globalInjectionValues).sum()/globalInjectionValues.length;
                StandardDeviation standardDeviation = new StandardDeviation();
                double sd = standardDeviation.evaluate(globalInjectionValues, mean);
                double cv = sd/mean;

                compound2cv.get(oneCompound.getId()).put(onePlate.getKey(), cv);
                oneCompound.setAttribute(String.format("Plate-%d-CV", onePlate.getKey().getId()), String.valueOf(cv));
            }
        }

        Map<Integer, Double> maxcv = new HashMap<>();
        List<Compound> newCompounds = new ArrayList<>();
        for (Map.Entry<Integer, Map<Plate, Double>> one : compound2cv.entrySet()) {
            maxcv.put(one.getKey(), one.getValue().values().stream().max(Double::compare).get());
            id2compound.get(one.getKey()).setAttribute("MaxCV", maxcv.get(one.getKey()));
            if (maxcv.get(one.getKey()) < CVThreshold) {
                newCompounds.add(id2compound.get(one.getKey()));
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
