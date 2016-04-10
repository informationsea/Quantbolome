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

import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.CompoundCVFilter;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoggingIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.CorrectionCommand;
import org.kohsuke.args4j.Option;

/**
 * Created by yasu on 15/08/05.
 */
public class CVFilterCommand extends CorrectionCommand {

    @Option(name = "-cv-threshold", required = true, usage = "The threshold of CV value")
    double CVThreshold;

    @Override
    public LoggingIntensityCorrector getCorrector() {
        return new CompoundCVFilter(CVThreshold);
    }
}
