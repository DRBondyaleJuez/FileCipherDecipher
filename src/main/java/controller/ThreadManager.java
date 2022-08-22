package controller;

import model.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ThreadManager {

    public ThreadManager() {
    }

    public boolean manageDividerThreads(byte[] file, FileDeposit currentFileDeposit, String fileName, String saveDirectory){

        //5 threads max
        int numberOfThreads = 5;

        if(currentFileDeposit.viewTotalNumberOfParts() < numberOfThreads){numberOfThreads= currentFileDeposit.viewTotalNumberOfParts();}

        for (int i = 0; i < numberOfThreads; i++) {
            FileDivider fileDivider = new FileDivider(file,fileName,saveDirectory,currentFileDeposit);
            Thread thread = new Thread(fileDivider);
            thread.start();
        }

        return true;
    }

    public boolean manageCipherThreads(String cipherStoreDirectory,FileDeposit currentFileDeposit){

        //2 threads max
        int numberOfThreads = 2;

        if(currentFileDeposit.viewTotalNumberOfParts() < numberOfThreads){numberOfThreads=currentFileDeposit.viewTotalNumberOfParts();}

        for (int i = 0; i < numberOfThreads; i++) {
            FileCipher fileCipher = new FileCipher(cipherStoreDirectory,currentFileDeposit);
            Thread thread = new Thread(fileCipher);
            thread.start();
        }

        return true;
    }

    public boolean manageDecipherThreads(String[] arrayOfFilePaths,String keyPath, String decipherStoreDirectory){

        //Identify number of files to decipher
        int numberOfFilesToDecipher = arrayOfFilePaths.length;


        //3 threads max
        int numberOfThreads = 3;

        //Create the number of threads necessary for deciphering within the thread pool
        if(numberOfFilesToDecipher < numberOfThreads){numberOfThreads = numberOfFilesToDecipher;}
        int numberOfPartsForEachThreadToDecipher = numberOfFilesToDecipher/numberOfThreads;
        int finalThreadPartsToDecipher = numberOfPartsForEachThreadToDecipher + numberOfFilesToDecipher%numberOfThreads;

        //Creation FileJoiner
        FileJoiner currentFileJoiner = new FileJoiner(numberOfThreads,decipherStoreDirectory);
        System.out.println("FileJoiner Created"); ////////////////////////////////////// DELETE WHEN FINISHED

        //Initialize a variable to create subarray with corresponding list of paths to decipher
        FileDecipher[] fileDecipherArray = new FileDecipher[numberOfThreads];

        //Creation of fileDecipher threads with the particular list of files it is going to decipher, the key and the fileJoiner
        for (int i = 0; i < numberOfThreads; i++) {

            int startPos = i*numberOfPartsForEachThreadToDecipher;
            int endPos = startPos+numberOfPartsForEachThreadToDecipher-1;

            if(i == numberOfThreads-1) {
                startPos = i*numberOfPartsForEachThreadToDecipher;
                endPos = startPos+finalThreadPartsToDecipher-1;
            }

            String[] subArrayForThisThread = new String[endPos - startPos + 1];
            for (int j = 0; j < subArrayForThisThread.length; j++) {
                subArrayForThisThread[j] = arrayOfFilePaths[startPos+j];
            }

            fileDecipherArray[i] = new FileDecipher(subArrayForThisThread,keyPath,currentFileJoiner);
            System.out.println("FileDecipher " +i+ " Created"); ////////////////////////////////////// DELETE WHEN FINISHED
        }

        //Start of fileDecipher threads
        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(fileDecipherArray[i]);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //Final actions of the fileJoiner once all the bytes are there
        currentFileJoiner.decipherByteJoiner();

        System.out.println("If no error message has been displayed the file should be correctly stored in this path: " + decipherStoreDirectory);

        return true;
    }

    //TO DO: CHECK IF THE NUMBER OF FILES IN THE CIPHER FOLDER ARE EQUAL TO THE NUMBER OF PARTS AND INTERRUPT THE TWO CIPHERS


}
