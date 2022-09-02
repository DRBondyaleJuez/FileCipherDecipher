package utils.deciphering;

import model.FileDecipherDeposit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileDecipher implements Runnable{

    private int id;
    private final String[] pathsOfFilesToDecipher;
    private byte[] decipherKey;
    private final FileDecipherDeposit fileDecipherDeposit;

    public FileDecipher(String[] pathsOfFilesToDecipher, String keyFileString, FileDecipherDeposit fileDecipherDeposit, int threadId) {

            this.fileDecipherDeposit = fileDecipherDeposit;
            try {
                decipherKey = Files.readAllBytes(Path.of(keyFileString));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Was not able to convert key file to byte array");
            }

        id = threadId;
        this.pathsOfFilesToDecipher = pathsOfFilesToDecipher;

        System.out.println("FileDecipher with id number " + id + " was constructed"); ///////////////////////////////DELETE WHEN FINISHED
    }

    @Override
    public void run() {

        while(fileDecipherDeposit.viewNumberOfPartsDeciphered() < fileDecipherDeposit.viewTotalNumberOfParts()){
            int part = fileDecipherDeposit.getPartToDecipher();
            if(part > fileDecipherDeposit.viewTotalNumberOfParts()){break;}
            byte[] byteArrayReadyForDecipher = getDecipherByteArray(part);
            byte[] byteArrayReadyForFiling = unXORArray(byteArrayReadyForDecipher);

            ArrayList<Byte> byteListReadyForFiling = new ArrayList<>();
            for (byte b : byteArrayReadyForFiling) {
                byteListReadyForFiling.add(b);
            }

            fileDecipherDeposit.addBytesToFile(part,byteListReadyForFiling);

            System.out.println("I am thread " + id + ". Part " + part + "unXORted added");

        }
    }

    //Convert file that needs deciphering to its byte array form
    public byte[] getDecipherByteArray(int fileListPosition) {

        byte[] currentByteArrayToDecipher = null;

        try {
            currentByteArrayToDecipher = Files.readAllBytes(Path.of(pathsOfFilesToDecipher[fileListPosition]));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert key file to byte array");
        }
        return currentByteArrayToDecipher;
    }

    //Undo the xor ciphering of the byte array of the file
    public byte[] unXORArray(byte[] byteArrayToUnXOR){
        byte[] unXoredArray = new byte[byteArrayToUnXOR.length];

        for (int i = 0; i < byteArrayToUnXOR.length; i++) {
            unXoredArray[i] = (byte) (byteArrayToUnXOR[i] ^ decipherKey[i%256]);
        }
        return unXoredArray;
    }
}
