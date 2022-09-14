package controller;

import model.*;
import utils.ciphering.FileCipher;
import utils.deciphering.FileDecipher;
import utils.ciphering.FileDivider;
import utils.deciphering.FileJoiner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class ThreadManager {

    public ThreadManager() {
    }

    public void manageDividerThreads(byte[] file, FileCipherDeposit currentFileCipherDeposit, String fileName, String saveDirectory) {

        //5 threads max
        int numberOfThreads = 5;

        if (currentFileCipherDeposit.viewTotalNumberOfParts() < numberOfThreads) {
            numberOfThreads = currentFileCipherDeposit.viewTotalNumberOfParts();
        }

        Thread thread;

        for (int i = 0; i < numberOfThreads; i++) {
            FileDivider fileDivider = new FileDivider(file, fileName, saveDirectory, currentFileCipherDeposit,i);
            thread = new Thread(fileDivider);
            thread.start();
        }

        System.out.println("If no error message has been displayed the divided parts of the file and key should be correctly stored in this path: " + saveDirectory);
    }

    public void manageCipherThreads(String cipherStoreDirectory, FileCipherDeposit currentFileCipherDeposit){

        //2 threads max
        int numberOfThreads = 2;

        if(currentFileCipherDeposit.viewTotalNumberOfParts() < numberOfThreads){numberOfThreads= currentFileCipherDeposit.viewTotalNumberOfParts();}
        Thread[] threads = new Thread[numberOfThreads];

        //Generating and Storing Key if it does not exist already

        String keyPath = cipherStoreDirectory + "\\KEY.cipher";
        File keyFile = new File(keyPath);
        if (! keyFile.exists()) {
            byte[] keyByteArray =  new byte[256];
            Random randomGenerator = new Random();
            randomGenerator.nextBytes(keyByteArray);
            storeKey(keyFile,keyByteArray);
            System.out.println("Key file created and stored: " + keyFile); ////////////////////////////////////// DELETE WHEN FINISHED
        }
        byte[] keyByteArray;
        try {
            keyByteArray = Files.readAllBytes(keyFile.toPath());
        } catch (IOException e) {
            System.out.println("Error reading bytes of key file the process has to stop");
            return;
        }

        for (int i = 0; i < numberOfThreads; i++) {
            FileCipher fileCipher = new FileCipher(cipherStoreDirectory, currentFileCipherDeposit,keyByteArray,i);
            threads[i] = new Thread(fileCipher);
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("If no error message has been displayed the cipher parts of the file and key should be correctly stored in this path: " + cipherStoreDirectory);
    }

    private void storeKey(File keyFile, byte[] keyByteArray){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(keyFile);///// NOT ABLE TO SOLVE WARNING
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store key bytes");
        }
        if(fos == null) {
            System.out.println("Unable to use file to store key bytes");
        } else {
            try {
                fos.write(keyByteArray);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes y key file");
            }
        }
    }

    public void manageDecipherThreads(String[] arrayOfFilePaths,String keyPath, String decipherStoreDirectory){
        //Identify number of files to decipher
        int numberOfFilesToDecipher = arrayOfFilePaths.length;

        //3 threads max
        int numberOfThreads = 3;

        //Create the number of threads necessary for deciphering within the thread pool
        if(numberOfFilesToDecipher < numberOfThreads){numberOfThreads = numberOfFilesToDecipher;}

        //Creation FileDecipherDeposit
        FileDecipherDeposit currentFileDecipherDeposit = new FileDecipherDeposit(numberOfFilesToDecipher);
        System.out.println("FileDecipherDeposit Created"); ////////////////////////////////////// DELETE WHEN FINISHED

        //Initialize the array of file decipher that will be created
        FileDecipher[] fileDecipherArray = new FileDecipher[numberOfThreads];

        //Creation of fileDecipher threads with the particular list of files it is going to decipher, the key, the fileDecipherDeposit and the fileJoiner
        for (int i = 0; i < numberOfThreads; i++) {

            fileDecipherArray[i] = new FileDecipher(arrayOfFilePaths,keyPath,currentFileDecipherDeposit,i);
            System.out.println("FileDecipher " +i+ " Created"); ////////////////////////////////////// DELETE WHEN FINISHED
        }

        //Start of fileDecipher threads
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(fileDecipherArray[i]);
            threads[i].start();
        }

        //File Joiner thread
        //Creation FileJoiner
        FileJoiner currentFileJoiner = new FileJoiner(currentFileDecipherDeposit,decipherStoreDirectory);
        System.out.println("FileJoiner Created"); ////////////////////////////////////// DELETE WHEN FINISHED
        Thread fileJoinerThread = new Thread(currentFileJoiner);
        fileJoinerThread.start();
        System.out.println("Starting Final Joining"); ///////////////////////////////////////DELETE AFTER FINISH


        for (int i = 0; i < numberOfThreads; i++) {
            try {
                if(threads[i] != null) {
                    threads[i].join();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
                fileJoinerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("If no error message has been displayed the file should be correctly stored in this path: " + decipherStoreDirectory);
    }
}
