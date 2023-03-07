package utils.deciphering;

import model.FileDecipherDeposit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides the runnable objects provided to Threads in charge of joining decipher fragments back to form a file.
 * <p>
 *     This class implement the interface Runnable.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileJoiner implements Runnable {
    private final String decipherFileStorage;
    private final FileDecipherDeposit fileDecipherDeposit;

    /**
     * This is the constructor of this runnable Class.
     * @param fileDecipherDeposit FileDecipherDeposit object that keeps track of the deciphered fragments and is used to organize
     *                            the access of the threads to the fragments to join.
     * @param decipherFileStorage String corresponding to the path where the decipher file should be stored.
     */
    public FileJoiner(FileDecipherDeposit fileDecipherDeposit, String decipherFileStorage) {
        this.fileDecipherDeposit = fileDecipherDeposit;
        this.decipherFileStorage = decipherFileStorage;
    }

    /**
     * Implementation of the abstract method of the Runnable interface to perform the concurrent joining of the deciphered fragments.
     * <p>
     *     The fragments are joined in the correct order to recuperate the original. The fileJoiner runs parallel to the decipherer
     *     the iterative process of the joiner crete and keeps adding or writing bytes to the stored file representing the decipher
     *     complete file until the FileDecipherDeposit causes the iteration to end confirming that all the fragments have been joined.
     * </p>
     */
    @Override
    public void run() {

        //Check if file deciphered already exists
        File possiblePreviousDecipherFile = new File(decipherFileStorage);
        if(possiblePreviousDecipherFile.exists()){
            boolean deletedPreviousSimilarDecipher = possiblePreviousDecipherFile.delete();
            if(deletedPreviousSimilarDecipher) {
                System.out.println("A previous deciphering of file - " + decipherFileStorage + " - has been eliminated before the new deciphering"); //////////////////DELETE WHEN FINISHED
            }
        }

        for (int i = 0; i < fileDecipherDeposit.viewTotalNumberOfParts(); i++) {
            byte[] byteArrayForFiling = fileDecipherDeposit.getBytesReadyToFile(i);
            storeDecipherFile(byteArrayForFiling);
            System.out.println("Deciphered part " + i + " stored in file " + decipherFileStorage); ///////////////////////DELETE WHEN FINISHED
        }
    }

    private void storeDecipherFile(byte[] finalByteArray){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(decipherFileStorage,true); /////UNABLE TO FIX THIS WARNING
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store cipher bytes: " + decipherFileStorage);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store cipher bytes: " + decipherFileStorage);
        } else {
            try {
                fos.write(finalByteArray);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of cipher file: " + decipherFileStorage);
            }
        }
    }
}
