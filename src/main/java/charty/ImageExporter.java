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

public class ImageExporter {
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

    static void exportPng(File path, Chart chart) {
        export(path, chart, "png");
    }

    static void exportJpeg(File path, Chart chart) {
        export(path, chart, "jpeg");
    }

}