package controller;

import model.FileCipherDeposit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Controller {

    private File directoryToStoreDividedFile;
    private File directoryToStoreCipherFile;
    private final ThreadManager threadManager;
    private File directoryToStoreDecipherFile;

    public Controller() {
        createOrUseFileDirectories();
        threadManager= new ThreadManager();
    }

    //Cipher SECTION

    public void cipherFile(File file) {

        //DIVISION
        long fileSize = file.length();
        System.out.println("File size: "+fileSize); //////////////////////////////////DELETE WHEN FINSIHED

        //File will be considered too large when it exceeds 500Mb i.e. the process will be performed by 500 byteArrays
        int subFileSize = 500 * 1024 * 1024;

        System.out.println("Subfile size: " + subFileSize);
        long numberOfRepetitionToProcessFile = fileSize /  subFileSize;

        if (fileSize % subFileSize != 0) {
            numberOfRepetitionToProcessFile++;
        }
        System.out.println("NumberOfRepsToProcessFile: " + numberOfRepetitionToProcessFile); //////////////////////////////////DELETE WHEN FINSIHED

        int sizeOfThisSubFile = subFileSize;
        if (fileSize < sizeOfThisSubFile) {
            sizeOfThisSubFile = (int) fileSize;
        }

        System.out.println("sizeOfThisSubFile: "+ sizeOfThisSubFile); //////////////////////////////////DELETE WHEN FINSIHED

        byte[] subFileByteArray = new byte[sizeOfThisSubFile];
        System.out.println("Empty subfile array built of size: "+subFileByteArray.length);

        int repCounter = 0;
        try {
            InputStream inputStream = new FileInputStream(file);

            while ((inputStream.read(subFileByteArray)) != -1) {
                //byte array is now filled. Do something with it.
                processCurrentFileByteArray(file,subFileByteArray,repCounter);
                repCounter++;
                if(repCounter < numberOfRepetitionToProcessFile) {
                    System.out.println("STARTING ANOTHER SECTION (Section "+repCounter+") TO PROCESS FILE WHICH EXCEEDS 500Mb"); ///////////////////////DELETE WHEN FINISH

                    sizeOfThisSubFile = subFileSize;
                    long readBytes = (long)subFileSize * repCounter;
                    if (fileSize - readBytes < sizeOfThisSubFile) {
                        sizeOfThisSubFile = (int)(fileSize - readBytes);
                    }
                    subFileByteArray = new byte[sizeOfThisSubFile];
                }
            }

            inputStream.close();

        } catch (IOException ioe) {
            System.out.println("Error " + ioe.getMessage());
            System.out.println("Unable to input stream file: " + file.toString());
        }
    }

    private void processCurrentFileByteArray(File file, byte[] currentByteArray, int repNumber){

        //Obtaining Filename and format
        String fileNameAndFormat = file.getName();
        String[] separateNameAndFormat = fileNameAndFormat.split("\\.");
        String fileFormat = separateNameAndFormat[separateNameAndFormat.length - 1];
        String fileName = fileNameAndFormat.replace("." + fileFormat, "");

        //Calculating number of parts necessary to divide the file
        int numberOfParts = numberOfPartsBasedOnFileSize(currentByteArray);

        //Creating File Deposit
        FileCipherDeposit currentFileCipherDeposit = new FileCipherDeposit(numberOfParts);

        //Creating the folder to store divisions
        String projectPath = directoryToStoreDividedFile.toString()+"\\" + fileName + "_" + fileFormat;
        File dividedFilesDir = new File(projectPath);

        if (! dividedFilesDir.exists()) {
            boolean newDirectoryMade = dividedFilesDir.mkdirs(); ////// I DON'T UNDERSTAND THE WARNING
            if(!newDirectoryMade){
                System.out.println("Unable to make specific file divide directory");
            }
        }

        //Managing thread for file division
        threadManager.manageDividerThreads(currentByteArray, currentFileCipherDeposit,fileName+"_"+fileFormat+"_Section_"+repNumber, projectPath);

        //CIPHER

        //Creating the folder to store ciphers
        projectPath = directoryToStoreCipherFile.toString()+"\\" + fileName +"_"+fileFormat;
        File cipherFilesDir  = new File(projectPath);
        if (! cipherFilesDir.exists()) {
            boolean newDirectoryMade = cipherFilesDir.mkdirs(); ////// I DON'T UNDERSTAND THE WARNING
            if(!newDirectoryMade){
                System.out.println("Unable to make specific file cipher directory");
            }
        }

        //Managing thread for file cipher
        threadManager.manageCipherThreads(cipherFilesDir.toString(), currentFileCipherDeposit);
        //Reassure file transfer while threads are working
    }



    //DECIPHER SECTION

    public void decipherFiles(File cipheredFilesFolder){

        String[] cipheredFileNames = cipheredFilesFolder.list();

        if(cipheredFileNames == null){return;}

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
        String keyFilePath;

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
            boolean newDirectoryMade = dividedFilesDir.mkdirs(); ////// I DON'T UNDERSTAND THE WARNING
            if(!newDirectoryMade){
                System.out.println("Unable to make divide directory");
            }
        }
        directoryToStoreDividedFile = dividedFilesDir;

        File cipherFilesDir = new File("WorkingFolder\\defaultFolder\\fileCipher");
        if (! cipherFilesDir.exists()) {
            boolean newDirectoryMade = cipherFilesDir.mkdirs(); ////// I DON'T UNDERSTAND THE WARNING
            if(!newDirectoryMade){
                System.out.println("Unable to make cipher directory");
            }
        }
        directoryToStoreCipherFile = cipherFilesDir;

        File decipherFilesDir = new File("WorkingFolder\\defaultFolder\\fileDecipher");
        if (! decipherFilesDir.exists()) {
            boolean newDirectoryMade = decipherFilesDir.mkdirs(); ////// I DON'T UNDERSTAND THE WARNING
            if(!newDirectoryMade){
                System.out.println("Unable to make decipher directory");
            }
        }

        directoryToStoreDecipherFile = decipherFilesDir;
    }

    private int numberOfPartsBasedOnFileSize(byte[] currentByteArray){
        int numberOfParts;

        double fileSizeInKb = (double) currentByteArray.length/1024;
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
