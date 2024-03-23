package charty;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Pie implements Chart {
    ObservableList<PieChart.Data> state = FXCollections.observableArrayList();
    ObservableList<Node> recievers = FXCollections.observableArrayList();
    PieChart chart = new PieChart(state);

    static Pie empty() {
        return new Pie(FXCollections.observableArrayList(new PieChart.Data("", 1)));
    }

    Pie(ObservableList<PieChart.Data> state) {
        this.state = state;
        this.chart = new PieChart(state);
        for (PieChart.Data data : state) {
            createInputReceiver(data.getName(), data.getPieValue(), data);
        }
    }

    @Override
    public Node node() {
        if (chart == null)
            chart = new PieChart(state);
        return chart;
    }

    @Override
    public void onClear() {
        state.clear();
        recievers.clear();
    }

    @Override
    public Node createInputReceiver() {
        return createInputReceiver("", 1.0);
    }

    public Node createInputReceiver(String initName, Double initVal) {
        var nodeState = new PieChart.Data(initName, initVal);
        state.add(nodeState);

        var node = createInputReceiver(initName, initVal, nodeState);
        return node;
    }

    public Node createInputReceiver(String initName, Double initVal, PieChart.Data nodeState) {
        var name = new TextField(initName);
        name.textProperty()
                .addListener((observable, oldValue, newValue) -> nodeState.setName(newValue));

        var size = new TextField(initVal.toString());
        size.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(""))
                return;
            try {
                nodeState.setPieValue(Double.parseDouble(newValue));
            } catch (NumberFormatException e) {
                size.setText(oldValue);
            }
        });

        var pane = new GridPane();
        pane.addRow(0, new Label("name: "), name);
        pane.addRow(1, new Label("size: "), size);
        recievers.add(pane);
        return pane;
    }

    @Override
    public SerializableChartProxy serializable() {
        return new SerializablePie(state);
    }

    @Override
    public ObservableList<Node> getInputRecievers() {
        return recievers;
    }
}

class SerializablePie implements SerializableChartProxy {
    List<Pair<String, Double>> state;

    SerializablePie(ObservableList<PieChart.Data> state) {
        this.state = state.stream()
                .map(x -> new Pair<String, Double>(x.getName(), x.getPieValue()))
                .collect(Collectors.toList());
    }

    SerializablePie(List<Pair<String, Double>> state) {
        this.state = state;
    }

    @Override
    public Chart intoChart() {
        return new Pie(FXCollections.observableArrayList(
                state.stream()
                        .map(x -> new PieChart.Data(x.getKey(), x.getValue()))
                        .collect(Collectors.toList())));
    }
}
