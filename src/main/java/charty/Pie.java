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
    /**
     * PieChart state.
     */
    private ObservableList<PieChart.Data> state = FXCollections.observableArrayList();

    /**
     * Active recievers.
     */
    private ObservableList<Node> recievers = FXCollections.observableArrayList();

    /**
     * Active PieChart instance
     */
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
        return createInputReceiver(new PieChart.Data("", 1.0));
    }

    /**
     * Creates new input reciever.
     *
     * @param nodeState Initial state of input receiver.
     * @return newly created input receiver.
     */
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
    /** {@inheritDoc} */
    public SerializableChartProxy serializable() {
        return new SerializablePie(state);
    }

    @Override
    /** {@inheritDoc} */
    public ObservableList<Node> getInputRecievers() {
        return recievers;
    }
}

/**
 * `Serializable` Proxy for the `Pie`
 */
class SerializablePie implements SerializableChartProxy {
    /**
     * List of Slice name, Size.
     */
    private List<Pair<String, Double>> state;

    /**
     * Creates new instance from a Pie.
     * 
     * @param state State of the pie to serialize
     */
    SerializablePie(ObservableList<PieChart.Data> state) {
        this.state = state.stream()
                .map(x -> new Pair<String, Double>(x.getName(), x.getPieValue()))
                .collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
    public Chart intoChart() {
        return new Pie(FXCollections.observableArrayList(
                state.stream()
                        .map(x -> new PieChart.Data(x.getKey(), x.getValue()))
                        .collect(Collectors.toList())));
    }
}
