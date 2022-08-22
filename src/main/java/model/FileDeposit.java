package model;

import java.util.ArrayList;

public class FileDeposit {

    int totalNumberOfParts;
    int numberOfFilesCipher;
    int numberOfPartsDivided;
    ArrayList<String> fileReadyToCipherList;
    ArrayList<String> partReadyToDecipherList;

    public FileDeposit(int partsToBeDivided, ArrayList<String> partReadyToDecipherList) {

        totalNumberOfParts = partsToBeDivided;
        numberOfFilesCipher = 0;
        numberOfPartsDivided = 0;
        this.fileReadyToCipherList = new ArrayList<>();
        this.partReadyToDecipherList = partReadyToDecipherList;
    }

    public synchronized int getPartToDivide(){
        int partToDivide = numberOfPartsDivided;
        numberOfPartsDivided++;
        return partToDivide;
    }

    public synchronized int viewNumberOfPartsDivided(){
        return numberOfPartsDivided;
    }

    public synchronized int viewTotalNumberOfParts(){
        return totalNumberOfParts;
    }

    public synchronized int viewTotalNumberOfFileCipher(){
        return numberOfFilesCipher;
    }

    public synchronized String getFileReadyToCipher(){

        while(fileReadyToCipherList.size() == 0){
            try {
                if(numberOfFilesCipher == totalNumberOfParts){ return null;}
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        numberOfFilesCipher++;

        String fileReadyToCipher = fileReadyToCipherList.get(0);
        fileReadyToCipherList.remove(0);
        return fileReadyToCipher;
    }

    public synchronized String getPartReadyToDecipher(){
        String filePartReadyToDecipher = partReadyToDecipherList.get(0);
        partReadyToDecipherList.remove(0);
        return filePartReadyToDecipher;
    }

    public synchronized void addFileReadyToCipher(String fileDivided){
        fileReadyToCipherList.add(fileDivided);
        notifyAll();
    }
}
