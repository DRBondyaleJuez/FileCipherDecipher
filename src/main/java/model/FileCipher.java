package model;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FileCipher implements Runnable{

    private int id;
    private static String saveCipherDirectory;
    private static FileDeposit fileDeposit;
    private static byte[] key;


    public FileCipher(String saveCipherDirectory,FileDeposit currentFileDeposit,int threadId) {

        id = threadId;

        //Setting of two attributes which must not be restarted by other thread and only started by the first who reaches this point
        if(threadId == 0) {
            FileCipher.saveCipherDirectory = saveCipherDirectory;
            FileCipher.fileDeposit = currentFileDeposit;

            //Generating key
            FileCipher.key = new byte[256];
            Random randomGenerator = new Random();
            randomGenerator.nextBytes(key);
            storeKey();
        }
        System.out.println("FileCipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }


    @Override
    public void run() {

        while(fileDeposit.viewTotalNumberOfFileCipher() < fileDeposit.viewTotalNumberOfParts()) {
            //Generate key
            byte[] currentKey = key;

            //GetFile content in byte array format
            File fileToProcess = getNextFile();
            if(fileToProcess == null){break;} //This has to do with threads that are waiting in the getNextFile method when there is no file Not sure how to finish those
            System.out.println("I am thread " +id+". File to cipher obtained"+fileToProcess.getName()); ///////////////////////////////////////////////DELETE WHEN FINISHED

            String fileName = fileToProcess.getName().replace(".txt",".cipher");
            byte[] byteContentFileToProcess = getByteArrayFromDividedFile(fileToProcess);
            System.out.println("I am thread " +id+". Byte array from file to cipher obtained "+fileToProcess.getName()); ////////////////////////////////////// DELETE WHEN FINISHED


            //XORting
            byte[] xortedByteArray = xorCipherByteArray(currentKey,byteContentFileToProcess);
            System.out.println("I am thread " +id+". File byte Array XORted "+fileToProcess.getName());

            //Storing
            storeCipherFilePart(xortedByteArray,fileName);
            System.out.println("I am thread " +id+". File byte Array stored "+fileToProcess.getName());
        }

    }

    private  File getNextFile(){

        String fileToCipherPath = fileDeposit.getFileReadyToCipher();
        if(fileToCipherPath == null){return null;}

        File fileToCipher = new File(fileToCipherPath);
        return fileToCipher;
    }

    private byte[] getByteArrayFromDividedFile(File processedFile){

        byte[] partFileByteArray = null;

        try {
            partFileByteArray = Files.readAllBytes(processedFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert part file to byte array");
        }

        return partFileByteArray;
    }

    private byte[] xorCipherByteArray(byte[] key, byte[] fileContent){

        byte[] xoredArray = new byte[fileContent.length];

        for (int i = 0; i < fileContent.length; i++) {
            xoredArray[i] = (byte) (fileContent[i] ^ key[i%256]);
        }

        return xoredArray;
    }


    private void storeCipherFilePart(byte[] cipherBytes, String fileName){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveCipherDirectory + "\\" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store cipher bytes: " + fileName);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store cipher bytes: " + fileName);
        } else {
            try {
                fos.write(cipherBytes);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of cipher file: " + fileName);
            }
        }
    }

    private void storeKey(){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveCipherDirectory + "\\KEY.cipher");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store key bytes");
        }
        if(fos == null) {
            System.out.println("Unable to use file to store key bytes");
        } else {
            try {
                fos.write(key);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes y key file");
            }
        }
    }
}
