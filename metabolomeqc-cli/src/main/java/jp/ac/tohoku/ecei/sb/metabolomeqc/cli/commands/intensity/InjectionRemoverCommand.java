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

import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.InjectionRemover;
import jp.ac.tohoku.ecei.sb.metabolomeqc.basiccorrector.LoggingIntensityCorrector;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.CorrectionCommand;
import org.kohsuke.args4j.Option;

/**
 * MetabolomeLIMS
 * Copyright (C) 2016 Yasunobu OKAMURA
 * Created at 2016/02/04.
 */
public class InjectionRemoverCommand extends CorrectionCommand {

    @Option(name = "-injection-ids", usage = "Injection IDs with comma separators")
    private String injectionIds = "";

    @Option(name = "-sample-ids", usage = "Sample IDs with comma separators")
    private String sampleIds = "";

    @Option(name = "-plate-ids", usage = "Plate IDs with comma separators")
    private String plateIds = "";

    @Option(name = "-remove-ignored", usage = "Plate IDs with comma separators")
    private boolean removeIgnored = false;

    @Override
    public LoggingIntensityCorrector getCorrector() {
        return new InjectionRemover(injectionIds, sampleIds, plateIds, removeIgnored);
    }
}
