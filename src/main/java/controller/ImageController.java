package controller;

import algorithm.Neural;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import utils.FileHandler;

import java.io.File;

public class ImageController {
    private static ImageController INSTANCE;
    private ImageView imageView;
    private String format;
    @Getter
    private int frameSz;
    private Neural neural;
    private String srcFile;

    public static ImageController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageController();
        }
        return INSTANCE;
    }

    public ImageController imageView(ImageView imageView) {
        this.imageView = imageView;
        return INSTANCE;
    }

    public ImageController format(String format) {
        this.format = format;
        return INSTANCE;
    }

    public ImageController frameSz(int frameSz) {
        this.frameSz = frameSz;
        return INSTANCE;
    }

    public ImageController neural(Neural neural) {
        this.neural = neural;
        return INSTANCE;
    }

    public ImageController srcFile(String srcFile) {
        this.srcFile = srcFile;
        return INSTANCE;
    }

    public void setImage() {
        writeMatrixToImage();

        if (imageView != null) {
            File file = new File(neural.getDestImage());
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    public void writeMatrixToImage() {
        FileHandler.writePointsAsClusters(neural.getWinnerIds(), neural.getNeurons(), neural.getImgcprFile(), neural.getSeparator());

        FileHandler.writeMatrixToImage(FileHandler.readPixels(neural.getImgcprFile(), neural.getSeparator()),
                srcFile, neural.getDestImage(), neural.getSeparator(), frameSz, format);
    }

    public void clearImage() {
        imageView.setImage(null);
    }
}
