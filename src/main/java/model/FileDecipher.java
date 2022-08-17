package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private  static byte[] decipherKey;
    private static FileJoiner fileJoiner;

    public FileDecipher(String[] pathsOfFilesToDecipher,String keyFileString, FileJoiner fileJoiner) {

        if(numberOfThreads == 0){
            FileDecipher.fileJoiner = fileJoiner;
            try {
                decipherKey = Files.readAllBytes(Path.of(keyFileString));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Was not able to convert key file to byte array");
            }
        }

        id = numberOfThreads;
        numberOfThreads++;
        this.pathsOfFilesToDecipher = pathsOfFilesToDecipher;
        decipherByteList = new ArrayList<>();


        System.out.println("FileDecipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED

    }


    @Override
    public void run() {

    }

    public byte[] getDecipherByteArray(int fileListPosition) {

        byte[] currentByteArrayToDecipher = null;

        try {
            currentByteArrayToDecipher = Files.readAllBytes(Path.of(pathsOfFilesToDecipher[fileListPosition]));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert key file to byte array");
        }
        return currentByteArrayToDecipher;
    }

    public byte[] unXORArray(byte[] byteArrayToUnXOR){
        byte[] unXoredArray = new byte[byteArrayToUnXOR.length];

        for (int i = 0; i < byteArrayToUnXOR.length; i++) {
            unXoredArray[i] = (byte) (byteArrayToUnXOR[i] ^ decipherKey[i%256]);
        }
        return unXoredArray;
    }

    public void addDecipherByte(Byte byteToAdd){
        decipherByteList.add(byteToAdd);
    }




}
