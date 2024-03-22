package charty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface Chart {
    Node node();

    void onClear();

    Node createInputReceiver();

    ObservableList<Node> getInputRecievers();

    SerializableChartProxy serializable();

    static void save(Chart chart, File saveFile) throws IOException {
        try (var os = new FileOutputStream(saveFile); var objectOutput = new ObjectOutputStream(os)) {
            objectOutput.writeObject(chart.serializable());
        }
    }

    static Chart load(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (var in = new FileInputStream(file); var objectInput = new ObjectInputStream(in)) {
            return ((SerializableChartProxy) objectInput.readObject()).intoChart();
        }
    }
}