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

package jp.ac.tohoku.ecei.sb.metabolome.lims.gui.progenesis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jp.ac.tohoku.ecei.sb.metabolome.lims.DataManager;
import jp.ac.tohoku.ecei.sb.metabolome.lims.ZipDataLoader;
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.AlertHelper;
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.MainWindowController;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.InvalidSampleInfoFormatException;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.ProgenesisLoader;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/28.
 */
public class ProgenesisImporterController {
    @FXML
    private TextField textProgenesisCSV;

    @FXML
    private TextField textSampleInfoXlsx;

    @FXML
    private TextField textOutput;

    @Setter
    MainWindowController mainWindowController;

    @Setter
    Stage stage;

    @FXML
    void onImport(ActionEvent event) {

        if (textProgenesisCSV.getText().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Progenesis CSV is not selected").show();
            return;
        }

        if (textSampleInfoXlsx.getText().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Sample info xlsx is not selected").show();
            return;
        }

        if (textOutput.getText().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Output Zip is not selected").show();
            return;
        }

        File outputZipFile = new File(textOutput.getText());

        try (InputStream is1 = new FileInputStream(new File(textProgenesisCSV.getText())); InputStream is2 = new FileInputStream(new File(textSampleInfoXlsx.getText()))) {
            DataManager dataManager = ProgenesisLoader.loadFromProgenesisAndSampleInfo(is1, is2);
            ZipDataLoader.storeToZip(outputZipFile, dataManager);

            mainWindowController.openData(outputZipFile);
            stage.close();
        } catch (IOException |SQLException |InvalidSampleInfoFormatException e1) {
            e1.printStackTrace();

            StringBuilder buffer = new StringBuilder();
            buffer.append(e1.toString()).append("\n");
            Throwable th = e1;
            while ((th = th.getCause()) != null) {
                buffer.append(th.toString()).append("\n");
            }

            AlertHelper.showExceptionAlert("Cannot import", null, e1);
        }
    }

    @FXML
    void onOutputSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Zipped file", "*.zip"),
                new FileChooser.ExtensionFilter("Flat files", "*"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        textOutput.setText(file.getAbsolutePath());
    }

    @FXML
    void onProgenesisSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ProgenesisQI CSV");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("ProgenesisQI CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        textProgenesisCSV.setText(file.getAbsolutePath());
    }

    @FXML
    void onSampleInfoSelect(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Sample Information Excel");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        textSampleInfoXlsx.setText(file.getAbsolutePath());
    }
}
