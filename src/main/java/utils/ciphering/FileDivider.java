package utils.ciphering;

import model.FileCipherDeposit;

import java.io.*;

/**
 * Provides the runnable objects provided to Threads in charge of dividing files into fragments to facilitate ciphering.
 * <p>
 *     This class implement the interface Runnable.
 * </p>
 * @author Daniel R Bondyale Juez
 * @version 1.0
 */
public class FileDivider implements Runnable{

    private final int id;
    private final byte[] file;
    private final String fileName;
    private final String saveDirectory;
    private final FileCipherDeposit fileCipherDeposit;

    /**
     * This is the constructor of this runnable Class
     * @param file byte[] corresponding to the bytes of the file that needs dividing
     * @param fileName String corresponding to the generic name of the file which will have a serial code added.
     * @param saveDirectory String corresponding to the path of the directory where these divided fragments will be stored.
     * @param currentFileCipherDeposit FileCipherDeposit object containing the information regarding the number of fragments
     *                                 expected, the number that have been stored already and the paths of all these fragments
     *                                 for further after to cipher.
     * @param threadId int corresponding to one of the threads. In this case from 0 to 4 since it operates with 5 threads
     *                 these may indicate which section of the file it is dividing so no threads divide the same section twice.
     */
    public FileDivider(byte[] file, String fileName, String saveDirectory, FileCipherDeposit currentFileCipherDeposit, int threadId) {
        id = threadId;
        this.file = file;
        this.fileName = fileName;
        this.saveDirectory = saveDirectory;
        this.fileCipherDeposit = currentFileCipherDeposit;
        System.out.println("FileDivider with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }


    /**
     * Implementation of the abstract method of the Runnable interface to perform the concurrent division of the file into fragments.
     * <p>
     *     The fragment have been established to have a size of 5Mb if it is between 5 and 10kB the fragments are half the
     *     size of the file and if they are below 10kB they are not fragmented.
     *     The process of dividing consists on an iterative loop where successive the sections of the file are stored separately.
     *     However, the control of how many parts are left to divide is handled by the FileCipherDeposit. This could be a
     *     critical section since multiple threads could attempt to modify or retrieve so the get method of this information
     *     is synchronized.
     * </p>
     */
    @Override
    public void run() {

        int sizeOfEachPart = 5*1024*1024;
        if(file.length < sizeOfEachPart){
            sizeOfEachPart = file.length/2;
        }
        if(file.length/1024 < 10){
            sizeOfEachPart = file.length;
        }
        while(fileCipherDeposit.viewNumberOfPartsDivided() < fileCipherDeposit.viewTotalNumberOfParts()){

            //Get Part
            int partNumber = fileCipherDeposit.getPartToDivide();
            System.out.println("I am thread "+id+" and I have gotten part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
            if(partNumber > fileCipherDeposit.viewTotalNumberOfParts()){return;}

            //Calculate bytes based on Part
            int startPosInFile = partNumber * sizeOfEachPart;
            int sizeOfThisPart = Math.min(file.length - startPosInFile, sizeOfEachPart);
            if(file.length < 5*1024*1024 && partNumber == 1 && file.length%2 > 0){
                sizeOfThisPart = sizeOfThisPart+1;
            }

            byte[] partByteArray = new byte[sizeOfThisPart];
            System.arraycopy(file, startPosInFile, partByteArray, 0, sizeOfThisPart);

            //Store
            storeFileDivision(partNumber,partByteArray);
            System.out.println("Thread of file divider "+id+" finished storing the file part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
        }
    }

    //Method to divide and store file division
    private void storeFileDivision(int partNumber,byte[] partByteArrayToStore){

        StringBuilder partNumberZeros = new StringBuilder();
        int numberOfZeros = 8 - String.valueOf(partNumber).length();
        partNumberZeros.append("0".repeat(Math.max(0, numberOfZeros)));

        FileOutputStream fos = null;
        String dividedFilePartPath = saveDirectory + "\\" + fileName + "_part" + partNumberZeros + partNumber + ".txt";
        try {
            fos = new FileOutputStream(dividedFilePartPath); ///// NOT ABLE TO SOLVE WARNING
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store divided bytes: " + fileName +"_"+partNumber);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store divided bytes: " + fileName+"_"+partNumber);
        } else {
            try {
                fos.write(partByteArrayToStore);
                fileCipherDeposit.addFileReadyToCipher(dividedFilePartPath);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of divided file: " + fileName+"_"+partNumber);
            }
        }
    }
}
