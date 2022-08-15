package model;

import java.io.File;
import java.util.ArrayList;

public class FileDecipher implements Runnable{

    //Perhaps an id attribute is not bad idea
    //And a directory attribute
    //An array based on file size to keep track of the files that have been ciphered

    //Don't forget the builder
    private static int numberOfThreads = 0;
    private int id;
    private String[] pathsOfFilesToDecipher;
    private ArrayList<Byte> decipherByteList;

    public FileDecipher(String[] pathsOfFilesToDecipher) {

        id = numberOfThreads;
        numberOfThreads++;
        this.pathsOfFilesToDecipher = pathsOfFilesToDecipher;
        decipherByteList = new ArrayList<>();

        System.out.println("FileDecipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED

    }


    @Override
    public void run() {

    }

    public byte[] getDecipherByteArray() {

        byte[] decipherByteArray = new byte[decipherByteList.size()];
        for (int i = 0; i < decipherByteArray.length; i++) {
            decipherByteArray[i] = decipherByteList.get(i);
        }

        return decipherByteArray;
    }

    //Method to extract key from files

    //Method to get bytearray from files

    //Method to decypher byte array from files and store them in byteArray in order

    //Rebuild original file byte array from matrix and store it as deciphered file.



}
