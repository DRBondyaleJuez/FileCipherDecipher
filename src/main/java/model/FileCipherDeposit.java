package model;

import java.util.ArrayList;

/**
 * Provides a monitor class that regulates access to shared resources that allow the synchronization and  organization of
 * the FileDivider and FileCipher Runnable Classes when they operate concurrently.
 * <p>
 *     Multiple methods in this class are synchronized.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileCipherDeposit {

    private final int totalNumberOfParts;
    private int numberOfFilesCipher;
    private int numberOfPartsDivided;
    private final ArrayList<String> fileReadyToCipherList;

    /**
     * This is the constructor. When first instantiated the number of fragments created by the dividers and the number of fragments
     * ciphered are both initialized at 0 and the ArrayList of strings corresponding to paths of the fragments ready to
     * cipher also begins empty.
     * @param partsToBeDivided int that informes this FileCipherDeposit how many fragments of the file or file part are going
     *                         to be generated based on the established size of each fragment
     */
    public FileCipherDeposit(int partsToBeDivided) {

        totalNumberOfParts = partsToBeDivided;
        numberOfFilesCipher = 0;
        numberOfPartsDivided = 0;
        this.fileReadyToCipherList = new ArrayList<>();
    }

    /**
     * This method retrieves the current fragment index, this is, the first fragment is 0, the second is 1 and so on.
     * Once a fragment index has been retrieve it has to increase by one so the next petition knows that the previous index
     * is already being worked on.
     * <p>
     *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     * </p>
     * @return int which corresponds to the index of the fragment that will be divided next
     */
    public synchronized int getPartToDivide(){
        int partToDivide = numberOfPartsDivided;
        numberOfPartsDivided++;
        return partToDivide;
    }

    /**
     * Method to only view the number of fragments already divided not to get the index of the next one.
     * It is used to end loops by comparing it to the number of total fragments required.
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @return int corresponding to the number of fragments that have been created through division
     */
    public synchronized int viewNumberOfPartsDivided(){
        return numberOfPartsDivided;
    }

    /**
     * Returns the total number of fragments needed.
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @return int corresponding to the total number of fragments required for this particular file or file part.
     */
    public synchronized int viewTotalNumberOfParts(){
        return totalNumberOfParts;
    }

    /**
     * Return the number of fragments extracted from the filesReadyToCipher ArrayList
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @return int corresponding to the number of fragments that have been ciphered.
     */
    public synchronized int viewTotalNumberOfFileCipher(){
        return numberOfFilesCipher;
    }

    /**
     * Returns the path of a fragment belonging to the divided file that has been stored and is ready for encryption.
     * This method is conditionally synchronized since the ArrayList of Strings could be empty. This would require the thread
     * petitioning the information to start ciphering to wait until notified.
     * @return String corresponding to the path of a recently created file fragment ready to be ciphered
     */
    public synchronized String getFileReadyToCipher(){

        while(fileReadyToCipherList.size() == 0){
            try {
                if(numberOfFilesCipher == totalNumberOfParts){ return null;}
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        numberOfFilesCipher++;

        String fileReadyToCipher = fileReadyToCipherList.get(0);
        fileReadyToCipherList.remove(0);
        return fileReadyToCipher;
    }


    /**
     * Adds the string of the path of a recently created fragment of the file or file part to the arrayList. This way it
     * is ready for a ciphering thread to retrieve it so all this threads that could be waiting are notified.
     *      * <p>
     *      *     Being synchronized protects this critical section from multiple threads modifying or retrieving wrong numbers.
     *      * </p>
     * @param fileDivided String corresponding to the path of a recently created fragment.
     */
    public synchronized void addFileReadyToCipher(String fileDivided){
        fileReadyToCipherList.add(fileDivided);
        notifyAll();
    }
}
