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
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.ProgenesisLoader;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.MetabolomeContext;
import org.kohsuke.args4j.Argument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/23.
 */
public class ImportProgenesis implements ManagedCommand {

    @Argument(required = true, usage = "Sample Information xlsx", metaVar = "XLSX")
    private File sampleXlsx;

    @Argument(required = true, usage = "ProgenesisQI generated CSV", index = 1)
    private File progenesis;


    private MetabolomeContext context = null;

    @Override
    public CommandResult execute() throws Exception {
        try (InputStream xlsxInput = new FileInputStream(sampleXlsx)) {
            try (InputStream progenesis = new FileInputStream(this.progenesis)) {
                DataManager dataManager = ProgenesisLoader.loadFromProgenesisAndSampleInfo(progenesis, xlsxInput);
                context.setDataManager(dataManager);
            }
        }
        return new CommandResult(null, CommandResult.ResultState.SUCCESS);
    }

    @Override
    public void setContext(Object context) {
        this.context = (MetabolomeContext) context;
    }
}
