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

import jp.ac.tohoku.ecei.sb.metabolome.lims.data.Sample;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoggingIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LowIntensityCompoundFilter;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.CorrectionCommand;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.Option;

/**
 * Created by yasu on 15/08/05.
 */
public class LowIntensityFilterCommand extends CorrectionCommand {

    @Getter @Setter @Option(name = "-number-threshold-of-low-intensity", usage = "The threshold of a number of low intensities")
    private long numberThresholdOfLowIntensity = 100;

    @Setter @Getter @Option(name = "-low-intensity-threshold", usage = "The threshold of low intensity")
    private double lowIntensityThreshold = 0.001;

    @Setter @Getter @Option(name = "-sample-type", usage = "Sample Type (keep null for all types)")
    private Sample.SampleType sampleType = null;

    @Override
    public LoggingIntensityCorrector getCorrector() {
        return new LowIntensityCompoundFilter(numberThresholdOfLowIntensity, lowIntensityThreshold, sampleType);
    }
}
