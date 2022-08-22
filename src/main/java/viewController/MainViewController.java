package viewController;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    Controller controller;

    @FXML
    ImageView mainImageView;

    @FXML
    AnchorPane mainAnchorPane;


    public MainViewController() {
        controller = new Controller();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image mainImage = new Image(getClass().getResource("/images/cipherLock.png").toURI().toString());
            mainImageView.setImage(mainImage);
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("mainImage could not be found");
        }
    }

    @FXML
    public void openAction(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile != null) {
            System.out.println(selectedFile.getAbsolutePath());
            controller.cipherFile(selectedFile);
        } else {
            System.out.println("No file has been selected.");
        }
    }

    @FXML
    public void decipherAction(){
        DirectoryChooser dirChooser = new DirectoryChooser();

        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();

        File selectedFile = dirChooser.showDialog(stage);

        if(selectedFile != null) { // Also check that the directory contains files with the extension .cipher
            System.out.println(selectedFile.getAbsolutePath());
            controller.decipherFiles(selectedFile);
        } else {
            System.out.println("No file has been selected.");
        }
    }
}


