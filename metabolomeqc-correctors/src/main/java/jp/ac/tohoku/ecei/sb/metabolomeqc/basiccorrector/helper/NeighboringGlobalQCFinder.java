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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Plate;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/12
 */
@Slf4j
public class NeighboringGlobalQCFinder {
    private NeighboringGlobalQCFinder(){}

    /**
     * Find Neighboring SQC samples next to normal sample
     *
     * @param mim metabolome intensity matrix to search
     * @param badInjections black list of injections
     * @return a map of a normal sample and a SQC samples pair
     */
    public static Map<Injection, NeighboringGlobalQC> findNeighboringSQC(IntensityMatrix mim, List<Injection> badInjections) {
        Map<Plate, Map<Sample.SampleType, List<Injection>>> samplesByRunAndType = mim.getInjectionsByPlateAndType();
        Map<Injection, NeighboringGlobalQC> neighboringSQCMap = new HashMap<>();
        List<Sample> globalQCIndexes = mim.getGlobalQCSamples();
        if (globalQCIndexes.size() == 0)
            throw new UnsupportedOperationException("No global QC");
        Sample selectedGlobalQC = globalQCIndexes.get(0);

        for (Map.Entry<Plate, Map<Sample.SampleType, List<Injection>>> oneRun : samplesByRunAndType.entrySet()) {

            Function<List<Injection>, Plate> processor = (x) -> {
                if (x == null) return null;
                for (Injection sample : x) {
                    if (sample.getSample().getSampleType() == Sample.SampleType.QC)
                        if (sample.getSample().equals(selectedGlobalQC)) continue;
                    Injection left = null, right = null;

                    for (Injection globalQCInjection : oneRun.getValue().get(Sample.SampleType.QC)) {
                        if (!globalQCInjection.getSample().equals(selectedGlobalQC)) continue;
                        if (globalQCInjection.isIgnored()) continue;
                        if (badInjections != null && badInjections.contains(globalQCInjection)) continue; // do not use bad sample as neighboring SQC

                        if (globalQCInjection.getRunIndex() < sample.getRunIndex()) {
                            left = globalQCInjection;
                        } else if (globalQCInjection.getRunIndex() > sample.getRunIndex()) {
                            right = globalQCInjection;
                            break;
                        }
                    }

                    neighboringSQCMap.put(sample, new NeighboringGlobalQC(left, right));
                }
                return null;
            };

            processor.apply(oneRun.getValue().get(Sample.SampleType.NORMAL));
            processor.apply(oneRun.getValue().get(Sample.SampleType.QC));
            processor.apply(oneRun.getValue().get(Sample.SampleType.BLANK));
        }
        return neighboringSQCMap;
    }

    @Value
    public static class NeighboringGlobalQC {
        public Injection leftGlobalQC;
        public Injection rightGlobalQC;
    }

}
