package charty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Line implements Chart {
    ObservableList<XYChart.Data<Number, Number>> state = FXCollections.observableArrayList();
    ObservableList<Node> recievers = FXCollections.observableArrayList();
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);

    Line(List<Pair<Number, Number>> data) {
        chart.setLegendVisible(false);

        xAxis.setLabel("X");
        yAxis.setLabel("Y");
        for (Pair<Number, Number> pair : data) {
            createInputReceiver(new XYChart.Data<Number, Number>(pair.getKey(), pair.getValue()));
        }
    }

    static Line empty() {
        return new Line(Arrays.asList(new Pair<>(1.0, 1.0)));
    }

    @Override
    public Node node() {
        ObservableList<XYChart.Series<Number, Number>> l = FXCollections.observableArrayList();
        l.add(new XYChart.Series<>(state));
        chart.setData(l);

        if (chart == null)
            chart = new LineChart<>(xAxis, yAxis);
        return chart;
    }

    @Override
    public void onClear() {
        state.clear();
        recievers.clear();
        state.clear();
    }

    @Override
    public Node createInputReceiver() {
        return createInputReceiver(new XYChart.Data<Number, Number>(1.0, 1.0));
    }

    public Node createInputReceiver(XYChart.Data<Number, Number> nodeState) {
        state.add(nodeState);

        Label xLabel = new Label("x");
        var x = new TextField("");
        x.textProperty().addListener((actionEvent, oldVal, newVal) -> {
            if (newVal.equals(""))
                return;
            try {
                nodeState.setXValue(Double.parseDouble(x.getText()));
            } catch (NumberFormatException e) {
                x.setText(oldVal);
            }
        });

        Label yLabel = new Label("y");
        var y = new TextField("");
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
        GridPane.setFillWidth(xLabel, true);
        xLabel.setMaxWidth(Double.MAX_VALUE);

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