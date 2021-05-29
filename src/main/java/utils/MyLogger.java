package utils;

import algorithm.AlgorithmType;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyLogger {
    private static MyLogger INSTANCE;
    private TextArea textArea;
    private List<String> lines = new ArrayList<>();
    private String filePath;

    private static final String DIR_PATH = "logs/";

    private MyLogger() {
        if (!Files.exists(Paths.get(DIR_PATH))) {
            new File(DIR_PATH).mkdir();
        }
    }

    public static MyLogger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MyLogger();
        }
        return INSTANCE;
    }

    public MyLogger textArea(TextArea textArea) {
        this.textArea = textArea;
        return INSTANCE;
    }

    public void info(String message) {
        System.out.println(message);
        lines.add(message);
        if (textArea != null) {
            Platform.runLater(() -> textArea.appendText(message + "\n"));
        }
    }

    public void saveLogsToFile(AlgorithmType algorithm) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        filePath = DIR_PATH + algorithm.getAbbreviation() + "_" + LocalDateTime.now().format(formatter) + ".txt";
        try {
            new File(filePath).createNewFile();
            Files.write(Paths.get(filePath), lines,
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openLogsFile() {
        try {
            File file = new File(filePath);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (file.exists())
                    desktop.open(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
