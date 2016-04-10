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
import jp.ac.tohoku.ecei.sb.metabolome.lims.VersionResolver;

/**
 * Created by yasu on 15/08/05.
 */
public class VersionCommand implements ManagedCommand {
    @Override
    public CommandResult execute() {
        String version = String.format("Copyright (C) 2014-2016 Yasunobu Okamura All Rights Reserved.\n" +
                "DO NOT USE OUTSIDE OF TOHOKU MEDICAL MEGABANK IN TOHOKU UNIVERSITY.\n" +
                "DO NOT REUSE OR DIVERT TO OTHER PURPOSE TO OTHER PURPOSE.\n" +
                "DO NOT PUBLISH THIS SOFTWARE WITHOUT PERMISSION.\n" +
                "\n" +
                "   Version: %s\n" +
                "Git commit: %s\n" +
                "Build date: %s\n",
                VersionResolver.getVersion(VersionResolver.class),
                VersionResolver.getGitCommit(VersionResolver.class),
                VersionResolver.getBuildDate(VersionResolver.class));

        return new CommandResult(version, CommandResult.ResultState.SUCCESS);
    }
}
