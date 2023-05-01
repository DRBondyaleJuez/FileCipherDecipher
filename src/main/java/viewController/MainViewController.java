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
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Provides the initializable object in charge of acting as a viewController of the view section that correspond to a
 * simple user interface.
 * <p>
 *     This viewController handles fxml view with the options of selecting a file to cipher or selecting a folder with
 *     ciphered fragments to decipher.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class MainViewController implements Initializable {

    private final Controller controller;

    @FXML
    private ImageView mainImageView;

    @FXML
    private AnchorPane mainAnchorPane;


    /**
     * This is the constructor. A controller object corresponding to the attribute controller is instantiated.
     */
    public MainViewController() {
        controller = new Controller();
    }


    /**
     * This is the implementation of the initialize abstract method.
     *  * <p>
     *     Before the view is displayed, the decorative image is set. This method is invoked during the FXMLLoader
     *     class' load method.
     *  * </p>
     * @param url URL object provided during FXMLLoader's load method.
     * @param resourceBundle ResourceBundle object provided during FXMLLoader's load method.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image mainImage = getImageFromResources();
        mainImageView.setImage(mainImage);

    }


    /**
     * This method is called when the corresponding menu item is clicked on the MainView.fxml view. This starts the
     * ciphering process where a file to cipher must be first selected.
     *  * <p>
     *     First it instantiates a FileChooser object and if the user selects an appropriate file the controller is called
     *     to continue the ciphering process. If no file was selected or the file is a null object the app remains open, the ciphering does not continue and a message is
     *     displayed in the console.
     *  * </p>
     */
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

    /**
     * This method is called when the corresponding menu item is clicked on the MainView.fxml view. This starts the deciphering
     * process where first a directory with ciphered file fragments must be selected.
     *  * <p>
     *     First it instantiates a DirectoryChooser object initially set to the fileCipher directory. THis directory is created
     *     when the first file is cipher and contains the folders of the ciphered fragments with a particular extension (.cipher).
     *     If the file is null, empty or doe not contain .cipher files in its entirity nothing happens, the app remains open and a message is
     *     displayed in the console.
     *  * </p>
     */
    @FXML
    public void decipherAction(){
        File initialCipherFilesDir = new File("WorkingFolder\\defaultFolder\\fileCipher");

        DirectoryChooser dirChooser = new DirectoryChooser();

        if(initialCipherFilesDir.exists()){
            dirChooser.setInitialDirectory(initialCipherFilesDir);
        }

        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();

        File selectedFile = dirChooser.showDialog(stage);

        if(checkTheFilesInDirExtension(selectedFile)) {
            System.out.println(selectedFile.getAbsolutePath());
            controller.decipherFiles(selectedFile);
        } else {
            System.out.println("No appropriate file has been selected.");
        }
    }

    private boolean checkTheFilesInDirExtension(File selectedDir){

        if(selectedDir == null) return false;

        String[] cipheredFileNames = selectedDir.list();

        if(cipheredFileNames == null || cipheredFileNames.length == 0) return false;

        for (String cipheredFileName : cipheredFileNames) {
            int currentFileNameLength = cipheredFileName.length();
            String currentExtension = cipheredFileName.substring(currentFileNameLength - 7); //This 7 is because of the length of the extension ".cipher" if this extension changes this needs to be modified too.
            if (!currentExtension.equals(".cipher")) return false;
        }
        return true;
    }

    private Image getImageFromResources(){
        try {
            InputStream imageInputStream = MainViewController.class.getResourceAsStream("/images/cipherLock.png");
            if(imageInputStream == null){
                throw new IOException();
            }
            return new Image(new ByteArrayInputStream(IOUtils.toByteArray(imageInputStream)));
        } catch (IOException e) {
            System.out.println("Unable to retrieve empty grid image");
            e.printStackTrace();
            return null;
        }
    }
}


