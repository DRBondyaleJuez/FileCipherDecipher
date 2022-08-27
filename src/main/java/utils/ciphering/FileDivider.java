package utils.ciphering;

import model.FileDeposit;

import java.io.*;

public class FileDivider implements Runnable{

    private final int id;
    private final byte[] file;
    private final String fileName;
    private static String saveDirectory;
    private static FileDeposit fileDeposit;

    public FileDivider(byte[] file,String fileName,String saveDirectory,FileDeposit currentFileDeposit,int threadId) {

        id = threadId;
        this.file = file;
        this.fileName = fileName;
        FileDivider.saveDirectory = saveDirectory;
        if(threadId == 0){
            FileDivider.fileDeposit = currentFileDeposit;
        }
        System.out.println("FileDivider with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED

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

        String partNumberZeros = "";
        if(partNumber < 100){ partNumberZeros = partNumberZeros + 0;}
        if(partNumber < 10){ partNumberZeros = partNumberZeros + 0;}


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveDirectory + "\\" + fileName + "_part" + partNumberZeros + partNumber + ".txt"); ///// NOT ABLE TO SOLVE WARNING
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

        fileDeposit.addFileReadyToCipher(saveDirectory + "\\" + fileName + "_part" + partNumberZeros + partNumber + ".txt");
    }

}
