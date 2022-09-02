package utils.ciphering;

import model.FileCipherDeposit;

import java.io.*;

public class FileDivider implements Runnable{

    private final int id;
    private final byte[] file;
    private final String fileName;
    private final String saveDirectory;
    private final FileCipherDeposit fileCipherDeposit;

    public FileDivider(byte[] file, String fileName, String saveDirectory, FileCipherDeposit currentFileCipherDeposit, int threadId) {
        id = threadId;
        this.file = file;
        this.fileName = fileName;
        this.saveDirectory = saveDirectory;
        this.fileCipherDeposit = currentFileCipherDeposit;
        System.out.println("FileDivider with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }


    @Override
    public void run() {

        int sizeOfEachPart = 5*1024*1024;
        if(file.length < sizeOfEachPart){
            sizeOfEachPart = file.length/2;
        }
        while(fileCipherDeposit.viewNumberOfPartsDivided() < fileCipherDeposit.viewTotalNumberOfParts()){

            //Get Part
            int partNumber = fileCipherDeposit.getPartToDivide();
            System.out.println("I am thread "+id+" and I have gotten part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
            if(partNumber > fileCipherDeposit.viewTotalNumberOfParts()){return;}

            //Calculate bytes based on Part
            int startPosInFile = partNumber * sizeOfEachPart;
            int sizeOfThisPart = Math.min(file.length - startPosInFile, sizeOfEachPart);

            byte[] partByteArray = new byte[sizeOfThisPart];
            System.arraycopy(file, startPosInFile, partByteArray, 0, sizeOfThisPart);

            //Store
            storeFileDivision(partNumber,partByteArray);
            System.out.println("Thread of file divider "+id+" finished storing the file part "+partNumber); ///////////////////////////////DELETE WHEN FINISHED
        }
    }

    //Method to divide and store file division
    private void  storeFileDivision(int partNumber,byte[] partByteArrayToStore){

        StringBuilder partNumberZeros = new StringBuilder();
        int numberOfZeros = 8 - String.valueOf(partNumber).length();
        partNumberZeros.append("0".repeat(Math.max(0, numberOfZeros)));

        FileOutputStream fos = null;
        String dividedFilePartPath = saveDirectory + "\\" + fileName + "_part" + partNumberZeros + partNumber + ".txt";
        try {
            fos = new FileOutputStream(dividedFilePartPath); ///// NOT ABLE TO SOLVE WARNING
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find a file to store divided bytes: " + fileName +"_"+partNumber);
        }
        if(fos == null) {
            System.out.println("Unable to use file to store divided bytes: " + fileName+"_"+partNumber);
        } else {
            try {
                fos.write(partByteArrayToStore);
                fileCipherDeposit.addFileReadyToCipher(dividedFilePartPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to write bytes of divided file: " + fileName+"_"+partNumber);
            }
        }
    }
}
