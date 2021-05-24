package controller;

import alogirthm.NeuralGas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import utils.FileHandler;

public class MainController {
    @FXML
    private Button startBtn;

    public void initialize() {
        setStartBtn();
    }

    public void setStartBtn() {
        startBtn.setOnAction(action -> {
            images();
        });
    }

    public static void images() {
        FileHandler.makeEmptyDir("data_img");
        String imageFile = "images/1.png";
        String imageData = "data_img/img.data";
        String sep = "\t";
        int frameSz = 4;
        int n = 30;
        int iter = 25;
        double mapRadius = 50;
        double lr = 0.1;
        double lrc = 1;

        FileHandler.parseIMG(imageFile, imageData, sep, frameSz);

        NeuralGas k = new NeuralGas(n, iter, imageData, sep, false, mapRadius, lr, lrc);
        k.calc();

        FileHandler.writeMatrixToImage(FileHandler.readPixels(k.destDir + k.imgcprFile, sep),
                imageFile, k.destDir+"out.png", sep, frameSz, "png");
    }
}
