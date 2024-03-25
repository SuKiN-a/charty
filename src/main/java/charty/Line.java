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

public class Line extends Chart {
    ObservableList<XYChart.Data<Number, Number>> state = FXCollections.observableArrayList();
    ObservableList<Node> recievers = FXCollections.observableArrayList();
    LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());

    Line(List<Pair<Number, Number>> data) {
        ObservableList<XYChart.Series<Number, Number>> l = FXCollections.observableArrayList();
        l.add(new XYChart.Series<>(state));

        chart.setLegendVisible(false);
        chart.setData(l);

        for (Pair<Number, Number> pair : data) {
            createInputReceiver(new XYChart.Data<Number, Number>(pair.getKey(), pair.getValue()));
        }
    }

    static Line empty() {
        return new Line(Arrays.asList(new Pair<>(1.0, 1.0)));
    }

    @Override
    public Node getNode() {
        return chart;
    }

    @Override
    public void onClear() {
        state.clear();
        recievers.clear();
    }

    @Override
    public Node createInputReceiver() {
        return createInputReceiver(new XYChart.Data<Number, Number>(1.0, 1.0));
    }

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
    public ObservableList<Node> getInputRecievers() {
        return recievers;
    }

    @Override
    public SerializableChartProxy serializable() {
        return new SerializableLine(this);
    }
}

class SerializableLine implements SerializableChartProxy {
    List<Pair<Number, Number>> state;

    SerializableLine(Line line) {
        this.state = line.state.stream().map(x -> new Pair<>(x.getXValue(), x.getYValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Chart intoChart() {
        return new Line(state);
    }
}