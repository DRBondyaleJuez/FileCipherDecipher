package utils.deciphering;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileJoiner {

    private final ArrayList<Byte>[] decipherByteArrayClumps;
    private final int numberOfByteArrayClumps;
    private final String decipherFileStorage;

    public FileJoiner( int numberOfByteArrayClumps, String decipherFileStorage) {
        this.decipherByteArrayClumps = new ArrayList<>[numberOfByteArrayClumps]; ///// UNABLE TO FIX THIS WARNING PERHAPS IT IS BAD TO COMBINE Array and LISTS
        this.numberOfByteArrayClumps = numberOfByteArrayClumps;
        this.decipherFileStorage = decipherFileStorage;
    }

    public synchronized void add(ArrayList<Byte> decipherByteArray,int threadId){
        decipherByteArrayClumps[threadId] = decipherByteArray;
        System.out.println("I am thread "+ threadId + " I have stored my bytes in the array position. The size of this byte array was: " + decipherByteArray.size()); ////////////////////////DELETE WHEN FINSIHED
        ////// ^DELETE WHEN FINISHED
    }

    public void decipherByteJoiner(){

        int lengthOfFinalByteArray = 0;
        for (int i = 0; i < numberOfByteArrayClumps; i++) {
            lengthOfFinalByteArray = lengthOfFinalByteArray + decipherByteArrayClumps[i].size();
            System.out.println("Final joining about to start. Size of the third portion number " + i + ": " + decipherByteArrayClumps[i].size());
        }

        byte[] finalJoinedByteArray = new byte[lengthOfFinalByteArray];

        int positionInFinalArray = 0;
        for (int clump = 0; clump < decipherByteArrayClumps.length; clump++) {

            for (int i = 0; i < decipherByteArrayClumps[clump].size(); i++) {
                finalJoinedByteArray[positionInFinalArray] = decipherByteArrayClumps[clump].get(i);
                positionInFinalArray++;
            }
            System.out.println(clump+1 + "third of the file deciphered");///////////////////////////////DELETE WHEN FINISHED
        }

        storeDecipherFile(finalJoinedByteArray);
    }

    public void storeDecipherFile(byte[] finalByteArray){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(decipherFileStorage); /////UNABLE TO FIX THIS WARNING
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
