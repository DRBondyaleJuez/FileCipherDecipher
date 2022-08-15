package controller;

import model.FileCipher;
import model.FileDecipher;
import model.FileDivider;
import model.ObserverBetweenDividerAndCipher;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ThreadManager {

    public ThreadManager() {
    }

    public boolean manageDividerThreads(byte[] file,int numberOfParts,String fileName,String saveDirectory){

        //5 threads max
        int numberOfThreads = 5;

        //THIS COULD BE WRONG I ASSUMED EACH THREAD WILL BE IN CHARGED OF ONE PART OF THE DIVIDED FILE
        if(numberOfParts < numberOfThreads){numberOfThreads=numberOfParts;}

        for (int i = 0; i < numberOfThreads; i++) {
            FileDivider fileDivider = new FileDivider(file,fileName,saveDirectory,numberOfParts);
            Thread thread = new Thread(fileDivider);
            thread.start();
        }

        return true;

    }

    public boolean manageCipherThreads(String directoryOfDividedFiles,String cipherStoreDirectory,int numberOfParts){

        //2 threads max
        int numberOfThreads = 2;

        //THIS COULD BE WRONG I ASSUMED EACH THREAD WILL BE IN CHARGED OF ONE PART OF THE DIVIDED FILE
        if(numberOfParts < numberOfThreads){numberOfThreads=numberOfParts;}

        for (int i = 0; i < numberOfThreads; i++) {
            FileCipher fileCipher = new FileCipher(directoryOfDividedFiles,cipherStoreDirectory,numberOfParts);
            Thread thread = new Thread(fileCipher);
            thread.start();
        }

        return true;
    }

    public void continueAddingFilesThroughObserver(int numberOfParts){

        ObserverBetweenDividerAndCipher observer = new ObserverBetweenDividerAndCipher(numberOfParts);

        Thread thread = new Thread(observer);

        System.out.println("Thread manager is going to continue adding files through observer"); /////////////////////////////////////DELETE WHEN FINISHED

        thread.start();

    }


    public boolean manageDecipherThreads(String[] arrayOfFilePaths,String decipherStoreDirectory){

        int numberOfFilesToDecipher = arrayOfFilePaths.length;

        //3 threads max
        int numberOfThreads = 3;

        //THIS COULD BE WRONG I ASSUMED EACH THREAD WILL BE IN CHARGED OF ONE PART OF THE DIVIDED FILE
        if(numberOfFilesToDecipher < numberOfThreads){numberOfThreads = numberOfFilesToDecipher;}

        int numberOfPartsForEachThreadToDecipher = numberOfFilesToDecipher/numberOfThreads;
        int finalThreadPartsToDecipher = numberOfPartsForEachThreadToDecipher + numberOfFilesToDecipher%numberOfThreads;

        FileDecipher[] fileDecipherArray = new FileDecipher[numberOfThreads];

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

            fileDecipherArray[i] = new FileDecipher(subArrayForThisThread);
        }

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(fileDecipherArray[i]);
            thread.start();
        }

        return true;
    }

    //TO DO: CHECK IF THE NUMBER OF FILES IN THE CIPHER FOLDER ARE EQUAL TO THE NUMBER OF PARTS AND INTERRUPT THE TWO CIPHERS


}
