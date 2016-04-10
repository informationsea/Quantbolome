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
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.AlertHelper;
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.MainWindowController;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.InvalidSampleInfoFormatException;
import jp.ac.tohoku.ecei.sb.metabolome.lims.progenesis.ProgenesisLoader;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/28.
 */
public class TemplateCreatorController {
    @FXML
    private TextField textProgenesisCSV;

    @FXML
    private TextField textSampleInfoXlsx;

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

        File inputCSVPath = new File(textProgenesisCSV.getText());
        File outputXlsxPath = new File(textSampleInfoXlsx.getText());

        try (InputStream is = new FileInputStream(inputCSVPath)) {
            Workbook workbook = ProgenesisLoader.createTemplateXlsxFile(is);
            try (OutputStream os = new FileOutputStream(outputXlsxPath)) {
                workbook.write(os);
            }

            new Alert(Alert.AlertType.INFORMATION, "Template xlsx file was created successfully").show();
        } catch (IOException|InvalidSampleInfoFormatException e1) {
            e1.printStackTrace();
            AlertHelper.showExceptionAlert("Cannot create template excel", null, e1);
        }
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
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        textSampleInfoXlsx.setText(file.getAbsolutePath());
    }
}
