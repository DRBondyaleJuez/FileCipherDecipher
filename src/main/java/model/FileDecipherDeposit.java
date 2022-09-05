package model;

import java.util.ArrayList;

public class FileDecipherDeposit {

    private final int totalNumberOfParts;
    private int numberOfPartsDeciphered;
    private final ArrayList<Byte>[] bytesReadyToFileArray;

    private int numberOfPartsAddedToFile;

    public FileDecipherDeposit(int partsToBeDeciphered) {
        totalNumberOfParts = partsToBeDeciphered;
        numberOfPartsDeciphered = 0;
        bytesReadyToFileArray = new ArrayList[partsToBeDeciphered]; /////////////////////////DON'T KNOW HOW TO FIX THIS WARNING
        numberOfPartsAddedToFile = 0;
    }

    public synchronized int getPartToDecipher(){
        int partToDivide = numberOfPartsDeciphered;
        numberOfPartsDeciphered++;
        return partToDivide;
    }

    public synchronized int viewNumberOfPartsDeciphered(){
        return numberOfPartsDeciphered;
    }

    public synchronized int viewTotalNumberOfParts(){
        return totalNumberOfParts;
    }

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

    public synchronized void addBytesToFile(int part,ArrayList<Byte> listOfDecipheredBytes){

        //To try to avoid out of memory error we buffered slightly the addition of byte arrays by the threads here
        while(part > numberOfPartsAddedToFile + 10){
            try {
                System.out.println("I am a deciphering thread trying to add part " + part + " to decipher deposit. But there have been only " + numberOfPartsAddedToFile +
                        " parts added to the file. To avoid out of memory error I will wait 2 seconds");
                wait(2000);
            } catch (InterruptedException e) {
                System.out.println("The deciphering of part " +part+" was interrupted.");
                throw new RuntimeException(e);
            }
        }

        bytesReadyToFileArray[part] = listOfDecipheredBytes;
        notifyAll();
    }
}
