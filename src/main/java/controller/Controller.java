package controller;

import model.FileDeposit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Controller {

    private File directoryToStoreDividedFile;
    private File directoryToStoreCipherFile;
    private ThreadManager threadManager;
    private File directoryToStoreDecipherFile;

    public Controller() {
        createOrUseFileDirectories();
        threadManager= new ThreadManager();
    }

    //Cipher SECTION

    public void cipherFile(File file){

        //Obtaining Filename and format
        String fileNameAndFormat = file.getName();
        String[] separateNameAndFormat = fileNameAndFormat.split("\\.");
        String fileFormat = separateNameAndFormat[separateNameAndFormat.length-1];
        String fileName = fileNameAndFormat.replace("."+fileFormat,"");


        //DIVISION

        //Obtaining file in byte array form
        byte[] fileByteArray = null;

        try {
            fileByteArray = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return;
        }

        //Calculating number of parts necessary to divide the file
        int numberOfParts = numberOfPartsBasedOnFileSize(file);

        //Creating File Deposit
        FileDeposit currentFileDeposit = new FileDeposit(numberOfParts,null);

        //Creating the folder to store divisions
        String projectPath = directoryToStoreDividedFile.toString()+"\\" + fileName + "_" + fileFormat;
        File dividedFilesDir = new File(projectPath);

        if (! dividedFilesDir.exists()) {
            dividedFilesDir.mkdirs();
        }

        //Managing thread for file division
        threadManager.manageDividerThreads(fileByteArray,currentFileDeposit,fileName+"_"+fileFormat,projectPath);

        //CIPHER

        //Creating the folder to store ciphers
        projectPath = directoryToStoreCipherFile.toString()+"\\" + fileName +"_"+fileFormat;
        File cipherFilesDir  = new File(projectPath);
        if (! cipherFilesDir.exists()) {
            cipherFilesDir.mkdirs();
        }

        //Managing thread for file cipher
        threadManager.manageCipherThreads(cipherFilesDir.toString(),currentFileDeposit);
        //Reassure file transfer while threads are working
    }



    //DECIPHER SECTION

    public void decipherFiles(File cipheredFilesFolder){

        String[] cipheredFileNames = cipheredFilesFolder.list();

        int numberOfFilesToDecipher = cipheredFileNames.length-1;
        if(numberOfFilesToDecipher <= 0) {
            System.out.println("There were no cipheredFileNames in this directory: " + cipheredFilesFolder);
            return;
        }


        //Extracting and writing name of deciphered file
        String rawName = cipheredFilesFolder.getName();
        String[] nameAndFormat = rawName.split("_");
        String format = nameAndFormat[nameAndFormat.length-1];
        String name = rawName.replace("_"+format," (DECIPHER)."+format);

        String decipherFileStoreDirectory = directoryToStoreDecipherFile.getAbsolutePath() + "\\" + name;

        //Extracting and separating Key and cipheredFileNames

        String[] filesPathsNoKey = new String[numberOfFilesToDecipher];
        String keyFilePath = "";

        if(cipheredFileNames[0].equals("KEY.cipher")){
            for (int i = 1; i < numberOfFilesToDecipher+1; i++) {
                filesPathsNoKey[i-1] = cipheredFilesFolder.getAbsolutePath() + "\\" + cipheredFileNames[i];
            }
            keyFilePath = cipheredFilesFolder.getAbsolutePath() + "\\" + cipheredFileNames[0];
        } else if(cipheredFileNames[numberOfFilesToDecipher].equals("KEY.cipher")){
            for (int i = 0; i < numberOfFilesToDecipher; i++) {
                filesPathsNoKey[i] = cipheredFilesFolder.getAbsolutePath() + "\\" + cipheredFileNames[i];
            }
            keyFilePath = cipheredFilesFolder.getAbsolutePath() + "\\" + cipheredFileNames[numberOfFilesToDecipher];
        } else {
            System.out.println("Key file is neither the first or the last file so perhaps the naming convention of the file to decipher is particularly complicated.");
            return;
        }

        threadManager.manageDecipherThreads(filesPathsNoKey,keyFilePath,decipherFileStoreDirectory);
    }


    private void createOrUseFileDirectories(){

        File dividedFilesDir = new File("WorkingFolder\\defaultFolder\\fileDivisions");

        if (! dividedFilesDir.exists()) {
            dividedFilesDir.mkdirs();
        }
        directoryToStoreDividedFile = dividedFilesDir;

        File cipherFilesDir = new File("WorkingFolder\\defaultFolder\\fileCipher");
        if (! cipherFilesDir.exists()) {
            cipherFilesDir.mkdirs();
        }
        directoryToStoreCipherFile = cipherFilesDir;

        File decipherFilesDir = new File("WorkingFolder\\defaultFolder\\fileDecipher");
        if (! decipherFilesDir.exists()) {
            decipherFilesDir.mkdirs();
        }

        directoryToStoreDecipherFile = decipherFilesDir;
    }

    private int numberOfPartsBasedOnFileSize(File file){
        int numberOfParts = 1;

        double fileSizeInKb = (double) file.length()/1024;
        double fileSizeInMb = fileSizeInKb/1024;

        //First check if it is below 10kb
        if(fileSizeInKb < 10){
            return 1;
        }

        if(fileSizeInMb < 5){
            return 2;
        }

        numberOfParts = (int)fileSizeInMb/5;
        if(fileSizeInMb%5 != 0){
            numberOfParts++;
        }

        return numberOfParts;
    }

}
