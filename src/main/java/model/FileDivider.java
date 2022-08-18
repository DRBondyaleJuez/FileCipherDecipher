package model;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileDivider implements Runnable{

    private static int numberOfThreads = 0;
    private int id;
    private byte[] file;
    private String fileName;
    private static String saveDirectory;
    private static FileDeposit fileDeposit;

    public FileDivider(byte[] file,String fileName,String saveDirectory,FileDeposit currentFileDeposit) {

        id = numberOfThreads;
        numberOfThreads++;
        this.file = file;
        this.fileName = fileName;
        FileDivider.saveDirectory = saveDirectory;
        if(fileDeposit == null){
            FileDivider.fileDeposit = currentFileDeposit;
        }
        System.out.println("FileDivider with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED

    }

    public FileDivider() {
    }


    @Override
    public void run() {

        int sizeOfEachPart = 5*1024*1024;
        if(file.length < sizeOfEachPart){
            sizeOfEachPart = file.length/2;
        }
        while(fileDeposit.viewNumberOfPartsDivided() < fileDeposit.viewTotalNumberOfParts()){

            //Get Part
            int partNumber = fileDeposit.getPartToDivide();
            System.out.println("I am thread "+id+" and I have gotten part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
            if(partNumber > fileDeposit.viewTotalNumberOfParts()){return;}

            //Calculate bytes based on Part
            int startPosInFile = partNumber * sizeOfEachPart;
            int sizeOfThisPart = sizeOfEachPart;
            if(file.length-startPosInFile < sizeOfEachPart){
                sizeOfThisPart = file.length-startPosInFile;
            }

            byte[] partByteArray = new byte[sizeOfThisPart];
            for (int i = 0; i < sizeOfThisPart; i++) {
                partByteArray[i] = file[startPosInFile+i];
            }

            //Store
            storeFileDivision(partNumber,partByteArray);
            System.out.println("Thread of file divider "+id+" finished storing the file part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
        }

    }

    //Method to divide and store file division
    private void  storeFileDivision(int partNumber,byte[] partByteArrayToStore){

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveDirectory + "\\" + fileName + "_part" + partNumber + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store divided bytes: " + fileName +"_"+partNumber);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store divided bytes: " + fileName+"_"+partNumber);
        } else {
            try {
                fos.write(partByteArrayToStore);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of divided file: " + fileName+"_"+partNumber);
            }
        }

        fileDeposit.addFileReadyToCipher(saveDirectory + "\\" + fileName + "_part" + partNumber + ".txt");
    }

}
