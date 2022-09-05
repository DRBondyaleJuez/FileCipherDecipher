import core.FileCipherDecipher;
import javafx.application.Application;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Main {
    public static void main(String[] args){

        Application.launch(FileCipherDecipher.class, args);

    }
}