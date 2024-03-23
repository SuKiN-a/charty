package charty;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class UI {
    Chart chart;
    ListView<Node> segments = new ListView<>();
    Scene scene = new Scene(new GridPane(), 800, 600);
    Stage stage;
    MenuBar menuBar;

    UI(Stage stage, MenuBar menuBar) {
        this.stage = stage;
        this.menuBar = menuBar;

        var instructionsLine1 = new Label("Go to File > New to open a new chart project");
        var instructionsLine2 = new Label("Go to File > Open to open an existing project");

        GridPane.setHgrow(menuBar, Priority.ALWAYS);
        GridPane.setVgrow(instructionsLine1, Priority.ALWAYS);
        GridPane.setHgrow(instructionsLine1, Priority.ALWAYS);
        GridPane.setVgrow(instructionsLine2, Priority.ALWAYS);
        GridPane.setHgrow(instructionsLine2, Priority.ALWAYS);
        GridPane.setHalignment(instructionsLine1, HPos.CENTER);
        GridPane.setHalignment(instructionsLine2, HPos.CENTER);
        GridPane.setValignment(instructionsLine2, VPos.TOP);

        var pane = new GridPane();

        pane.addRow(0, menuBar);
        pane.addRow(1, instructionsLine1);
        pane.addRow(2, instructionsLine2);

        scene.setRoot(pane);
        stage.setScene(scene);
    }

    void setChart(Chart chart) {
        this.chart = chart;
        this.segments.setItems(chart.getInputRecievers());

        var add = new Button("Add");
        add.setOnAction(actionEvent -> chart.createInputReceiver());
        add.setMaxWidth(Double.MAX_VALUE);

        var pane = new GridPane();
        GridPane.setVgrow(segments, Priority.ALWAYS);
        GridPane.setHgrow(chart.node(), Priority.ALWAYS);
        GridPane.setHgrow(add, Priority.SOMETIMES);
        GridPane.setFillWidth(add, true);

        GridPane.setHalignment(chart.node(), HPos.CENTER);
        GridPane.setHalignment(add, HPos.CENTER);

        pane.add(menuBar, 0, 0, 2, 1);
        pane.add(chart.node(), 1, 1, 1, 2);
        pane.add(add, 0, 1);
        pane.add(segments, 0, 2);

        scene.setRoot(pane);
    }
}
