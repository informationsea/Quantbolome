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

import info.informationsea.dataclustering4j.matrix.aggregate.Aggregate;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * metabolomeqc-next
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 15/08/06.
 */
@Slf4j @AllArgsConstructor @NoArgsConstructor
public class LowIntensityInjectionFilter extends LoggingIntensityCorrector {

    @Getter
    @Setter
    private long numberThresholdOfLowIntensity = 100;

    @Setter @Getter
    private double lowIntensityThreshold = 0.001;

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        return null;
    }

    private class CountLowIntensity<T extends Number> implements Aggregate<T, Long> {

        @Override
        public Long process(List<T> array) {
            return array.stream().filter(v -> v.doubleValue() < lowIntensityThreshold).count();
        }
    }
}
