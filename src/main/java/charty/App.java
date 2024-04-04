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
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The App.
 */
public class App extends Application {
    /**
     * Currently active chart.
     */
    private Chart chart;

    /**
     * File which the app will be saved to.
     */
    private File saveFile;

    /**
     * The UI controller.
     */
    private UI ui;

    /**
     * Starts the app.
     * 
     * @param stage Stage onto which the app is rendered.
     */
    @Override
    public void start(Stage stage) {
        ui = new UI(stage, menuBar(stage));

        stage.setTitle("charty - A very cleverly named chart maker");
        stage.show();
    }

    /**
     * Creates a new menu bar.
     * 
     * @param stage Stage onto which the app is rendered.
     * @return Newly created menu bar.
     */
    MenuBar menuBar(Stage stage) {
        var bar = new MenuBar();

        var fileMenu = new Menu("File");
        bar.getMenus().addAll(fileMenu);

        var newSubMenu = new Menu("New");
        var newPieChart = new MenuItem("Pie chart");
        var newLineChart = new MenuItem("Line chart");
        var newBarChart = new MenuItem("Bar chart");
        newSubMenu.getItems().addAll(newPieChart, newLineChart, newBarChart);
        newPieChart.setOnAction(actionEvent -> newPieChart(stage));
        newLineChart.setOnAction(actionEvent -> newLineChart(stage));
        newBarChart.setOnAction(actionEvent -> newBarChart(stage));

        var save = new MenuItem("Save");
        save.setOnAction(actionEvent -> save(stage));

        var load = new MenuItem("Open");
        load.setOnAction(actionEvent -> load(stage));

        var export = new Menu("Export");
        var exportPng = new MenuItem("As png");
        var exportJpeg = new MenuItem("As jpeg");
        export.getItems().addAll(exportPng, exportJpeg);
        fileMenu.getItems().addAll(newSubMenu, save, load, export);

        exportPng.setOnAction(x -> {
            if (chart == null) {
                new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
                return;
            }
            var f = new FileChooser();
            f.setTitle("Save as");
            f.setSelectedExtensionFilter(new ExtensionFilter("PNG file", "*.png"));
            var file = f.showSaveDialog(stage);
            if (file != null) {
                ImageExporter.exportPng(file, chart);
            }
        });

        exportJpeg.setOnAction(x -> {
            if (chart == null) {
                new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
                return;
            }
            var f = new FileChooser();
            f.setTitle("Save as");
            f.setSelectedExtensionFilter(new ExtensionFilter("JPEG file", "*.jpeg", "*.jpg"));
            var file = f.showSaveDialog(stage);
            if (file != null) {
                ImageExporter.exportPng(file, chart);
            }
        });

        return bar;
    }

    /**
     * Sets the current chart to an empty bar chart and updates the UI.
     * 
     * @param stage Stage onto which the app is rendered.
     */
    void newBarChart(Stage stage) {
        chart = Bar.empty();
        ui.setChart(chart);
    }

    /**
     * Sets the current chart to an empty line chart and updates the UI.
     * 
     * @param stage Stage onto which the app is rendered.
     */
    void newLineChart(Stage stage) {
        chart = Line.empty();
        ui.setChart(chart);
    }

    /**
     * Sets the current chart to an empty pie chart and updates the UI.
     * 
     * @param stage Stage onto which the app is rendered.
     */
    void newPieChart(Stage stage) {
        chart = Pie.empty();
        ui.setChart(chart);
    }

    /**
     * Opens an existing charty project.
     * Asks the user to choose a file, deserializes it into a chart,
     * and updates the UI.
     * 
     * @param stage Stage onto which the app is rendered.
     */
    void load(Stage stage) {
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves current chart into a file.
     * Prompts user for location to save file into
     * and deserializes the current chart into it.
     *
     * @param stage Stage onto which the app is rendered.
     */
    void save(Stage stage) {
        if (chart == null) {
            new Alert(AlertType.ERROR, "No open chart", ButtonType.CLOSE).showAndWait();
            return;
        }

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

    /**
     * Starts charty when invoked
     * 
     * @param args this parameter is ignored.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
