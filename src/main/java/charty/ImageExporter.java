package charty;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;

/**
 * Utility class which exposes methods to convert Charts to png and jpeg files.
 */
public class ImageExporter {
    /**
     * Exports Chart to a given image format in given path.
     * 
     * @param file   File to export to.
     * @param chart  Chart to export.
     * @param format Format of exported image file.
     */
    private static void export(File file, Chart chart, String format) {
        WritableImage image = chart.getNode().snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try (var out = new FileImageOutputStream(file)) {
            file.createNewFile();
            ImageIO.write(bufferedImage, format, out);
        } catch (IOException e) {
            new Alert(AlertType.ERROR, "unexpected error when writing image.", ButtonType.CLOSE).showAndWait();
        }
    }

    /**
     * Exports Chart to a JPEG file in the given path.
     *
     * @param path  Path of newly created JPEG file.
     * @param chart Chart to convert to JPEG.
     */
    public static void exportPng(File path, Chart chart) {
        export(path, chart, "png");
    }

    /**
     * Exports Chart to a JPEG file in the given path.
     *
     * @param path  Path of newly created JPEG file.
     * @param chart Chart to convert to JPEG.
     */

    public static void exportJpeg(File path, Chart chart) {
        export(path, chart, "jpeg");
    }

}
