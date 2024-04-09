package charty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

/**
 * The BarChart, associated data and input recievers.
 * This implementation uses an intermediate List that is
 * transformed into an ObservableList and attached to the BarChart on every
 * change.
 * 
 * @see <a href=
 *      "https://bugs.openjdk.org/browse/JDK-8089250?filter=39543&jql=project%20%3D%20JDK%20AND%20issuetype%20in%20(Bug%2C%20Enhancement)%20AND%20resolution%20%3D%20Unresolved%20AND%20component%20%3D%20javafx%20AND%20text%20~%20%22BarChart%22%20ORDER%20BY%20updated%20DESC">
 *      The bugs </a>
 */
public class Bar extends Chart {
    /**
     * Actual barchart state. this is to have a known working version of the
     * barchart's state in cases where the underlying ObservableList gets corrupted
     * by the Chart itself.
     */
    private List<XYChart.Data<String, Number>> state = new ArrayList<>();

    /**
     * The barchart state exposed to the BarChart node. We replace this on every
     * change to get around the spooky behaviour that BarChart seems to have.
     */
    private ObservableList<XYChart.Data<String, Number>> backing_list = FXCollections.observableArrayList();

    /**
     * Active recievers.
     */
    private ObservableList<Node> recievers = FXCollections.observableArrayList();

    /**
     * The BarChart node.
     */
    private BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());

    /**
     * Creates defaultish BarChart.
     * 
     * @return barchart with one bar
     */
    static Bar empty() {
        return new Bar(Arrays.asList(new Pair<>("label", 1.0)));
    }

    /**
     * Creates a Bar from the given data. Initializes state and creates recievers.
     * 
     * @param data Initial state for the segments
     */
    Bar(List<Pair<String, Number>> data) {
        chart.setLegendVisible(false);
        chart.setBarGap(5.0);
        chart.setCategoryGap(5.0);
        reload();

        for (Pair<String, Number> pair : data) {
            createInputReceiver(new XYChart.Data<String, Number>(pair.getKey(), pair.getValue()));
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
        recievers.clear();
        state.clear();
        reload();
    }

    @Override
    /** {@inheritDoc} */
    public Node createInputReceiver() {
        var nodeState = new XYChart.Data<String, Number>("label" + state.size(), 1.0);
        return createInputReceiver(nodeState);
    }

    /**
     * creates new input reciever.
     *
     * @param nodeState initial state for the segment.
     * @return newly created reciever.
     */
    public Node createInputReceiver(XYChart.Data<String, Number> nodeState) {
        state.add(nodeState);
        reload();

        var nameLabel = new Label("name");
        var name = new TextField(nodeState.getXValue());
        name.textProperty().addListener((actionEvent, oldVal, newVal) -> {
            nodeState.setXValue(newVal);
            reload();
        });

        var sizeLabel = new Label("size");
        var size = new TextField(nodeState.getYValue().toString());
        size.textProperty().addListener((actionEvent, oldVal, newVal) -> {
            if (newVal.equals("")) {
                reload();
                return;
            }
            try {
                nodeState.setYValue(Double.parseDouble(size.getText()));
            } catch (NumberFormatException e) {
                size.setText(oldVal);
            } finally {
                reload();
            }
        });

        GridPane.setHgrow(nameLabel, Priority.ALWAYS);
        GridPane.setHalignment(nameLabel, HPos.CENTER);
        GridPane.setHgrow(sizeLabel, Priority.ALWAYS);
        GridPane.setHalignment(sizeLabel, HPos.CENTER);

        var pane = new GridPane();
        recievers.add(pane);

        pane.addRow(0, nameLabel, name);
        pane.addRow(1, sizeLabel, size);

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
        reload();
        return new SerializableBar(backing_list);
    }

    /**
     * Attaches a fresh `Series` to the BarChart because
     * javafx's BarChart implementation seems to be broken.
     * 
     * @see <a href=
     *      "https://bugs.openjdk.org/browse/JDK-8089250?filter=39543&jql=project%20%3D%20JDK%20AND%20issuetype%20in%20(Bug%2C%20Enhancement)%20AND%20resolution%20%3D%20Unresolved%20AND%20component%20%3D%20javafx%20AND%20text%20~%20%22BarChart%22%20ORDER%20BY%20updated%20DESC">
     *      The bugs </a>
     */
    public void reload() {
        ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();
        chart.setData(series);
        backing_list = FXCollections.observableArrayList(state);
        backing_list.add(new XYChart.Data<String, Number>("", 0));
        series.add(new XYChart.Series<>(backing_list));
    }
}

/**
 * Serialiable proxy for the Bar chart.
 */
class SerializableBar implements SerializableChartProxy {
    /**
     * List of Bar Name, Lengths.
     */
    List<Pair<String, Number>> state;

    /**
     * Creates a new instance from a list of bar names, sizes.
     * 
     * @param backing_list list of data to initialize with
     */
    SerializableBar(ObservableList<XYChart.Data<String, Number>> backing_list) {
        state = backing_list.stream().map(x -> {
            return new Pair<>(x.getXValue(), x.getYValue());
        }).collect(Collectors.toList());
    }

    @Override
    /** {@inheritDoc} */
    public Chart intoChart() {
        return new Bar(state);
    }
}
