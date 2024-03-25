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

/**
 * A Piechart and it's associated state and input receivers.
 */
public class Pie extends Chart {
    private ObservableList<PieChart.Data> state = FXCollections.observableArrayList();
    private ObservableList<Node> recievers = FXCollections.observableArrayList();
    private PieChart chart = new PieChart(state);

    /**
     * Default Piechart.
     * 
     * @return piechart with one slice.
     */
    static Pie empty() {
        return new Pie(FXCollections.observableArrayList(new PieChart.Data("", 1)));
    }

    /**
     * Constructs new Pie based on data.
     * 
     * @param state List of slices.
     */
    Pie(ObservableList<PieChart.Data> state) {
        for (PieChart.Data data : state) {
            createInputReceiver(data);
        }
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
        return createInputReceiver(new PieChart.Data("", 1.0));
    }

    public Node createInputReceiver(PieChart.Data nodeState) {
        state.add(nodeState);

        var name = new TextField(nodeState.getName());
        name.textProperty()
                .addListener((observable, oldValue, newValue) -> nodeState.setName(newValue));

        var size = new TextField(((Double) nodeState.getPieValue()).toString());
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
