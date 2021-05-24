package controller;

import algorithm.AlgorithmType;
import algorithm.NeuralGas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.FileHandler;
import utils.MyLogger;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button startBtn;

    @FXML
    private ComboBox algorithmComboBox;

    @FXML
    private Slider neuronsSlider;

    @FXML
    private Slider iterationSlider;

    @FXML
    private Slider learningRateSlider;

    @FXML
    private Slider mapRadiusSlider;

    @FXML
    private Label neuronsLabel;

    @FXML
    private Label iterationLabel;

    @FXML
    private Label learningRateLabel;

    @FXML
    private Label mapRadiusLabel;

    @FXML
    private TextArea logsArea;

    @FXML
    private ImageView imageView;

    private AlgorithmType algorithmType = AlgorithmType.NEURAL_GAS;
    private int neurons = 5;
    private int iterations = 100;
    private double learningRate = 0.1;
    private double mapRadius = 50.0;
    private static final double PRECISION = 100.0;
    private static final String SEPARATOR = "\t";
    private MyLogger myLogger;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myLogger = MyLogger.getInstance().textArea(logsArea);
        initAlgorithmCombobox();
        initSliders();
        initButtons();
    }

    public void initButtons() {
        startBtn.setOnAction(action -> {
            FileHandler.makeEmptyDir("data_img");
            String imageFile = "images/1.png";
            String imageData = "data_img/img.data";
            int frameSz = 4;
            double lrc = 1;

            FileHandler.parseIMG(imageFile, imageData, SEPARATOR, frameSz);

            new Thread(() -> {
                NeuralGas ng = new NeuralGas(neurons, iterations, imageData, SEPARATOR, false, mapRadius, learningRate, lrc);
                ng.calc();

                FileHandler.writeMatrixToImage(FileHandler.readPixels(ng.destDir + ng.imgcprFile, SEPARATOR),
                        imageFile, ng.destDir+"out.png", SEPARATOR, frameSz, "png");

                setImage(ng.destDir + "out.png");
            }).start();
        });
    }

    private void initAlgorithmCombobox() {
        AlgorithmType[] functionTypes = AlgorithmType.values();
        algorithmComboBox.setItems(FXCollections.observableArrayList(functionTypes));
        algorithmComboBox.setValue(AlgorithmType.NEURAL_GAS.getName());
        algorithmComboBox.valueProperty().addListener(
                (observable, oldValue, actualValue) -> algorithmType = (AlgorithmType) actualValue);
    }

    private void initSliders() {
        neuronsSlider.valueProperty().addListener((observable, oldValue, actualValue) -> {
            int value = actualValue.intValue();
            neurons = value;
            neuronsLabel.setText(String.valueOf(value));
        });
        iterationSlider.valueProperty().addListener((observable, oldValue, actualValue) -> {
            int value = actualValue.intValue();
            iterations = value;
            iterationLabel.setText(String.valueOf(value));
        });
        mapRadiusSlider.valueProperty().addListener((observable, oldValue, actualValue) -> {
            double value = Math.round(actualValue.doubleValue() * PRECISION) / PRECISION;
            mapRadius = value;
            mapRadiusLabel.setText(String.valueOf(value));
        });
        learningRateSlider.valueProperty().addListener((observable, oldValue, actualValue) -> {
            double value = Math.round(actualValue.doubleValue() * PRECISION) / PRECISION;
            learningRate = value;
            learningRateLabel.setText(String.valueOf(value));
        });
    }

    private void setImage(String path) {
        File file = new File(path);
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
    }
}
