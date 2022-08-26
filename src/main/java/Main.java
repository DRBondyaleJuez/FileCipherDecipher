import core.FileCipherDecipher;
import javafx.application.Application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        Application.launch(FileCipherDecipher.class, args);

    }
}