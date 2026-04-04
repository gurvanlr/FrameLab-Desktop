package fr.framelab.controller;

import fr.framelab.AppSession;
import fr.framelab.model.Project;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class EditorController {

    private String activeCanvasName;
    private Canvas canvas;
    private int index = 1;
    private Project project;
    @FXML
    private StackPane displayImage;
    @FXML
    private ListView<String> listCanvas;
    @FXML
    private Slider brightnessbar;
    private Image originalImage;

    public void initData(Project project) {
        this.project = project;

        display();
        loadCanvaList();
    }

    @FXML
    public void initialize() {
        listCanvas.setOnMouseClicked(event -> {
            String selected = listCanvas.getSelectionModel().getSelectedItem();
            if (selected != null) {
                activeCanvasName = selected;

                File file = new File("Projects/" + project.getName() + "/" + selected);
                Image img = new Image(file.toURI().toString());
                originalImage = img;

                canvas = new Canvas(500, 500);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                double canvasWidth = canvas.getWidth();
                double canvasHeight = canvas.getHeight();

                double imageWidth = img.getWidth();
                double imageHeight = img.getHeight();

                double ratio = Math.min(canvasWidth / imageWidth, canvasHeight / imageHeight);

                double newWidth = imageWidth * ratio;
                double newHeight = imageHeight * ratio;

                double x = (canvasWidth - newWidth) / 2;
                double y = (canvasHeight - newHeight) / 2;
                gc.drawImage(img, x, y, newWidth, newHeight);


                displayImage.getChildren().add(canvas);
            }
        });
    }

    @FXML
    private void display() {
        String path;
        if (AppSession.isDemoMode()) {
            path = "Challenges/demo_challenge.jpg";
        } else {
            path = "Challenges/chall#" + project.getChallengeId() + ".png";
        }

        File file = new File(path);
        if (!file.exists()) { return; }

        Image img = new Image(file.toURI().toString());
        originalImage = img;

        canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(img, 0, 0);

        displayImage.getChildren().add(canvas);
    }

    @FXML
    private void createDrawImage() {
        canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);

        originalImage = null;


        displayImage.getChildren().add(canvas);
        saveCanvas();
        loadCanvaList();
    }

    @FXML
    private void createWritableImage() {
        String path = ("Projects/" + project.getName() + "/base.png");
        File file = new File(path);
        String img_path = file.toURI().toString();
        Image img = new Image(img_path);

        originalImage = img;

        canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        double imageWidth = img.getWidth();
        double imageHeight = img.getHeight();

        double ratio = Math.min(canvasWidth / imageWidth, canvasHeight / imageHeight);

        double newWidth = imageWidth * ratio;
        double newHeight = imageHeight * ratio;

        double x = (canvasWidth - newWidth) / 2;
        double y = (canvasHeight - newHeight) / 2;
        gc.drawImage(img, x, y, newWidth, newHeight);

        saveCanvas();
        loadCanvaList();

        displayImage.getChildren().add(canvas);
    }

    public void saveCanvas() {
        if (canvas == null) {return; }

        WritableImage image = canvas.snapshot(null, null);

        File file = new File("Projects/" + project.getName() + "/canvas" + index + ".png");
        index++;

        try {
            ImageIO.write(
                    SwingFXUtils.fromFXImage(image, null),
                    "png",
                    file
            );
            loadCanvaList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCanvaList() {

        File folder = new File("Projects/" + project.getName());
        listCanvas.getItems().clear();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                listCanvas.getItems().add(file.getName());
            }
        }

    }

    @FXML
    private void eraseCanva () {
        if (activeCanvasName == null) {
            System.out.println("pas d'élément séléctionner");
        } else {
            String path = ("Projects/" + project.getName() + "/" + activeCanvasName);
            File file = new File(path);
            file.delete();
            loadCanvaList();
        }
    }

    private void colorInversion() {
        if (canvas == null){return;};

        int h = (int) canvas.getHeight();
        int w = (int) canvas.getWidth();
        WritableImage source = canvas.snapshot(null,null);

        WritableImage dest = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color sourceColor = reader.getColor(x, y);
                Color inverse = Color.color(1.0 - sourceColor.getRed(), 1.0 - sourceColor.getGreen(), 1.0 - sourceColor.getBlue());

                writer.setColor(x, y, inverse);
            }
        }
        canvas.getGraphicsContext2D().getPixelWriter().setPixels(0,0,w,h,dest.getPixelReader(),0,0);
        originalImage = canvas.snapshot(null,null);
    }

    private void blackAndWhite () {
        if (canvas == null){return;}

        int h = (int) canvas.getHeight();
        int w = (int) canvas.getWidth();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE);
        WritableImage source = canvas.snapshot(params,null);

        WritableImage dest = new WritableImage(w, h);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color sourceColor = reader.getColor(x, y);
                double lum = 0.299 * sourceColor.getRed() + 0.587 * sourceColor.getGreen() + 0.114 * sourceColor.getBlue();
                Color gray = Color.color(lum,lum,lum);
                    writer.setColor(x, y, gray );
            }
        }
        canvas.getGraphicsContext2D().getPixelWriter().setPixels(0,0,w,h,dest.getPixelReader(),0,0);
        originalImage = canvas.snapshot(null,null);

    }



    @FXML
    private void handleInverseFilter () {
        this.colorInversion();
    }

    @FXML
    private void handleBlackAndWhiteFilter () {
        this.blackAndWhite();
    }

    @FXML
    private void brightness () {

        double bright = brightnessbar.getValue();

        if (canvas == null) {
            System.out.println("Aucun canvas sélectionné");
            return;
        }

        if (originalImage == null) {
            System.out.println("Aucune image source disponible");
            return;
        }

        int heightImage = (int) originalImage.getHeight();
        int withImage = (int) originalImage.getWidth();

        PixelReader reader = originalImage.getPixelReader();
        WritableImage dest = new WritableImage(withImage, heightImage);
        PixelWriter writer = dest.getPixelWriter();

        for (int y = 0 ; y < heightImage; y++) {
            for (int x = 0; x < withImage; x++) {
                Color sourceColor = reader.getColor(x, y);
                double r = Math.min(1.0, Math.max(0.0, bright * sourceColor.getRed()));
                double g = Math.min(1.0, Math.max(0.0, bright * sourceColor.getGreen()));
                double b = Math.min(1.0, Math.max(0.0, bright * sourceColor.getBlue()));
                Color newBrightness = Color.color(r,g,b);
                    writer.setColor(x,y,newBrightness);
            }
        }
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double ratio = Math.min(canvasWidth / withImage, canvasHeight / heightImage);
        double newWidth = withImage * ratio;
        double newHeight = heightImage * ratio;
        double x = (canvasWidth - newWidth) / 2;
        double y = (canvasHeight - newHeight) / 2;


        canvas.getGraphicsContext2D().clearRect(0, 0, canvasWidth, canvasHeight);
        canvas.getGraphicsContext2D().drawImage(dest, x, y, newWidth, newHeight);
    }
}




