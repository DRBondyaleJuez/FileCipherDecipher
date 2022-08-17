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
        if (! cipherFilesDir.exists()) {
            cipherFilesDir.mkdirs();
        }
        directoryToStoreDecipherFile = decipherFilesDir;

    }

    public void cipherFile(File file){

        //Obtaining Filename and format
        String fileName = file.getName();
        String[] nameAndFormat = fileName.split("\\W");
        String fileFormat = nameAndFormat[nameAndFormat.length-1];
        String[] fileName1 = fileName.split("."+fileFormat);
        fileName = fileName1[0];

        System.out.println(fileName); ///////////////////DELETE WHEN FINISHED
        System.out.println(fileFormat); ///////////////////DELETE WHEN FINISHED

        //DIVISION

        //Obtaining file in byte array form
        byte[] fileByteArray = null;

        try {
            fileByteArray = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            gracefulShutdown("File could not be converted to byteArray");
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
        directoryToStoreDividedFile = dividedFilesDir;

        //Managing thread for file division
        threadManager.manageDividerThreads(fileByteArray,currentFileDeposit,fileName+"_"+fileFormat,projectPath);

        //CIPHER

        //Creating the folder to store ciphers
        projectPath = directoryToStoreCipherFile.toString()+"\\" + fileName +"_"+fileFormat;
        File cipherFilesDir  = new File(projectPath);
        if (! cipherFilesDir.exists()) {
            cipherFilesDir.mkdirs();
        }
        directoryToStoreCipherFile = cipherFilesDir;

        //Managing thread for file cipher
        System.out.println("threadManager calls fileCiphers");////////////////////////////////////////////////////////////////////////////DELETE WHEN FINISHED
        threadManager.manageCipherThreads(directoryToStoreCipherFile.toString(),currentFileDeposit);
        //Reassure file transfer while threads are working
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


    //DECIPHER SECTION

    public void decipherFiles(File filesToDecipher){

        String[] files = filesToDecipher.list();
        int numberOfFilesToDecipher = files.length;

        if(numberOfFilesToDecipher == 0) {
            System.out.println("There were no files in this directory: " + filesToDecipher);
            return;
        }
        System.out.println(numberOfFilesToDecipher);
        for (String file : files) {
            System.out.println(file);
        }

        //Extracting and writing name of deciphered file
        String rawName = filesToDecipher.getName();
        String[] nameAndFormat = rawName.split("_");
        String format = nameAndFormat[nameAndFormat.length-1];
        String name = rawName.replace("_"+format," (DECIPHER)."+format);
        System.out.println(name);

        String decipherFileStoreDirectory = directoryToStoreDecipherFile.getAbsolutePath()+"\\name";

        //threadManager.manageDecipherThreads(files,decipherFileStoreDirectory);





    }

    public void gracefulShutdown(String errorMessage){
        System.out.println("Graceful shutdown due to Error involved: " + errorMessage);
        System.exit(-1);
    }


    //Give cipher files directory to the decipher while creating their threads









}
