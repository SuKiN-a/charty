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

public class Bar extends Chart {
    private List<XYChart.Data<String, Number>> state = new ArrayList<>();
    private ObservableList<XYChart.Data<String, Number>> backing_list = FXCollections.observableArrayList();
    private ObservableList<Node> recievers = FXCollections.observableArrayList();
    private BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());

    static Bar empty() {
        return new Bar(Arrays.asList(new Pair<>("label", 1.0)));
    }

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
    public Node getNode() {
        return chart;
    }

    @Override
    public void onClear() {
        recievers.clear();
        state.clear();
        reload();
    }

    @Override
    public Node createInputReceiver() {
        var nodeState = new XYChart.Data<String, Number>("label" + state.size(), 1.0);
        return createInputReceiver(nodeState);
    }

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
    public ObservableList<Node> getInputRecievers() {
        return recievers;
    }

    @Override
    public SerializableChartProxy serializable() {
        reload();
        return new SerializableBar(backing_list);
    }

    /*
     * Attaches a fresh `Series` to the BarChart because
     * javafx's BarChart implementation is *super* broken
     * 
     * @formatter:off
     * https://bugs.openjdk.org/browse/JDK-8089250?filter=39543&jql=project%20%3D%20JDK%20AND%20issuetype%20in%20(Bug%2C%20Enhancement)%20AND%20resolution%20%3D%20Unresolved%20AND%20component%20%3D%20javafx%20AND%20text%20~%20%22BarChart%22%20ORDER%20BY%20updated%20DESC 
     */
    public void reload() {
        ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();
        chart.setData(series);
        backing_list = FXCollections.observableArrayList(state);
        backing_list.add(new XYChart.Data<String,Number>("", 0));
        series.add(new XYChart.Series<>(backing_list));
    }
}

class SerializableBar implements SerializableChartProxy {
    List<Pair<String, Number>> state;

    SerializableBar(ObservableList<XYChart.Data<String, Number>> backing_list) {
        state = backing_list.stream().map(x -> {
            return new Pair<>(x.getXValue(), x.getYValue());
        }).collect(Collectors.toList());
    }

    @Override
    public Chart intoChart() {
        return new Bar(state);
    }
}