package charty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

/**
 * The LineChart, associated data and input recievers.
 *
 */
public class Line extends Chart {
    /**
     * The LineChart's Points
     */
    ObservableList<XYChart.Data<Number, Number>> state = FXCollections.observableArrayList();

    /**
     * Active recievers.
     */
    ObservableList<Node> recievers = FXCollections.observableArrayList();

    /**
     * Active LineChart instance.
     */
    LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());

    /**
     * Creates a new LineChart from stored data. Creates the appropriate recievers
     * and initializes state.
     * 
     * @param data Initial state of the Line
     */
    Line(List<Pair<Number, Number>> data) {
        ObservableList<XYChart.Series<Number, Number>> l = FXCollections.observableArrayList();
        l.add(new XYChart.Series<>(state));

        chart.setLegendVisible(false);
        chart.setData(l);

        for (Pair<Number, Number> pair : data) {
            createInputReceiver(new XYChart.Data<Number, Number>(pair.getKey(), pair.getValue()));
        }
    }

    /**
     * Creates a default chart.
     * 
     * @return linechart with one defaultish point
     */
    static Line empty() {
        return new Line(Arrays.asList(new Pair<>(1.0, 1.0)));
    }

    @Override
    /** {@inheritDoc} */
    public Node getNode() {
        return chart;
    }

    @Override
    /** {@inheritDoc} */
    public void onClear() {
        state.clear();
        recievers.clear();
    }

    @Override
    /** {@inheritDoc} */
    public Node createInputReceiver() {
        return createInputReceiver(new XYChart.Data<Number, Number>(1.0, 1.0));
    }

    /**
     * Creates new input reciever.
     *
     * @param nodeState Initial state of input receiver.
     * @return newly created input receiver.
     */
    public Node createInputReceiver(XYChart.Data<Number, Number> nodeState) {
        state.add(nodeState);

        Label xLabel = new Label("X");
        var x = new TextField(nodeState.getXValue().toString());
        x.textProperty().addListener((actionEvent, oldVal, newVal) -> {
            if (newVal.equals(""))
                return;
            try {
                nodeState.setXValue(Double.parseDouble(x.getText()));
            } catch (NumberFormatException e) {
                x.setText(oldVal);
            }
        });

        Label yLabel = new Label("Y");
        var y = new TextField(nodeState.getYValue().toString());
        y.textProperty().addListener((actionEvent, oldVal, newVal) -> {
            if (newVal.equals(""))
                return;
            try {
                nodeState.setYValue(Double.parseDouble(y.getText()));
            } catch (NumberFormatException e) {
                y.setText(oldVal);
            }
        });

        var pane = new GridPane();
        recievers.add(pane);

        GridPane.setHgrow(xLabel, Priority.ALWAYS);
        GridPane.setHalignment(xLabel, HPos.CENTER);
        GridPane.setHgrow(yLabel, Priority.ALWAYS);
        GridPane.setHalignment(yLabel, HPos.CENTER);

        pane.addRow(0, xLabel, x);
        pane.addRow(1, yLabel, y);

        return pane;
    }

    @Override
    /** {@inheritDoc} */
    public ObservableList<Node> getInputRecievers() {
        return recievers;
    }

    @Override
    /** {@inheritDoc} */
    public SerializableChartProxy serializable() {
        return new SerializableLine(this);
    }
}

/**
 * `Serializable` proxy for `Line`
 */
class SerializableLine implements SerializableChartProxy {
    /**
     * List of X, Y points
     */
    private List<Pair<Number, Number>> state;

    /**
     * Creates new instance from a Line.
     * 
     * @param line Line to get data from.
     */
    SerializableLine(Line line) {
        this.state = line.state.stream().map(x -> new Pair<>(x.getXValue(), x.getYValue()))
                .collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
    public Chart intoChart() {
        return new Line(state);
    }
}
