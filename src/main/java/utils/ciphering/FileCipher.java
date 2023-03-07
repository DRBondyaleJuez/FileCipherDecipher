package utils.ciphering;

import model.FileCipherDeposit;

import java.io.*;
import java.nio.file.Files;

/**
 * Provides the runnable objects provided to Threads in charge of ciphering previously divided fragments.
 * <p>
 *     This class implement the interface Runnable.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileCipher implements Runnable{

    private final int id;
    private final String saveCipherDirectory;
    private final FileCipherDeposit fileCipherDeposit;
    private static byte[] key;

    /**
     * This is the constructor of this runnable Class
     * @param saveCipherDirectory String corresponding to the path where the ciphered fragments will be stored.
     * @param currentFileCipherDeposit FileCipherDeposit object involved in the file division containing the number of
     *                                 fragments required for the file, the number of fragments ready for ciphering
     *                                 and their locations. Will also track the progression of the ciphering.
     * @param keyByteArray byte[] of the key needed to cipher the divided fragments.
     * @param threadId int identifying the current thread which in this case can be 0 or 1 since it is working with 2 threads.
     */
    public FileCipher(String saveCipherDirectory, FileCipherDeposit currentFileCipherDeposit, byte[] keyByteArray, int threadId) {
        id = threadId;
        this.saveCipherDirectory = saveCipherDirectory;
        this.fileCipherDeposit = currentFileCipherDeposit;
        key = keyByteArray;

        System.out.println("FileCipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }


    /**
     * Implementation of the abstract method of the Runnable interface to perform the concurrent ciphering of the file into fragments.
     * <p>
     *     The fragments are ciphered in a loop where the fragment to cipher is provided by the FileCipherDeposit which has
     *     a synchronized get method to avoid issue in the critical section when the threads are operating simultaneously.
     *     The end of the loop is also declared by the FileCipherDeposit.
     * </p>
     */
    @Override
    public void run() {

        while(fileCipherDeposit.viewTotalNumberOfFileCipher() < fileCipherDeposit.viewTotalNumberOfParts()) {
            //Get key
            byte[] currentKey = key;

            //GetFile content in byte array format
            File fileToProcess = getNextFile();
            if(fileToProcess == null){break;} //This has to do with threads that are waiting in the getNextFile method when there is no file Not sure how to finish those
            System.out.println("I am thread " +id+". File to cipher obtained"+fileToProcess.getName()); ///////////////////////////////////////////////DELETE WHEN FINISHED

            String fileName = fileToProcess.getName().replace(".txt",".cipher");
            byte[] byteContentFileToProcess = getByteArrayFromDividedFile(fileToProcess);
            System.out.println("I am thread " +id+". Byte array from file to cipher obtained "+fileToProcess.getName()); ////////////////////////////////////// DELETE WHEN FINISHED


            //XORting
            byte[] xortedByteArray = xorCipherByteArray(currentKey,byteContentFileToProcess);
            System.out.println("I am thread " +id+". File byte Array XORted "+fileToProcess.getName());

            //Storing
            storeCipherFilePart(xortedByteArray,fileName);
            System.out.println("I am thread " +id+". File byte Array stored "+fileToProcess.getName());
        }

    }

    private File getNextFile(){
        String fileToCipherPath = fileCipherDeposit.getFileReadyToCipher();
        if(fileToCipherPath == null){return null;}

        return new File(fileToCipherPath);
    }

    private byte[] getByteArrayFromDividedFile(File processedFile){

        byte[] partFileByteArray = null;

        try {
            partFileByteArray = Files.readAllBytes(processedFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert part file to byte array");
        }

        return partFileByteArray;
    }

    private byte[] xorCipherByteArray(byte[] key, byte[] fileContent){

        byte[] xoredArray = new byte[fileContent.length];

        for (int i = 0; i < fileContent.length; i++) {
            xoredArray[i] = (byte) (fileContent[i] ^ key[i%256]);
        }

        return xoredArray;
    }


    private void storeCipherFilePart(byte[] cipherBytes, String fileName){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveCipherDirectory + "\\" + fileName);///// NOT ABLE TO SOLVE WARNING
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store cipher bytes: " + fileName);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store cipher bytes: " + fileName);
        } else {
            try {
                fos.write(cipherBytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of cipher file: " + fileName);
            }
        }
    }

}
