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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Injection;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.IntensityMatrix;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.helper.BadInjectionFinder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * metabolomeqc-next
 * Copyright (C) 2015 OKAMURA Yasunobu
 * Created on 15/07/26.
 */
public abstract class IntensityCorrectorWithBadSamples extends LoggingIntensityCorrector {

    @Getter @Setter
    protected List<Injection> badInjections = null;

    @Getter @Setter
    protected int badQCThreshold = BadInjectionFinder.DEFAULT_BAD_SAMPLE_THRESHOLD;

    protected void updateBadInjections(IntensityMatrix matrix) {
        if (badInjections == null)
            badInjections = BadInjectionFinder.findBadSamples(matrix, badQCThreshold);
    }
}
