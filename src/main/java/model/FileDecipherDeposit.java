package model;

import java.util.ArrayList;

/**
 * Provides a monitor class that regulates access to shared resources that allow the synchronization and  organization of
 * the FileDecipher and FileJoiner Runnable Classes when they operate concurrently.
 * <p>
 *     Multiple methods in this class are synchronized.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileDecipherDeposit {

    private final int totalNumberOfParts;
    private int numberOfPartsDeciphered;
    private final ArrayList<Byte>[] bytesReadyToFileArray;

    private int numberOfPartsAddedToFile;

    /**
     * This is the constructor. The number of fragments deciphered and the number added to file as well as the arraylist begin
     * at 0 and empty and fill while the FileDecipher and FileJoiner are interacting with it.
     * @param partsToBeDeciphered int corresponding to the number of encrypted fragments needed to decipher to recuperate
     *                            the original file
     */
    public FileDecipherDeposit(int partsToBeDeciphered) {
        totalNumberOfParts = partsToBeDeciphered;
        numberOfPartsDeciphered = 0;
        bytesReadyToFileArray = new ArrayList[partsToBeDeciphered]; /////////////////////////DON'T KNOW HOW TO FIX THIS WARNING
        numberOfPartsAddedToFile = 0;
    }

    /**
     * This method retrieves the current fragment index, this is, the first fragment is 0, the second is 1 and so on.
     * Once a fragment index has been retrieve it has to increase by one so the next petition knows that the previous index
     * is already being worked on.
     * <p>
     *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     * </p>
     * @return int which corresponds to the index of the fragment that will be deciphered next
     */
    public synchronized int getPartToDecipher(){
        int partToDivide = numberOfPartsDeciphered;
        numberOfPartsDeciphered++;
        return partToDivide;
    }

    /**
     * Method to only view the number of fragments already deciphered not to get the index of the next one.
     * It is used to end loops by comparing it to the number of total fragments required to decipher.
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @return int corresponding to the number of fragments that have been deciphered
     */
    public synchronized int viewNumberOfPartsDeciphered(){
        return numberOfPartsDeciphered;
    }

    /**
     * Returns the total number of fragments needed to decipher.
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @return int corresponding to the total number of fragments required for this particular file or file part.
     */
    public synchronized int viewTotalNumberOfParts(){
        return totalNumberOfParts;
    }


    /**
     * Provided an int index it returns the fragment of deciphered byte[] that correspond to that index.
     * In case the [] of arrayList of bytes is not ready, this is, it is equal to null the thread requiring it must wait.
     * This is why it is conditionally synchronized.
     * <p>
     *     To prevent a memory out of bound error when the bytes are retrieve to rebuild the file the corresponding fragment
     *     position in the [] of arrayList of bytes is set to null.
     *     Since another way to cause a memory out of bound error is an excess of this arrayList of bytes when one is set to null
     *     the threads are notified in case the FileDecipher is waiting to add new byte arrayLists.
     * </p>
     * @param part int that corresponds to the index of the fragment required
     * @return byte[] which correspond to the deciphered byte arrayList of the particular fragment required
     */
    public synchronized byte[] getBytesReadyToFile(int part){

        while(bytesReadyToFileArray[part]==null){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        byte[] bytesToFile = new byte[bytesReadyToFileArray[part].size()];
        for (int i = 0; i < bytesReadyToFileArray[part].size(); i++) {
            bytesToFile[i] = bytesReadyToFileArray[part].get(i);
        }

        //After the byteArray is extracted and ready to return to the filejoiner the arraylist source is cleared from
        //the file decipher deposit to free space to allow deciphering of bigger files.
        bytesReadyToFileArray[part] = null;
        numberOfPartsAddedToFile++;
        notifyAll();

        return bytesToFile;
    }

    /**
     * The method by which the FileDecipher adds byte arrayList in a particular position to avoid loosing the order and
     * ready to be used to rebuild the original file.
     * <p>
     *     It is conditionally synchronized to prevent a memory out of bound error. The condition is that too many deciphered
     *     byte arrayList can't accumulate. So, if the index or position the deciphered arrayList is going to be added to (which corresponds
     *     to the fragment that has been deciphered) is 10 positions in front of the current fragment index that has been added
     *     to the rebuilt file, the addition of more byte arraylist must wait.
     *     On the contrary, if the [] of arrayList of bytes is empty any method trying to retrieve data from it should wait.
     *     This is why it also notifies when a deciphered arrayList of bytes is added.
     * </p>
     * @param part index of the fragment this deciphered bytes belong to
     * @param listOfDecipheredBytes the deciphered bytes ready to be used to rebuild
     */
    public synchronized void addBytesToFile(int part,ArrayList<Byte> listOfDecipheredBytes){

        //To try to avoid out of memory error we buffered slightly the addition of byte arrays by the threads here
        while(part > numberOfPartsAddedToFile + 10){
            try {
                System.out.println("I am a deciphering thread trying to add part " + part + " to decipher deposit. But there have been only " + numberOfPartsAddedToFile +
                        " parts added to the file. To avoid out of memory error I will wait");
                wait();
            } catch (InterruptedException e) {
                System.out.println("The deciphering of part " +part+" was interrupted.");
                throw new RuntimeException(e);
            }
        }

        bytesReadyToFileArray[part] = listOfDecipheredBytes;
        notifyAll();
    }
}
