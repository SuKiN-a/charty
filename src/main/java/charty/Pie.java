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
    int inputReceiverCount = 0;
    PieChart chart = new PieChart(state);

    static Pie empty() {
        return new Pie(FXCollections.observableArrayList(new PieChart.Data("", 1)));
    }

    Pie(ObservableList<PieChart.Data> state) {
        this.state = state;
        this.inputReceiverCount = state.size();
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
        inputReceiverCount = 0;
    }

    @Override
    public Node createInputReceiver() {
        return createInputReceiver("", 1.0);
    }

    public Node createInputReceiver(String initName, Double initVal) {
        state.add(new PieChart.Data(initName, initVal));
        var nodeState = state.get(inputReceiverCount);
        var node = createInputReceiver(initName, initVal, nodeState);
        inputReceiverCount += 1;
        return node;
    }

    public Node createInputReceiver(String initName, Double initVal, PieChart.Data nodeState) {
        var nameLabel = new Label("name: ");
        var name = new TextField(initName);
        name.textProperty()
                .addListener((observable, oldValue, newValue) -> nodeState.setName(newValue));

        var sizeLabel = new Label("size: ");
        var size = new TextField(initVal.toString());
        size.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                nodeState.setPieValue(Double.parseDouble(newValue));
            } catch (NumberFormatException e) {
                size.setText(oldValue);
            }
        });
        var box = new GridPane();

        box.addRow(0, nameLabel, name);
        box.addRow(1, sizeLabel, size);

        recievers.add(box);

        return box;
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
    int inputReceiverCount;
    List<Pair<String, Double>> state;

    SerializablePie(ObservableList<PieChart.Data> state) {
        this.inputReceiverCount = state.size();
        this.state = state.stream()
                .map(x -> new Pair<String, Double>(x.getName(), x.getPieValue()))
                .collect(Collectors.toList());
    }

    SerializablePie(List<Pair<String, Double>> state) {
        this.inputReceiverCount = state.size();
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
