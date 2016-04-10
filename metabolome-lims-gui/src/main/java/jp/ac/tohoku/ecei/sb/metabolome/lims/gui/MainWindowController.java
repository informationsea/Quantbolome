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

import info.informationsea.commandmanager.core.CommandManager;
import info.informationsea.commandmanager.gui.GUICommandPaneFactory;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.ac.tohoku.ecei.sb.metabolome.lims.*;
import jp.ac.tohoku.ecei.sb.metabolome.lims.data.*;
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.progenesis.ProgenesisImporterController;
import jp.ac.tohoku.ecei.sb.metabolome.lims.gui.progenesis.TemplateCreatorController;
import jp.ac.tohoku.ecei.sb.metabolome.lims.impl.*;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.MetabolomeContext;
import jp.ac.tohoku.ecei.sb.metabolomeqc.cli.MetabolomeQC;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.fx.FXGraphics2D;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MetabolomeLIMS
 * Copyright (C) 2015 Yasunobu OKAMURA
 * Created at 2015/08/27.
 */
@Slf4j
public class MainWindowController implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu correctionMenu;

    @FXML
    private TableView<StudyImpl> tableStudy;

    @FXML
    private TableView<PlateImpl> tablePlate;

    @FXML
    private TableView<SampleImpl> tableSample;

    @FXML
    private TableView<InjectionImpl> tableInjection;

    @FXML
    private TableView<CompoundImpl> tableCompound;

    @FXML
    private TableView<OperationHistory> tableHistory;

    @FXML
    private Tab tabPlateIntensity;

    @FXML
    private StackPane stackPlateIntensity;

    @FXML
    private ChoiceBox<PlateImpl> choicePlate;

    @FXML
    private Tab globalQCIntensity;

    @FXML
    private Button buttonCompoundShowIntensity;

    @FXML
    private StackPane stackGlobalQCIntensity;

    private DataManager dataManager;
    private CommandManager commandManager;
    MetabolomeContext commandManagerContext = new MetabolomeContext();

    @Getter @Setter
    private boolean opened = false;

    @Setter @Getter
    private Stage stage = null;

    public MainWindowController() {
        try {
            dataManager = new DataManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Version: " + VersionResolver.getVersion(getClass()) + "\n" +
                "Git Commit: " + VersionResolver.getGitCommit(getClass()) + "\n" +
                "Build date: " + VersionResolver.getBuildDate(getClass()) + "\n\n" +
        "Copyright (C) 2014-2016 Yasunobu Okamura All Rights Reserved.\n");
        alert.setHeaderText("Metabolome Analysis");
        alert.show();
    }

    @FXML
    void onExportAsExcel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel file", "*.xlsx"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            ExcelDataLoader.storeToExcel(file, dataManager);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            AlertHelper.showExceptionAlert("Cannot export", null, e);
        }
    }

    @FXML
    void onNew(ActionEvent event) {
        try {
            dataManager = new DataManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        onRefresh(event);
    }

    @FXML
    void onOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Metabolome Analysis File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Supported Files", Arrays.asList("*.zip", "*.csv")));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        if (!opened) {
            openData(file);
        } else {
            try {
                Stage primaryStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
                loader.load();
                MainWindowController controller = loader.getController();
                controller.setStage(primaryStage);
                VBox root = loader.getRoot();
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                controller.openData(file);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AlertHelper.showExceptionAlert("Cannot open", null, e);
            }
        }
    }

    public void openData(File file) {
        try {
            if (file.getName().endsWith(".zip")) {
                dataManager = ZipDataLoader.loadFromZip(file);
                stage.setTitle(file.getName());
            } else {
                dataManager = CSVDataLoader.loadFromCSVData(file.getParentFile());
                stage.setTitle(file.getParentFile().getName());
            }
            opened = true;
            onRefresh(null);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showExceptionAlert("Cannot open metabolome file", null, e);
        }
    }

    @FXML
    void onPreferences(ActionEvent event) {

    }

    @FXML
    void onProgenesisCreateTemplate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("progenesis/createtemplate.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            TemplateCreatorController controller = loader.getController();
            controller.setMainWindowController(this);
            controller.setStage(stage);
            stage.setTitle("Create Sample Info Template from Progenesis Output");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onProgenesisImport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("progenesis/importer.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            ProgenesisImporterController controller = loader.getController();
            controller.setMainWindowController(this);
            controller.setStage(stage);
            stage.setTitle("Import data from Progenesis CSV and Sample Info Excel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onQuit(ActionEvent event) {
        stage.close();
    }

    @FXML
    void onRefresh(ActionEvent event) {
        try {
            tableStudy.setItems(FXCollections.observableArrayList(dataManager.getStudies().queryForAll()));
            tablePlate.setItems(FXCollections.observableArrayList(dataManager.getPlates().queryForAll()));
            tableSample.setItems(FXCollections.observableArrayList(dataManager.getSamples().queryForAll()));
            tableInjection.setItems(FXCollections.observableArrayList(dataManager.getInjections().queryForAll()));
            tableCompound.setItems(FXCollections.observableArrayList(dataManager.getCompounds().queryForAll()));
            tableHistory.setItems(FXCollections.observableArrayList(dataManager.getOperationHistories().queryForAll()));

            choicePlate.setItems(FXCollections.observableArrayList(dataManager.getPlates().queryForAll()));
            choicePlate.valueProperty().addListener(e -> {
                refreshPlateIntensity(choicePlate.getSelectionModel().getSelectedItem());
            });
            if (choicePlate.getItems().size() > 0)
                refreshPlateIntensity(choicePlate.getSelectionModel().getSelectedItem());
            stackPlateIntensity.getChildren().clear();

            JFreeChart chart = getChartForGlobalQC();
            if (chart == null) {
                globalQCIntensity.setDisable(true);
            } else {
                globalQCIntensity.setDisable(false);
                stackGlobalQCIntensity.getChildren().clear();
                ChartCanvas canvas = new ChartCanvas(chart);
                canvas.heightProperty().bind(stackGlobalQCIntensity.heightProperty());
                canvas.widthProperty().bind(stackGlobalQCIntensity.widthProperty());
                stackGlobalQCIntensity.getChildren().add(canvas);
            }

            commandManagerContext.setDataManager(dataManager);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showExceptionAlert("Cannot refresh data", null, e);
        }
    }

    private void refreshPlateIntensity(PlateImpl plate) {
        if (plate == null) return;
        JFreeChart chart = getChartForPlate(plate);

        stackPlateIntensity.getChildren().clear();
        ChartCanvas canvas = new ChartCanvas(chart);
        canvas.heightProperty().bind(stackPlateIntensity.heightProperty());
        canvas.widthProperty().bind(stackPlateIntensity.widthProperty());
        stackPlateIntensity.getChildren().add(canvas);
    }

    @FXML
    void onSaveAs(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip file", "*.zip"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;
        try {
            ZipDataLoader.storeToZip(file, dataManager);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            AlertHelper.showExceptionAlert("Cannot export", null, e);
        }
    }


    @FXML
    void onTableClicked(MouseEvent event) {
        if (event.getClickCount() != 2) return;
        TableView tableView = ((TableView)event.getSource());
        TableView.TableViewFocusModel focusModel = (TableView.TableViewFocusModel) tableView.focusModelProperty().getValue();
        TablePosition position = focusModel.getFocusedCell();
        if (position.getTableColumn() == null) return;
        ObservableValue value = position.getTableColumn().getCellObservableValue(position.getRow());
        String valueStr = value.getValue().toString();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(String.format("Value at ID:%d  %s", ((LIMSData) tableView.getItems().get(position.getRow())).getId(), position.getTableColumn().getText()));
        if (valueStr.length() > 300)
            alert.setContentText(valueStr.substring(0, 300) + "...");
        else
            alert.setContentText(valueStr);

        TextArea textArea = new TextArea(valueStr);
        textArea.setEditable(false);
        alert.getDialogPane().setExpandableContent(textArea);

        event.consume();
        alert.show();
    }


    @FXML
    void onShowCompoundIntensity(MouseEvent event) {
        if (tableCompound.getSelectionModel().isEmpty()) return;
        for (CompoundImpl compound : tableCompound.getSelectionModel().getSelectedItems()) {
            JFreeChart chart = getChartForCompound(compound);

            StackPane stackPane = new StackPane();
            ChartCanvas chartCanvas = new ChartCanvas(chart);
            stackPane.getChildren().add(chartCanvas);
            chartCanvas.widthProperty().bind(stackPane.widthProperty());
            chartCanvas.heightProperty().bind(stackPane.heightProperty());

            Scene scene = new Scene(stackPane);
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle(compound.toString());
            stage.show();
        }
    }

    @FXML
    void onShowCompoundIntensityTable(MouseEvent event) {
        if (tableCompound.getSelectionModel().isEmpty()) return;

        IntensityMatrixImpl intensityMatrix = dataManager.getIntensityMatrix();

        for (CompoundImpl compound : tableCompound.getSelectionModel().getSelectedItems()) {

            TableView<IntensityValue> tableView = new TableView<>(FXCollections.observableArrayList(
                    intensityMatrix.getColumnKeys().stream().map(it ->
                            new IntensityValue(it.getPlate(), it.getSample(), it, intensityMatrix.get(compound, it))
                    ).collect(Collectors.toList())
            ));

            Arrays.asList("Plate", "Sample", "Injection", "Intensity").forEach(it -> {
                TableColumn<IntensityValue, Double> column = new TableColumn<>();
                column.setText(it);
                //noinspection unchecked
                column.setCellValueFactory(new PropertyValueFactory(it));
                tableView.getColumns().add(column);
            });


            Scene scene = new Scene(tableView);
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setTitle(compound.toString());
            stage.show();
        }

    }

    @Value
    public static class IntensityValue {
        public Plate plate;
        public Sample sample;
        public Injection injection;
        public double intensity;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Table Study
        initializeTable(tableStudy, StudyImpl.class);
        initializeTable(tablePlate, PlateImpl.class);
        initializeTable(tableSample, SampleImpl.class);
        initializeTable(tableInjection, InjectionImpl.class);
        initializeTable(tableCompound, CompoundImpl.class);
        initializeTable(tableHistory, OperationHistoryImpl.class);

        menuBar.setUseSystemMenuBar(true);

        commandManager = new CommandManager();
        commandManager.setContext(commandManagerContext);
        final GUICommandPaneFactory commandPaneFactory = new GUICommandPaneFactory(commandManager);
        MetabolomeQC.addCorrectionCommands(commandManager);
        for (Map.Entry<String, Class> commend : commandManager.getCommands().entrySet()) {
            MenuItem menuItem = new MenuItem(commend.getKey());
            menuItem.setText(commend.getKey());
            menuItem.setOnAction(e -> {
                final Stage stage = new Stage();
                stage.initModality(Modality.WINDOW_MODAL);

                Parent guiCommand = commandPaneFactory.getCommandPane(commend.getKey(), (commandEvent, managedCommand) -> {
                    stage.close();
                });
                BorderPane margins  = new BorderPane(guiCommand);
                BorderPane.setMargin(guiCommand, new Insets(10));

                stage.setScene(new Scene(margins));
                stage.initOwner(this.stage);
                stage.showAndWait();
                onRefresh(null);
            });
            correctionMenu.getItems().add(menuItem);
        }

        onRefresh(null);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable(TableView tableView, Class clazz) {
        ArrayList<TableColumn> columns = new ArrayList<>();
        HashSet<String> methodNames = new HashSet<>();
        method: for (Method one : clazz.getMethods()) {
            for (String black : new String[]{"getClass", "getAttributeKeySet"})
                if (one.getName().equals(black)) continue method;
            if (!one.getName().startsWith("get") && !one.getName().startsWith("is")) continue;
            if (one.getParameterCount() != 0) continue;
            if (methodNames.contains(one.getName())) continue;
            methodNames.add(one.getName());

            TableColumn oneColumn = new TableColumn();
            String name = one.getName().substring(3);
            if (one.getName().startsWith("is")) {
                name = one.getName().substring(2);
            }
            oneColumn.setText(name);
            oneColumn.setCellValueFactory(new PropertyValueFactory(name));

            if (one.getName().equals("getId"))
                columns.add(0, oneColumn);
            else
                columns.add(oneColumn);
        }

        tableView.getColumns().addAll(columns.toArray());
    }

    public JFreeChart getChartForCompound(CompoundImpl compound) {
        IntensityMatrixImpl intensityMatrix = dataManager.getIntensityMatrix();
        //DefaultCategoryDataset data = new DefaultCategoryDataset();
        XYSeriesCollection data = new XYSeriesCollection();

        HashMap<Plate, XYSeries> dataMap = new HashMap<>();
        for (Injection injection : intensityMatrix.getColumnKeys()) {
            Plate p = injection.getPlate();
            if (!dataMap.containsKey(p)) {
                XYSeries oneSeries = new XYSeries(p.getName());
                data.addSeries(oneSeries);
                dataMap.put(p, oneSeries);
            }

            dataMap.get(p).add(injection.getRunIndex(), intensityMatrix.get(compound, injection));
        }

        List<Sample> globalQC = intensityMatrix.getGlobalQCSamples();
        if (globalQC.size() > 0) {
            XYSeries globalQCData = new XYSeries("GlobalQC");
            Sample globalQCSample = globalQC.get(0);
            for (Injection injection : intensityMatrix.getInjectionsBySample(globalQCSample)) {
                globalQCData.add(injection.getRunIndex(), intensityMatrix.get(compound, injection));
            }
            data.addSeries(globalQCData);
        }

        return ChartFactory.createXYLineChart(compound.toString(), "Injection", "Intensity", data, PlotOrientation.VERTICAL, true, false, false);
    }

    public JFreeChart getChartForPlate(PlateImpl plate) {
        IntensityMatrixImpl intensityMatrix = dataManager.getIntensityMatrix();
        List<Injection> injections = intensityMatrix.getInjectionsByPlate().get(plate);

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (Injection one : injections) {
            dataset.add(Arrays.asList(intensityMatrix.getColumn(one)).stream().map((o) -> ((Double)o)).collect(Collectors.toList()), "Intensity", one.toString());
        }
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(plate.toString(), "Injection", "Intensity", dataset, true);
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
        return chart;
    }

    public JFreeChart getChartForGlobalQC() {
        IntensityMatrixImpl intensityMatrix = dataManager.getIntensityMatrix();
        if (intensityMatrix == null) return null;
        List<Sample> globalQCList = intensityMatrix.getGlobalQCSamples();
        log.info("global QC {}", globalQCList.size());
        if (globalQCList.size() == 0) return null;
        List<Injection> injections = intensityMatrix.getInjectionsBySample(globalQCList.get(0));

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (Injection one : injections) {
            dataset.add(Arrays.asList(intensityMatrix.getColumn(one)).stream().map((o) -> ((Double)o)).collect(Collectors.toList()), "Intensity", one.toString());
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("Global QC " + globalQCList.get(0).toString(), "Injection", "Intensity", dataset, true);
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
        return chart;
    }

    @Slf4j
    static class ChartCanvas extends Canvas {

        JFreeChart chart;

        private FXGraphics2D g2;

        public ChartCanvas(JFreeChart chart) {
            this.chart = chart;
            this.g2 = new FXGraphics2D(getGraphicsContext2D());
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> draw());
            heightProperty().addListener(evt -> draw());
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();
            getGraphicsContext2D().clearRect(0, 0, width, height);
            this.chart.draw(this.g2, new Rectangle2D.Double(0, 0, width,
                    height));
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) { return 400; }

        @Override
        public double prefHeight(double width) { return 300; }
    }
}
