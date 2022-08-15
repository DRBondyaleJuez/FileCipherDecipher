package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileDivider implements Runnable{

    private static int numberOfThreads = 0;
    private int id;
    private byte[] file;
    private String fileName;
    private static String saveDirectory;
    private static int numberOfPartsLeft;
    private static ArrayList<File> dividedFilesReadyToCipher;

    public FileDivider(byte[] file,String fileName,String saveDirectory,int totalNumberOfParts) {

        id = numberOfThreads;
        numberOfThreads++;
        this.file = file;
        this.fileName = fileName;
        FileDivider.saveDirectory = saveDirectory;
        if(id==0){
            numberOfPartsLeft = totalNumberOfParts;
            FileDivider.dividedFilesReadyToCipher = new ArrayList<>();
        }
        System.out.println("FileDivider with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED

    }

    public FileDivider() {
    }

    //Perhaps id attribute
    //directory attribute
    //The idea is to give each divider a number and depending on that number and they will work on particular
    // sections of the file and multiples of that particular starting section soy they don't step on each other

    //Don't forget the builder

    @Override
    public void run() {

        int sizeOfEachPart = 5*1024*1024;
        if(file.length < sizeOfEachPart){
            sizeOfEachPart = file.length/2;
        }
        while(numberOfPartsLeft>=0){
            int partNumber = getAPart();
            System.out.println("I am thread "+id+" and I have gotten part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
            if(partNumber<0){return;}
            int startPosInFile = partNumber * sizeOfEachPart;
            int sizeOfThisPart = sizeOfEachPart;
            if(file.length-startPosInFile < sizeOfEachPart){
                sizeOfThisPart = file.length-startPosInFile;
            }
            storeFileDivision(partNumber,startPosInFile,sizeOfThisPart);
            System.out.println("Thread of file divider "+id+" finished storing the file part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
        }

    }

    //Method to divide and store file division
    private void  storeFileDivision(int partNumber,int startPos,int partSize){

        File storedFileDivision = new File(saveDirectory + "\\" + fileName + "_part" + partNumber + ".txt");
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(storedFileDivision);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to open file writer the file division " + partNumber);
        }

        if(pw != null) {
            for (int i = 0; i < partSize; i++) {
                pw.println(file[i + startPos]);
            }
            pw.close();
        } else {
            System.out.println("Unable write the file division" + partNumber);
        }
        addFileReadyToCipher(storedFileDivision);
    }

    private synchronized int getAPart(){
        numberOfPartsLeft--;
        int selectedPart = numberOfPartsLeft;
        System.out.println("A part has been selected by thread " +id+". Number of parts left= "+numberOfPartsLeft);///////////////////////////////DELETE WHEN FINISHED
        return selectedPart;
    }

    private synchronized void addFileReadyToCipher(File fileReadyToCipher){
        dividedFilesReadyToCipher.add(fileReadyToCipher);
        System.out.println("A file has finished division: "+fileReadyToCipher.getName()+" . The number of divided files to cipher " + dividedFilesReadyToCipher.size()); /////////////////////////////////DELETE WHEN FINISHED
        notifyAll();
    }

    public synchronized File getReadyFile(){
        System.out.println("A file from the fileDivided is going to be obtained. The current number of files is: "+ dividedFilesReadyToCipher.size() );///////////////////////////////DELETE WHEN FINISHED

        while(dividedFilesReadyToCipher.size() == 0){
            try {
                System.out.println("The number of ready divided files is 0 so I am going to wait"); ///////////////////////////////DELETE WHEN FINISHED
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
        if (dividedFilesReadyToCipher.size() == 0){
            return null;
        }
        */

        File fileReadyForCipher = dividedFilesReadyToCipher.get(0);
        System.out.println("Divided file has been taken to cipher: "+fileReadyForCipher.getName()); /////////////////////////////////DELETE WHEN FINISHED
        dividedFilesReadyToCipher.remove(0);
        return fileReadyForCipher;
    }



}
