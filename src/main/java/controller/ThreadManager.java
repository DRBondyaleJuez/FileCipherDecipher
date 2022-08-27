package controller;

import model.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ThreadManager {

    public ThreadManager() {
    }

    public boolean manageDividerThreads(byte[] file, FileDeposit currentFileDeposit, String fileName, String saveDirectory) {

        //5 threads max
        int numberOfThreads = 5;

        if (currentFileDeposit.viewTotalNumberOfParts() < numberOfThreads) {
            numberOfThreads = currentFileDeposit.viewTotalNumberOfParts();
        }

        Thread thread = null;

        for (int i = 0; i < numberOfThreads; i++) {
            FileDivider fileDivider = new FileDivider(file, fileName, saveDirectory, currentFileDeposit,i);
            thread = new Thread(fileDivider);
            thread.start();
        }

        System.out.println("If no error message has been displayed the divided parts of the file and key should be correctly stored in this path: " + saveDirectory);

        return true;
    }

    public boolean manageCipherThreads(String cipherStoreDirectory,FileDeposit currentFileDeposit){

        //2 threads max
        int numberOfThreads = 2;

        if(currentFileDeposit.viewTotalNumberOfParts() < numberOfThreads){numberOfThreads=currentFileDeposit.viewTotalNumberOfParts();}

        Thread thread = null;

        for (int i = 0; i < numberOfThreads; i++) {
            FileCipher fileCipher = new FileCipher(cipherStoreDirectory,currentFileDeposit,i);
            thread = new Thread(fileCipher);
            thread.start();
        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException nE){
            throw new NullPointerException("Thread of fileCipher not able to properly initialize");
        }
        System.out.println("If no error message has been displayed the cipher parts of the file and key should be correctly stored in this path: " + cipherStoreDirectory);

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

            fileDecipherArray[i] = new FileDecipher(subArrayForThisThread,keyPath,currentFileJoiner,i);
            System.out.println("FileDecipher " +i+ " Created"); ////////////////////////////////////// DELETE WHEN FINISHED
        }

        //Start of fileDecipher threads
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(fileDecipherArray[i]);
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException nE){
                throw new NullPointerException("Thread of fileDecipher not able to properly initialize");
            }
        }

        //Final actions of the fileJoiner once all the bytes are there
        System.out.println("Starting Final Joining"); ///////////////////////////////////////DELETE AFTER FINISH
        currentFileJoiner.decipherByteJoiner();

        System.out.println("If no error message has been displayed the file should be correctly stored in this path: " + decipherStoreDirectory);

        return true;
    }




}
