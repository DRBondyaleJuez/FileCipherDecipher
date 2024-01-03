package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * This the entry point of the application launch called by the main
 * <p>
 *     Inheriting from application requiring an implementation of abstract class start that allows the display of the view
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileCipherDecipher extends Application {

    private static Logger logger = LogManager.getLogger(FileCipherDecipher.class);
    private Stage mainStage;

    /**
     * This is the constructor. Here the mainStage attribute is instantiate.
     */
    public FileCipherDecipher() { mainStage = new Stage();}

    /**
     * This is the implementation of the start abstract method of the extended class Application.
     *  * <p>
     *     This method is called during the execution of the Application class static method launch. It loads the FXMl files,
     *     therefore, building its controllers too and the built mainStage is displayed with the method show of the Stage class.
     *  * </p>
     * @param stage Stage object provided during the static launch method execution.
     */
    @Override public void start(Stage stage) {
        mainStage = stage;
        loadingMainScene();

        mainStage.setTitle("File-Cipher-Decipher");
        mainStage.centerOnScreen();
        mainStage.show();
    }

    private void loadingMainScene() {
        // TO access to the Resource folder, you have to do the following:
        // getClass().getResource("/path/of/the/resource")
        FXMLLoader paneLoader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        Parent root = loadPaneLoader(paneLoader);
        if(root == null) {
            // This is the extreme case if loaded fxml file is null
            gracefulShutdown();
        } else {
            Scene newScene = new Scene(root);
            mainStage.setScene(newScene);
        }
    }
    private Parent loadPaneLoader(FXMLLoader paneLoader) {
        try {
            return paneLoader.load();
        } catch (IOException e) {
            // ---- LOG ----
            StringBuilder errorStackTrace = new StringBuilder();
            for (StackTraceElement ste:e.getStackTrace()) {
                errorStackTrace.append("        ").append(ste).append("\n");
            }
            logger.error("The FXML file (" + paneLoader.toString() + ") could not be loaded. ERROR:\n " + e + "\n" + "STACK TRACE:\n" + errorStackTrace );
            return null;
        }
    }

    private void gracefulShutdown(){
        logger.info("There has been a fatal error. I am shutting down.");
        System.exit(-1);
    }
}
