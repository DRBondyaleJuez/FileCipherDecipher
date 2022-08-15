package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FileCipher implements Runnable{


    private static int numberOfThreads = 0;
    private int id;
    private String dividedFileDirectory;
    private static String saveCipherDirectory;
    private static ArrayList<File> filesToBeCipher;
    private static int numberOfPartsLeft = -1;
    //private static byte[] key;


    public FileCipher(String dividedFileDirectory, String saveCipherDirectory,int numberOfParts) {

        id = numberOfThreads;
        numberOfThreads++;
        this.dividedFileDirectory = dividedFileDirectory;
        FileCipher.saveCipherDirectory = saveCipherDirectory;
        //Setting of two attributes which must no be restarted by other thread and only started by the first who reaches this point
        firstSettingNumberPartAndFileList(numberOfParts);

        System.out.println("fileCipher " + id +" has been constructed. Numberof Parts left to cipher: " + numberOfParts );////////////////////////////////////////////////////////////////////////////DELETE WHEN FINISHED


        //Generating key
        //MAYBE YOU WANTED A KEY FOR EACH THREAD BUT THEN IT WOULD BE HARD TO KNOW WHICH KEY WAS USED FOR EACH FILE UNLESS IT IS STORED IN THE FILE BUT THEN WHY NOT A KEY FOR EACH FILE
        /*
        FileCipher.key = new byte[256];
        Random randomGenerator = new Random();
        randomGenerator.nextBytes(key);
         */
    }

    public synchronized void firstSettingNumberPartAndFileList(int numberOfParts){
        if(numberOfPartsLeft == -1){
            numberOfPartsLeft = numberOfParts;
            FileCipher.filesToBeCipher = new ArrayList<>();
        }
    }

    public FileCipher(int numberOfParts){
        firstSettingNumberPartAndFileList(numberOfParts);

    }

    @Override
    public void run() {

        while(numberOfPartsLeft>=0) {
            //Generate key
            byte[] currentKey = generateKey();

            //GetFile content in byte array format
            File fileToProcess = getNextFile();
            if(fileToProcess == null){break;} //This has to do with threads that are waiting in the getNextFile method when there is no file Not sure how to finish those
            System.out.println(id+"File to cipher obtained"+fileToProcess.getName()); ///////////////////////////////////////////////DELETE WHEN FINISHED

            String fileName = fileToProcess.getName().replace(".txt",".cipher");
            byte[] byteContentFileToProcess = getByteArrayFromDividedFile(fileToProcess.getPath());
            System.out.println(id+"Byte array from file to cipher obtained"+fileToProcess.getName()); ////////////////////////////////////// DELETE WHEN FINISHED


            //XORting
            byte[] xortedByteArray = xorCipherByteArray(currentKey,byteContentFileToProcess);
            System.out.println(id+"File byte Array WORted"+fileToProcess.getName());

            //Storing
            storeCipherFilePart(xortedByteArray,currentKey,fileName);
            System.out.println(id+"File byte Array stored"+fileToProcess.getName());
        }

    }

    private byte[] generateKey(){
        byte[] keyByteArray = new byte[256];
        Random randomGenerator = new Random();
        randomGenerator.nextBytes(keyByteArray);
        return keyByteArray;
    }

    private byte[] getByteArrayFromDividedFile(String filePath){

        File fileToCipher = new File(filePath);
        Scanner scan = null;
        try {
            scan = new Scanner(fileToCipher);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to read the file divided in order to cipher.");
        }

        ArrayList<Byte> byteListToCipher = new ArrayList<>();

        if(scan != null) {
            Byte currentByte = null;
            while (scan.hasNextLine()){
                currentByte = Byte.valueOf(scan.nextLine());
                byteListToCipher.add(currentByte);
            }
        } else{
            System.out.println("scanner is still null did not get file information/content");
        }

        byte[] byteArray = new byte[byteListToCipher.size()];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = byteListToCipher.get(i);
        }

        return byteArray;

    }

    private byte[] xorCipherByteArray(byte[] key, byte[] fileContent){

        byte[] xoredArray = new byte[fileContent.length];

        int i=0;
        while(i < fileContent.length){
            xoredArray[i] = (byte) (fileContent[i] ^ key[i%256]);
        }

        return xoredArray;

    }


    private void storeCipherFilePart(byte[] cipherBytes,byte[] keyBytes, String fileName){

        File storedFileCipher = new File(saveCipherDirectory + "\\" + fileName + ".cipher");
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(storedFileCipher);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to open file writer of the file cipher: " + fileName.toString());
        }

        if(pw != null) {
            for (int i = 0; i < cipherBytes.length; i++) {
                pw.println(cipherBytes[i]);
            }
            pw.println("0_0_0_0_0");
            for(int i = 0; i < keyBytes.length; i++){
                pw.println(cipherBytes[i]);
            }
            pw.close();
        } else {
            System.out.println("Unable write the file cipher: " + fileName.toString());
        }
        notifyAll();
    }

    public synchronized void addFileToCipher(File file){
        filesToBeCipher.add(file);
        System.out.println("A file has been added to list to be cipher: " + file.getName()); ///////////////////////////////////////////////////////////////DELETE WHEN FINISHED
        notifyAll();
    }

    private synchronized  File getNextFile(){
        while(filesToBeCipher.size() == 0){
            if(numberOfPartsLeft == 0){break;}
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(numberOfPartsLeft > 0) {

            numberOfPartsLeft--;

            File fileOfTheListToCipher = filesToBeCipher.get(0);
            System.out.println("A file has been removed:" + fileOfTheListToCipher.getName()); //////////////////////////////////////////////////// DELETE WHEN FINISHED
            filesToBeCipher.remove(0);
            return fileOfTheListToCipher;
        } else {
            return null;
        }
    }

    public int getNumberOfPartsLeft(){
        return numberOfPartsLeft;
    }


    //Method to create key


    //Method to get bytes in divided file and encrypt it with key using XOR

    //Method to save a file in a format .cipher when given a byte array



}
