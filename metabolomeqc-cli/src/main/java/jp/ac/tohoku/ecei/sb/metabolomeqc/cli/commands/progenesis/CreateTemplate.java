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

package jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.progenesis;

import info.informationsea.commandmanager.core.CommandResult;
import info.informationsea.commandmanager.core.ManagedCommand;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.InvalidSampleInfoFormatException;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.ProgenesisLoader;
import org.apache.poi.ss.usermodel.Workbook;
import org.kohsuke.args4j.Argument;

import java.io.*;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/23.
 */
public class CreateTemplate implements ManagedCommand {

    @Argument(required = true, usage = "A ProjenesisQI generated CSV")
    private File file = null;

    @Argument(required = true, usage = "Output Xlsx File", index = 1, metaVar = "OUTPUT")
    private File output = null;

    @Override
    public CommandResult execute() throws Exception {
        try (OutputStream os = new FileOutputStream(output)) {
            try (InputStream is = new FileInputStream(file)) {
                Workbook workbook = ProgenesisLoader.createTemplateXlsxFile(is);
                workbook.write(os);
            } catch (InvalidSampleInfoFormatException e) {
                System.err.printf("Invalid Progenesis File");
                e.printStackTrace();
            }
        }
        return new CommandResult(null, CommandResult.ResultState.SUCCESS);
    }
}
