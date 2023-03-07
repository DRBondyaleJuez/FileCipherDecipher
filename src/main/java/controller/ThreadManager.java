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

/**
 * Provides the object in charge of managing the distribution of bytearray fragments between the corresponding ciphering or deciphering
 * process that operate concurrently.
 * <p>
 *     To achieve this the ThreadManager uses Thread objects that call runnable class in the utils package and provides FileDeposits object
 *     in the model package which help organize the progression of files which are concurrently being processed. The key associated with the
 *     ciphered fragments is also built here.
 *     Currently:
 *     - 5 threads are used during further division of parts into fragments for ciphering
 *     - 2 threads are used to encrypt and store the divided fragments.
 *     - 3 threads are used during deciphering and a FileJoiner object joins and stores fragments and parts back together.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class ThreadManager {


    /**
     * This method manages the division of the file or file part to be divided into fragments. The number of fragments have been
     * calculated by the controller and fixed in the FileCipherDeposit.
     * <p>
     *   Based on if the number of fragments exceeds the number of threads used for division a number of threads (5 max) are ran.
     *   To do this Thread objects are instantiated are runnable objects of the Class FileDivider are given as arguments of this
     *   Thread objects which then call the method start.     *
     * </p>
     *
     * @param file byte[] which corresponds to the file or file part which is going to be divided before ciphering.
     * @param currentFileCipherDeposit FileCipherDeposit object built at the controller when calculating the number of fragments
     *                                 and will store the divided files putting them also at safe disposition of the following concurrent
     *                                 ciphering threads.
     * @param fileName String containing the generic name of the divided files a serial number or code will be added to each filename
     * @param saveDirectory String containing the path of the directory where the divided fragments are saved before ciphering
     */
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

    /**
     * This method generates the key, a 256 byte array, and uses it to cipher all the fragments present in the directory of divided fragments.
     * <p>
     *     This method is invoked by the controller once the file division has started but the concurrent nature allows this method
     *     to begin before the division ends. 2 threads are used here to cipher using runnable objects of the Class FileCipher as attributes.
     * </p>
     * @param cipherStoreDirectory Sting containing the path where the cyphered fragments should be stored. the key is stored here too.
     * @param currentFileCipherDeposit FileCipherDeposit object which should be the same object as that provided to manageDividerThread method
     *                                 which now contains information about the divided file fragments.
     */
    public void manageCipherThreads(String cipherStoreDirectory, FileCipherDeposit currentFileCipherDeposit){

        //2 threads max
        int numberOfThreads = 2;

        if(currentFileCipherDeposit.viewTotalNumberOfParts() < numberOfThreads){numberOfThreads = currentFileCipherDeposit.viewTotalNumberOfParts();}
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


    /**
     * This method with the corresponding key deciphers the encrypted fragments concurrently using 3 threads.
     * <p>
     *     These threads then use an instance of the Class FileDecipherDeposit to place the deciphered fragments which will
     *     be used after by the FileJoiner to reconstruct the original file.
     * </p>
     * @param arrayOfFilePaths String[] of paths of the encrypted file fragments that will be deciphered
     * @param keyPath String corresponding to the path of the key to decipher the encrypted fragments
     * @param decipherStoreDirectory String of the path where the complete deciphered file will be stored
     */
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
