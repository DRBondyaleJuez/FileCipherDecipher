package utils.deciphering;

import model.FileDecipherDeposit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Provides the runnable objects provided to Threads in charge of deciphering previously encrypted fragments.
 * <p>
 *     This class implement the interface Runnable.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileDecipher implements Runnable{

    private final int id;
    private final String[] pathsOfFilesToDecipher;
    private byte[] decipherKey;
    private final FileDecipherDeposit fileDecipherDeposit;

    /**
     * This is the constructor of this runnable Class.
     * @param pathsOfFilesToDecipher String array corresponding to the paths of encrypted fragments to be deciphered.
     * @param keyFileString String corresponding to the path of the key file needed to decipher.
     * @param fileDecipherDeposit FileDecipherDeposit object that keeps track of the deciphered fragments and is used to organize
     *                            the access of the threads to the fragments to decipher.
     * @param threadId int identifying the current thread which in this case can be 0 to 2 since it is working with 2 threads.
     */
    public FileDecipher(String[] pathsOfFilesToDecipher, String keyFileString, FileDecipherDeposit fileDecipherDeposit, int threadId) {

            this.fileDecipherDeposit = fileDecipherDeposit;
            try {
                decipherKey = Files.readAllBytes(Path.of(keyFileString));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Was not able to convert key file to byte array");
            }

        id = threadId;
        this.pathsOfFilesToDecipher = pathsOfFilesToDecipher;

        System.out.println("FileDecipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }

    /**
     * Implementation of the abstract method of the Runnable interface to perform the concurrent deciphering of the encrypted fragments.
     * <p>
     *     The fragments are deciphered in a loop where the fragment to decipher is provided by the FileDecipherDeposit which has
     *     a synchronized get method to avoid issue in the critical section when the threads are operating simultaneously.
     *     The end of the loop is also declared by the FileDecipherDeposit.
     * </p>
     */
    @Override
    public void run() {

        while(fileDecipherDeposit.viewNumberOfPartsDeciphered() < fileDecipherDeposit.viewTotalNumberOfParts()){
            int part = fileDecipherDeposit.getPartToDecipher();
            if(part > fileDecipherDeposit.viewTotalNumberOfParts()){break;}
            byte[] byteArrayReadyForDecipher = getDecipherByteArray(part);
            byte[] byteArrayReadyForFiling = unXORArray(byteArrayReadyForDecipher);

            ArrayList<Byte> byteListReadyForFiling = new ArrayList<>();
            for (byte b : byteArrayReadyForFiling) {
                byteListReadyForFiling.add(b);
            }

            fileDecipherDeposit.addBytesToFile(part,byteListReadyForFiling);

            System.out.println("I am thread " + id + ". Part " + part + " unXORted added");
        }
    }

    //Convert file that needs deciphering to its byte array form
    private byte[] getDecipherByteArray(int fileListPosition) {

        byte[] currentByteArrayToDecipher = null;

        try {
            currentByteArrayToDecipher = Files.readAllBytes(Path.of(pathsOfFilesToDecipher[fileListPosition]));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert key file to byte array");
        }
        return currentByteArrayToDecipher;
    }

    //Undo the xor ciphering of the byte array of the file
    private byte[] unXORArray(byte[] byteArrayToUnXOR){
        byte[] unXoredArray = new byte[byteArrayToUnXOR.length];

        for (int i = 0; i < byteArrayToUnXOR.length; i++) {
            unXoredArray[i] = (byte) (byteArrayToUnXOR[i] ^ decipherKey[i%256]);
        }
        return unXoredArray;
    }
}
