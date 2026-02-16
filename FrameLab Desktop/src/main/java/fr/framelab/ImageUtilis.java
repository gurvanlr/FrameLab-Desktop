package fr.framelab;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImageUtilis {

    public static void writableImage (Image image) {
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        WritableImage wi = new WritableImage(w,h);
        wi.getPixelWriter().setPixels(0,0,w,h,image.getPixelReader(),0,0);
    }
}
