package controller;

import algorithm.AlgorithmType;
import algorithm.Kohonen;
import algorithm.Neural;
import algorithm.NeuralGas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
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
    private Button openLogsBtn;

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

    @FXML
    private CheckBox liveUpdateCheckBox;

    private AlgorithmType algorithmType = AlgorithmType.NEURAL_GAS;
    private int neurons = 5;
    private int iterations = 100;
    private double learningRate = 0.1;
    private double mapRadius = 50.0;
    private Path imageFile;
    private static final double PRECISION = 100.0;
    private static final String SEPARATOR = "\t";
    private MyLogger myLogger;
    private ImageController imageController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myLogger = MyLogger.getInstance().textArea(logsArea);
        imageController = ImageController.getInstance()
                .imageView(imageView)
                .frameSz(4)
                .format("png");
        initAlgorithmCombobox();
        initImageComboBox();
        initSliders();
        initStartBtn();
        initOpenLogsBtn();
    }

    public void initStartBtn() {
        startBtn.setDisable(true);
        startBtn.setOnAction(action -> {
            myLogger.info("--------------------------------------\nSTART PROGRAM\n--------------------------------------");
            myLogger.info("Algorithm: " + algorithmType.getName());
            myLogger.info("Image: " + imageFile.toString());
            myLogger.info("Neurons: " + neurons);
            myLogger.info("Iterations: " + iterations);
            myLogger.info("Start map radius: " + mapRadius);
            myLogger.info("Start learning rate: " + learningRate);
            startBtn.setDisable(true);
            openLogsBtn.setDisable(true);
            boolean liveUpdate = liveUpdateCheckBox.isSelected();
            imageController.srcFile(imageFile.toString());
            FileHandler.makeEmptyDir("data_img");
            String imageData = "data_img/img.data";
            double lrc = 1;

            FileHandler.parseIMG(imageFile.toString(), imageData, SEPARATOR, imageController.getFrameSz());

            new Thread(() -> {
                Neural neural = null;
                if (algorithmType.equals(AlgorithmType.NEURAL_GAS)) {
                    neural = new NeuralGas(neurons, iterations, imageData, SEPARATOR, false, mapRadius, learningRate, lrc);
                } else if (algorithmType.equals(AlgorithmType.KOHONEN)){
                    neural = new Kohonen(neurons, iterations, imageData, SEPARATOR, false, mapRadius, learningRate, lrc);
                }
                neural.calc(liveUpdate);

                if (!liveUpdate) {
                    imageController.neural(neural).setImage();
                }

                myLogger.info("--------------------------------------\n" + "RESULT");
                FileHandler.compareImg(imageFile.toString(), neural.getDestImage());
                startBtn.setDisable(false);
                openLogsBtn.setDisable(false);
                myLogger.saveLogsToFile(algorithmType);
            }).start();
        });
    }

    private void initOpenLogsBtn() {
        openLogsBtn.setOnAction(action -> {
            myLogger.openLogsFile();
        });
    }

    private void initAlgorithmCombobox() {
        AlgorithmType[] functionTypes = AlgorithmType.values();
        algorithmComboBox.setItems(FXCollections.observableArrayList(functionTypes));
        algorithmComboBox.setValue(AlgorithmType.NEURAL_GAS);
        algorithmComboBox.setConverter(new StringConverter<AlgorithmType>() {
           @Override
           public String toString(AlgorithmType object) {
               return object.getName();
           }

           @Override
           public AlgorithmType fromString(String string) {
               return algorithmComboBox.getItems().stream().filter(ap ->
                       ap.getName().equals(string)).findFirst().orElse(null);
           }
        });

        algorithmComboBox.valueProperty().addListener(
                (observable, oldValue, actualValue) -> algorithmType = actualValue);
    }

    private void initImageComboBox() {
        String imagesPath = "images/";
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
