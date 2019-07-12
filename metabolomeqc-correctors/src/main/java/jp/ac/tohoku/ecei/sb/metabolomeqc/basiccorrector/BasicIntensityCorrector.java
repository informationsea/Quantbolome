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
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

/**
 * This class assume input matrix was log corrected
 */
public class BasicIntensityCorrector extends AdaptiveIntensityCorrector {
    public BasicIntensityCorrector(int badQCThreshold, File fixedBaseIntensity) {
        super(badQCThreshold, fixedBaseIntensity);
    }

    @Override
    public IntensityMatrix doCorrection(IntensityMatrix original) {
        return super.doCorrection(new MedianIntensityCorrector().doCorrection(original));
    }
}
