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

import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.CompoundRemover;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoggingIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.CorrectionCommand;
import org.kohsuke.args4j.Option;

/**
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/04.
 */
public class CompoundRemoverCommand extends CorrectionCommand {


    @Option(name = "-minimum-mz", usage = "Minimum M/Z to remove")
    private double minimumMZ = 0;

    @Option(name = "-maximum-mz", usage = "Maximum M/Z to remove")
    private double maximumMZ = Double.MAX_VALUE;

    @Option(name = "-minimum-retention-time", usage = "Minimum retention time to remove")
    private double minimumRetentionTime = 0;

    @Option(name = "-maximum-retention-time", usage = "Maximum retention time to remove")
    private double maximumRetentionTime = Double.MAX_VALUE;

    @Override
    public LoggingIntensityCorrector getCorrector() {
        return new CompoundRemover(minimumMZ, maximumMZ, minimumRetentionTime, maximumRetentionTime);
    }
}
