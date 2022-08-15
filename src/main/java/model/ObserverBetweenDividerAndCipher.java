package model;

import java.io.File;

public class ObserverBetweenDividerAndCipher implements Runnable{

    FileDivider fileDividerForConsultation;
    FileCipher fileCipherForConsultation;
    int numberOfPartsToProcess;

    public ObserverBetweenDividerAndCipher(int numberOfPartsToProcess) {
        fileDividerForConsultation = new FileDivider();
        fileCipherForConsultation = new FileCipher(numberOfPartsToProcess);
        this.numberOfPartsToProcess = numberOfPartsToProcess;
    }

    @Override
    public void run() {

        System.out.println("Observer is going to continue adding files"); /////////////////////////////////////DELETE WHEN FINISHED

        int filesProcessed = 0;

        while(filesProcessed <= numberOfPartsToProcess-1){
            File fileReadyForCipher = fileDividerForConsultation.getReadyFile();
            if(fileReadyForCipher != null) {
                fileCipherForConsultation.addFileToCipher(fileReadyForCipher);
                filesProcessed++;
                System.out.println("File added by thread manager to filecipher" + fileReadyForCipher.getName()); ////////////////////////////////DELETE WHEN FINISHED
            }
        }

        if(fileCipherForConsultation.getNumberOfPartsLeft() == 0){
            System.out.println("Cipher completed succesfully");
        }

    }
}
