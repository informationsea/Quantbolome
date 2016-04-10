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

package jp.ac.tohoku.ecei.sb.metabolome.lims.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/27.
 */
public class AlertHelper {
    public static void showExceptionAlert(String headerText, String message, Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);

        if (message != null) {
            alert.setContentText(message);
        } else {
            alert.setContentText(th.getMessage());
        }

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
}
