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

package jp.ac.tohoku.ecei.sb.metabolomeqc.cli;

import info.informationsea.commandmanager.cli.CLICommandConsole;
import info.informationsea.commandmanager.core.CommandManager;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.LoadCommand;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.SaveCommand;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.VersionCommand;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.intensity.*;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.progenesis.CreateTemplate;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.commands.progenesis.ImportProgenesis;

import java.io.IOException;

/**
 * MetabolomeQC
 * Copyright (C) 2014 Yasunobu OKAMURA
 * Created at 14/12/01
 */
public class MetabolomeQC {

    public static void main(String[] argv) {
        MetabolomeContext context = new MetabolomeContext();
        CommandManager commandManager = new CommandManager();
        commandManager.setContext(context);
        commandManager.addCommand("load", LoadCommand.class);
        commandManager.addCommand("save", SaveCommand.class);
        commandManager.addCommand("version", VersionCommand.class);
        commandManager.addCommand("progenesis-import", ImportProgenesis.class);
        commandManager.addCommand("progenesis-create-template", CreateTemplate.class);
        addCorrectionCommands(commandManager);

        CLICommandConsole cliCommandConsole = new CLICommandConsole(commandManager);

        if (argv.length > 0) {
            try {
                cliCommandConsole.executeMany(argv);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        System.err.println("             _        _           _                              ____  ___ \n" +
                "  /\\/\\   ___| |_ __ _| |__   ___ | | ___  _ __ ___   ___        /___ \\/ __\\\n" +
                " /    \\ / _ \\ __/ _` | '_ \\ / _ \\| |/ _ \\| '_ ` _ \\ / _ \\_____ //  / / /   \n" +
                "/ /\\/\\ \\  __/ || (_| | |_) | (_) | | (_) | | | | | |  __/_____/ \\_/ / /___ \n" +
                "\\/    \\/\\___|\\__\\__,_|_.__/ \\___/|_|\\___/|_| |_| |_|\\___|     \\___,_\\____/ \n" +
                "                                                                           \n");
        new VersionCommand().execute();
        System.err.println();

        try {
            cliCommandConsole.startConsole();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addCorrectionCommands(CommandManager commandManager) {
        commandManager.addCommand("basic-correction", BasicCorrectionCommand.class);
        commandManager.addCommand("logarithm-correction", LogarithmCorrectionCommand.class);
        commandManager.addCommand("median-correction", MedianCorrectionCommand.class);
        commandManager.addCommand("adaptive-correction", AdaptiveCorrectionCommand.class);
        commandManager.addCommand("loess-correction", LoessCorrectionCommand.class);
        commandManager.addCommand("linear-correction", LinearCorrectionCommand.class);
        commandManager.addCommand("regression-correction", RegressionCorrectionCommand.class);
        commandManager.addCommand("cv-filter", CVFilterCommand.class);
        commandManager.addCommand("low-intensity-filter", LowIntensityFilterCommand.class);
        commandManager.addCommand("injection-remover", InjectionRemoverCommand.class);
        commandManager.addCommand("compound-remover", CompoundRemoverCommand.class);
    }
}
