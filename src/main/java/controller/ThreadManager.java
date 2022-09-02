package controller;

import model.*;
import utils.ciphering.FileCipher;
import utils.deciphering.FileDecipher;
import utils.ciphering.FileDivider;
import utils.deciphering.FileJoiner;

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

        byte[] keyByteArray =  new byte[256];
        Random randomGenerator = new Random();
        randomGenerator.nextBytes(keyByteArray);

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

    public void manageDecipherThreads(String[] arrayOfFilePaths,String keyPath, String decipherStoreDirectory){
        //Identify number of files to decipher
        int numberOfFilesToDecipher = arrayOfFilePaths.length;

        //3 threads max
        int numberOfThreads = 3;

        //Create the number of threads necessary for deciphering within the thread pool
        if(numberOfFilesToDecipher < numberOfThreads){numberOfThreads = numberOfFilesToDecipher;}
        int numberOfPartsForEachThreadToDecipher = numberOfFilesToDecipher/numberOfThreads;
        int finalThreadPartsToDecipher = numberOfPartsForEachThreadToDecipher + numberOfFilesToDecipher%numberOfThreads;



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

        //TODO: Put the array of arrayList outside i.e. in the thread manager and give it to the file joiner or even think of making the writing process of the fos concurrent
        //Think about making the threads return decipher byte array and writing them in file using append or something like that to keep adding to the decipher byte array to the final file
        //in order
        
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
