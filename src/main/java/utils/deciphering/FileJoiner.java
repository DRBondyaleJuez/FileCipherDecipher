package utils.deciphering;

import model.FileDecipherDeposit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileJoiner implements Runnable {
    private final String decipherFileStorage;
    private final FileDecipherDeposit fileDecipherDeposit;

    public FileJoiner(FileDecipherDeposit fileDecipherDeposit, String decipherFileStorage) {
        this.fileDecipherDeposit = fileDecipherDeposit;
        this.decipherFileStorage = decipherFileStorage;
    }

    @Override
    public void run() {

        for (int i = 0; i < fileDecipherDeposit.viewTotalNumberOfParts(); i++) {
            byte[] byteArrayForFiling = fileDecipherDeposit.getBytesReadyToFile(i);

            storeDecipherFile(byteArrayForFiling);
            System.out.println("Deciphered part " + i + " stored in file " + decipherFileStorage); ///////////////////////DELETE WHEN FINISHED
        }

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
