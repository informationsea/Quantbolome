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

package jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands;

import info.informationsea.commandmanager.core.CommandResult;
import info.informationsea.commandmanager.core.ManagedCommand;
import jp.ac.tohoku.ecei.sb.metabolome.lims.CSVDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.ZipDataLoader;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.MetabolomeContext;
import org.kohsuke.args4j.Argument;

import java.io.File;

/**
 * Created by yasu on 15/08/05.
 */
public class LoadCommand implements ManagedCommand {

    @Argument(required = true, usage = "A file to load")
    private File file = null;

    private MetabolomeContext context = null;

    @Override
    public CommandResult execute() throws Exception {
        if (file.getName().endsWith(".zip")) {
            context.setDataManager(ZipDataLoader.loadFromZip(file));
        } else {
            context.setDataManager(CSVDataLoader.loadFromCSVData(file));
        }
        return new CommandResult(null, CommandResult.ResultState.SUCCESS);
    }

    @Override
    public void setContext(Object context) {
        this.context = (MetabolomeContext) context;
    }
}
