package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        if(root != null) {
            Scene newScene = new Scene(root);
            mainStage.setScene(newScene);
        } else {
            System.out.println("Unable to load pan loader root is NULL");
        }
    }

    private Parent loadPaneLoader(FXMLLoader paneLoader) {
        try {
            return paneLoader.load();
        } catch (IOException e) {
            //Todo: log!!
            //Todo do something if the try fails
            System.out.println("FAIL!!! EXPLOTION!!!! BOOOOOOM");
            return null;
        }
    }
}
