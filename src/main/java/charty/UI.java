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

/**
 * Controls the UI of the application.
 * This class contains the UI state and the transitions between them.
 */
public class UI {
    private ListView<Node> segments = new ListView<>();
    private Scene scene = new Scene(new GridPane(), 800, 600);
    private MenuBar menuBar;

    /**
     * Creates a new UI.
     * 
     * @param stage   Stage to apply the UI to.
     * @param menuBar
     */
    public UI(Stage stage, MenuBar menuBar) {
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

    /**
     * Updates the UI with the new chart.
     * This function should be called every time charty needs to switch to another
     * chart.
     * 
     * @param chart Chart to switch the UI to.
     */
    public void setChart(Chart chart) {
        this.segments.setItems(chart.getInputRecievers());

        var add = new Button("Add");
        add.setOnAction(actionEvent -> chart.createInputReceiver());
        add.setMaxWidth(Double.MAX_VALUE);

        var clear = new Button("Clear");
        clear.setOnAction(actionEvent -> chart.onClear());
        clear.setMaxWidth(Double.MAX_VALUE);

        var pane = new GridPane();
        GridPane.setVgrow(segments, Priority.ALWAYS);

        GridPane.setHgrow(chart.getNode(), Priority.ALWAYS);

        GridPane.setHgrow(add, Priority.SOMETIMES);
        GridPane.setFillWidth(add, true);

        GridPane.setHgrow(clear, Priority.SOMETIMES);
        GridPane.setFillWidth(clear, true);

        GridPane.setHalignment(chart.getNode(), HPos.CENTER);
        GridPane.setHalignment(add, HPos.CENTER);

        pane.add(menuBar, 0, 0, 2, 1);
        pane.add(chart.getNode(), 2, 1, 2, 2);
        pane.add(add, 0, 1);
        pane.add(clear, 1, 1);
        pane.add(segments, 0, 2, 2, 1);

        scene.setRoot(pane);
    }
}
