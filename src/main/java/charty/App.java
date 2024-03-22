package charty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application {
    Chart chart;
    File saveFile;
    UI ui;

    @Override
    public void start(Stage stage) {
        ui = new UI(stage, menuBar(stage));

        stage.setTitle("charty - A very cleverly named chart maker");
        stage.show();
    }

    MenuBar menuBar(Stage stage) {
        var bar = new MenuBar();

        var fileMenu = new Menu("File");
        bar.getMenus().addAll(fileMenu);

        var newSubMenu = new Menu("New");
        var newPieChart = new MenuItem("pie chart");
        newSubMenu.getItems().addAll(newPieChart);
        newSubMenu.setOnAction(actionEvent -> newPieChart(stage));

        var save = new MenuItem("Save");
        save.setOnAction(actionEvent -> save(stage));

        var load = new MenuItem("Open");
        load.setOnAction(actionEvent -> load(stage));

        var export = new Menu("export");
        var exportPng = new MenuItem("as png");
        var exportJpeg = new MenuItem("as jpeg");
        export.getItems().addAll(exportPng, exportJpeg);
        fileMenu.getItems().addAll(newSubMenu, save, load, export);

        exportPng.setOnAction(x -> {
            if (chart == null) {
                new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
                return;
            }
            ImageExporter.exportPng(saveFile, chart);
        });
        exportJpeg.setOnAction(x -> {
            if (chart == null) {
                new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
                return;
            }
            ImageExporter.exportJpeg(saveFile, chart);
        });

        return bar;
    }

    void newPieChart(Stage stage) {
        chart = Pie.empty();
        ui.setChart(chart);
    }

    void load(Stage stage) {
        if (chart == null) {
            new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
            return;
        }
        var openFileChooser = new FileChooser();
        openFileChooser.setTitle("Open chart project");
        saveFile = openFileChooser.showOpenDialog(stage);
        try {
            chart = Chart.load(saveFile);
            ui.setChart(chart);
        } catch (ClassNotFoundException e) {
            new Alert(AlertType.ERROR, "Invalid save file", ButtonType.CLOSE).showAndWait();
        } catch (FileNotFoundException e) {
            new Alert(AlertType.ERROR, "File not found", ButtonType.CLOSE).showAndWait();
        } catch (IOException e) {
            // silence and rethrow because not much can be done about these.
            throw new RuntimeException(e);
        }
    }

    void save(Stage stage) {
        var saveFileChooser = new FileChooser();
        saveFileChooser.setTitle("Save as");

        var saveFile = saveFileChooser.showSaveDialog(stage);
        if (saveFile == null) {
            return;
        }

        try {
            saveFile.delete();
            saveFile.createNewFile();
            this.saveFile = saveFile;
            Chart.save(chart, saveFile);
        } catch (IOException e) {
            // silence and rethrow because not much can be done about these.
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}