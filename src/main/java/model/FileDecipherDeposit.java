package model;

import java.util.ArrayList;

public class FileDecipherDeposit {

    private final int totalNumberOfParts;
    private int numberOfPartsDeciphered;
    private final ArrayList<Byte>[] bytesReadyToFileArray;

    public FileDecipherDeposit(int partsToBeDeciphered) {
        totalNumberOfParts = partsToBeDeciphered;
        numberOfPartsDeciphered = 0;
        bytesReadyToFileArray = new ArrayList[partsToBeDeciphered]; /////////////////////////DON'T KNOW HOW TO FIX THIS WARNING
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

        while(bytesReadyToFileArray[part].isEmpty()){
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
        bytesReadyToFileArray[part].clear();

        return bytesToFile;
    }

    public synchronized void addBytesToFile(int part,ArrayList<Byte> listOfDecipheredBytes){
        bytesReadyToFileArray[part] = listOfDecipheredBytes;
        notifyAll();
    }
}
