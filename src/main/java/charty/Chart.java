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

/**
 * Interface that all charty-supported charts implement.
 * In addition to these methods, Charts also have a static
 * `empty` method to create a default version of the chart.
 */
public abstract class Chart {
    /**
     * Gets the actual visual `Chart`.
     * 
     * @return the renderable chart.
     */
    abstract Node getNode();

    /**
     * Resets chart to empty state.
     */
    abstract void onClear();

    /**
     * Creates an input receiver(UI element that controls chart state).
     * 
     * @return new input reciever.
     */
    abstract Node createInputReceiver();

    /**
     * Gets all the input recievers currently associated with the chart.
     * 
     * @return all input recievers.
     */
    abstract ObservableList<Node> getInputRecievers();

    /**
     * Returns a serializable Object that encodes all chart data.
     * 
     * @return Serializable version of the chart.
     */
    abstract SerializableChartProxy serializable();

    /**
     * Writes chart into file.
     * 
     * @param chart    Chart to write into file.
     * @param saveFile File to be written into.
     * @throws IOException Error during IO.
     */
    static void save(Chart chart, File saveFile) throws IOException {
        try (var os = new FileOutputStream(saveFile); var objectOutput = new ObjectOutputStream(os)) {
            objectOutput.writeObject(chart.serializable());
        }
    }

    /**
     * Reads and deserializes chart.
     * 
     * @param file File to deserialize from.
     * @return Deserialized chart.
     * @throws FileNotFoundException  file is not found.
     * @throws IOException            Error during IO.
     * @throws ClassNotFoundException the project instance is of an older
     *                                version or invalid.
     */
    static Chart load(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (var in = new FileInputStream(file); var objectInput = new ObjectInputStream(in)) {
            return ((SerializableChartProxy) objectInput.readObject()).intoChart();
        }
    }
}
