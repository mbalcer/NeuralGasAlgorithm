import algorithm.NeuralGas;
import utils.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class TestAlgorithm {
    private static List<String> result = new ArrayList<>();

    public static void main(String[] args) {
        int neurons = 20;
        int iterations = 100;
        String srcFile = "images/small_view.jpg";
        double mapRadiusStart = 20.0;
        double learningRateStart = 2;

        FileHandler.makeEmptyDir("data_img");
        String imageData = "data_img/img.data";

        FileHandler.parseIMG(srcFile, imageData, "\t", 4);


        for (int i = 0; i < 100; i++) {
            NeuralGas neuralGas = new NeuralGas(neurons, iterations, imageData, "\t", false, mapRadiusStart, learningRateStart, 1);
            neuralGas.calc(false);

            FileHandler.writePointsAsClusters(neuralGas.getWinnerIds(), neuralGas.getNeurons(), neuralGas.getImgcprFile(), neuralGas.getSeparator());

            FileHandler.writeMatrixToImage(FileHandler.readPixels(neuralGas.getImgcprFile(), neuralGas.getSeparator()),
                    srcFile, neuralGas.getDestImage(), neuralGas.getSeparator(), 4, "png");

            double v = FileHandler.compareImg(srcFile, neuralGas.getDestImage());
            result.add(String.valueOf(v));
        }

        FileHandler.copy("results_ng/out.png", "tests/lr_" + learningRateStart + "_mr_" + mapRadiusStart + ".png");
        saveLogsToFile(learningRateStart, mapRadiusStart);
    }

    public static void saveLogsToFile(double learningRate, double mapRadius) {
        String filePath = "tests/lr_" + learningRate + "_mr_" + mapRadius + ".txt";
        try {
            if (!Files.exists(Paths.get("tests/"))) {
                new File("tests/").mkdir();
            }
            new File(filePath).createNewFile();
            Files.write(Paths.get(filePath), result, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
