package model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileJoiner {

    private ArrayList<Byte>[] decipherByteArrayClumps;
    private int numberOfByteArrayClumps;
    private int numberOfCompletedArrayClumps;
    private String decipherFileStorage;

    public FileJoiner( int numberOfByteArrayClumps, String decipherFileStorage) {
        this.decipherByteArrayClumps = new ArrayList[numberOfByteArrayClumps];
        this.numberOfByteArrayClumps = numberOfByteArrayClumps;
        numberOfCompletedArrayClumps = 0;
        this.decipherFileStorage = decipherFileStorage;
    }

    public synchronized void add(ArrayList<Byte> decipherByteArray,int threadId){
        decipherByteArrayClumps[threadId] = decipherByteArray;
        numberOfCompletedArrayClumps++;
    }

    public void decipherByteJoiner(){

        int lengthOfFinalByteArray = 0;
        for (int i = 0; i < numberOfByteArrayClumps; i++) {
            lengthOfFinalByteArray = lengthOfFinalByteArray + decipherByteArrayClumps[i].size();
        }

        byte[] finalJoinedByteArray = new byte[lengthOfFinalByteArray];

        int positionInFinalArray = 0;
        for (int clump = 0; clump < decipherByteArrayClumps.length; clump++) {

            for (int i = 0; i < decipherByteArrayClumps[clump].size(); i++) {
                finalJoinedByteArray[positionInFinalArray] = decipherByteArrayClumps[clump].get(i);
                positionInFinalArray++;
            }
        }

        storeDecipherFile(finalJoinedByteArray);
    }

    public void storeDecipherFile(byte[] finalByteArray){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(decipherFileStorage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store cipher bytes: " + decipherFileStorage);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store cipher bytes: " + decipherFileStorage);
        } else {
            try {
                fos.write(finalByteArray);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of cipher file: " + decipherFileStorage);
            }
        }
    }
}
