package utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class MyLogger {
    private static MyLogger INSTANCE;
    private TextArea textArea;

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
        if (textArea != null) {
            Platform.runLater(() -> textArea.appendText(message + "\n"));
        }
    }
}
