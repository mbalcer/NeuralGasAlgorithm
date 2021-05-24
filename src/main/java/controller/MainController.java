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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    @FXML
    private Button startBtn;

    @FXML
    private ComboBox<AlgorithmType> algorithmComboBox;

    @FXML
    private ComboBox<Path> imageComboBox;

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
    private Path imageFile;
    private static final double PRECISION = 100.0;
    private static final String SEPARATOR = "\t";
    private MyLogger myLogger;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myLogger = MyLogger.getInstance().textArea(logsArea);
        initAlgorithmCombobox();
        initImageComboBox();
        initSliders();
        initButtons();
    }

    public void initButtons() {
        startBtn.setDisable(true);
        startBtn.setOnAction(action -> {
            startBtn.setDisable(true);
            FileHandler.makeEmptyDir("data_img");
            String imageData = "data_img/img.data";
            int frameSz = 4;
            double lrc = 1;

            FileHandler.parseIMG(imageFile.toString(), imageData, SEPARATOR, frameSz);

            new Thread(() -> {
                NeuralGas ng = new NeuralGas(neurons, iterations, imageData, SEPARATOR, false, mapRadius, learningRate, lrc);
                ng.calc();

                FileHandler.writeMatrixToImage(FileHandler.readPixels(ng.destDir + ng.imgcprFile, SEPARATOR),
                        imageFile.toString(), ng.destDir+"out.png", SEPARATOR, frameSz, "png");

                setImage(ng.destDir + "out.png");
                startBtn.setDisable(false);
            }).start();
        });
    }

    private void initAlgorithmCombobox() {
        AlgorithmType[] functionTypes = AlgorithmType.values();
        algorithmComboBox.setItems(FXCollections.observableArrayList(functionTypes));
        algorithmComboBox.setValue(AlgorithmType.NEURAL_GAS);
        algorithmComboBox.valueProperty().addListener(
                (observable, oldValue, actualValue) -> algorithmType = actualValue);
    }

    private void initImageComboBox() {
        String imagesPath = "images/";
        Path path = Paths.get(imagesPath);
        List<Path> imagesList = null;
        try {
            imagesList = Files.list(Paths.get(imagesPath)).collect(Collectors.toList());
            imageComboBox.setItems(FXCollections.observableArrayList(imagesList));
            imageComboBox.valueProperty().addListener((observable, oldValue, actualValue) -> {
                imageFile = actualValue;
                startBtn.setDisable(false);
            });
        } catch (IOException e) {
            imageComboBox.setItems(FXCollections.observableArrayList(new ArrayList()));
        }

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
