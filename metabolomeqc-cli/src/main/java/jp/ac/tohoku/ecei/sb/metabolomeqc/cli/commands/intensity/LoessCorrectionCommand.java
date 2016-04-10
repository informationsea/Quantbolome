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

package jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.intensity;

import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoessIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoggingIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.CorrectionCommand;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.kohsuke.args4j.Option;

/**
 * Created by yasu on 15/08/05.
 */
public class LoessCorrectionCommand extends CorrectionCommand{

    @Option(name = "-bandWidth", usage = "Loess Parameter")
    double bandwidth = LoessInterpolator.DEFAULT_BANDWIDTH;
    @Option(name = "-robustnessIters", usage = "Loess Parameter")
    int robustnessIters = LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS;
    @Option(name = "-accuracy", usage = "Loess Parameter")
    double accuracy = LoessInterpolator.DEFAULT_ACCURACY;


    @Override
    public LoggingIntensityCorrector getCorrector() {
        return new LoessIntensityCorrector(bandwidth, robustnessIters, accuracy);
    }
}
