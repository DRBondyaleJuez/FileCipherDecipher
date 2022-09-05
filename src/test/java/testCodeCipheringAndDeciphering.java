import controller.Controller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public class testCodeCipheringAndDeciphering {

    //Testing if divided, ciphered and deciphered file below 10kB is the same
    @Test
    public void testDivideSmallFile1(){
        String fileName = "lessThan10KbFile.txt";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredSmallFile1(){
        String fileName = "lessThan10KbFile.txt";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }


    //Testing if divided, ciphered and deciphered file above 10kB below 5Mb is the same
    @Test
    public void testDivideMediumFile1(){
        String fileName = "between10KbAnd5MbFile.jpg";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }

    @Test
    public void testDecipheredMediumFile1(){

        String fileName = "between10KbAnd5MbFile.jpg";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file above 5Mb below 20Mb is the same
    @Test
    public void testDivideMediumLargeFile1(){

        String fileName = "between5MbAnd20MbFile.avi";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredMediumLargeFile1(){

        String fileName = "between5MbAnd20MbFile.avi";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 50Mb is the same
    @Test
    public void testDivideLargeFile1(){

        String fileName = "around50MbFile.avi";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }

    @Test
    public void testDecipheredLargeFile1(){

        String fileName = "around50MbFile.avi";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 100Mb is the same
    @Test
    public void testDivideLargerFile1(){

        String fileName = "around100MbFile.avi";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredLargerFile1(){

        String fileName = "around100MbFile.avi";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 200Mb is the same
    @Test
    public void testDivideSuperLargeFile1(){

        String fileName = "around200MbFile.avi";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredSuperLargeFile1(){

        String fileName = "around200MbFile.avi";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 500Mb is the same
    @Test
    public void testDivideHugeFile1(){

        String fileName = "around500MbFile.mkv";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredHugeFile1(){

        String fileName = "around500MbFile.mkv";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 1Gb is the same
    //TODO: Files.readAllBytes is not able to work without Out of memory error with very big files. Even when it could with files closer to 900Mb it takes a super long time
    @Test
    public void testDivideSuperHugeFile1(){

        String fileName = "around1GbFile.zip";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredSuperHugeFile1(){

        String fileName = "around1GbFile.zip";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    private boolean testDividingFile(String fileName){
        //Initializing Controller for Test
        Controller controllerForTests = new Controller();

        //Getting original file and its byte array
        String originalFileTestedPath = "WorkingFolder\\testingFiles\\" + fileName;
        File originalFileTested = new File(originalFileTestedPath);
        byte[] originalFileByteArray;
        try {
            originalFileByteArray = Files.readAllBytes(originalFileTested.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        //Dividing and ciphering
        controllerForTests.cipherFile(originalFileTested);

        byte[] hundredFirstAndHundredLastBytesOriginalFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesOriginalFile[i] = originalFileByteArray[i];
            hundredFirstAndHundredLastBytesOriginalFile[100+i] = originalFileByteArray[originalFileByteArray.length - (100-i)];
        }

        String[] fileNameAndFormat = fileName.split("\\.");
        String fileFormat = fileNameAndFormat[fileNameAndFormat.length-1];

        File dividedFilesDir= new File("WorkingFolder\\defaultFolder\\fileDivisions\\" + fileName.replace("."+fileFormat,"_"+fileFormat));
        File dividedFilePart0 = Objects.requireNonNull(dividedFilesDir.listFiles())[0];
        byte[] part0ByteArray;
        try {
            part0ByteArray= Files.readAllBytes(dividedFilePart0.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return false;
        }
        File dividedFileLastPart = Objects.requireNonNull(dividedFilesDir.listFiles())[Objects.requireNonNull(dividedFilesDir.list()).length-1];
        byte[] lastPartByteArray;
        try {
            lastPartByteArray= Files.readAllBytes(dividedFileLastPart.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        byte[] hundredFirstAndHundredLastBytesDividedFiles = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesDividedFiles[i] = part0ByteArray[i];
            hundredFirstAndHundredLastBytesDividedFiles[100+i] = lastPartByteArray[lastPartByteArray.length - (100-i)];
        }

        return Arrays.equals(hundredFirstAndHundredLastBytesOriginalFile,hundredFirstAndHundredLastBytesDividedFiles);
    }

    private boolean testDecipheringFile(String fileName){
        //Initializing Controller for Test
        Controller controllerForTests = new Controller();

        //Getting original file and its byte array
        String originalFileTestedPath = "WorkingFolder\\testingFiles\\"+fileName;
        File originalFileTested = new File(originalFileTestedPath);
        byte[] originalFileByteArray;
        try {
            originalFileByteArray = Files.readAllBytes(originalFileTested.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        //Dividing and ciphering
        controllerForTests.cipherFile(originalFileTested);

        byte[] hundredFirstAndHundredLastBytesOriginalFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesOriginalFile[i] = originalFileByteArray[i];
            hundredFirstAndHundredLastBytesOriginalFile[100+i] = originalFileByteArray[originalFileByteArray.length - (100-i)];
        }

        //Decipher
        String[] fileNameAndFormat = fileName.split("\\.");
        String fileFormat = fileNameAndFormat[fileNameAndFormat.length-1];

        File cipherFilesDir= new File("WorkingFolder\\defaultFolder\\fileCipher\\" + fileName.replace("."+fileFormat,"_"+fileFormat));
        controllerForTests.decipherFiles(cipherFilesDir);

        File decipheredFile= new File("WorkingFolder\\defaultFolder\\fileDecipher\\" + fileName.replace("."+fileFormat," (DECIPHER)."+fileFormat));

        byte[] decipherFileByteArray;
        try {
            decipherFileByteArray= Files.readAllBytes(decipheredFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        byte[] hundredFirstAndHundredLastBytesDecipherFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesDecipherFile[i] = decipherFileByteArray[i];
            hundredFirstAndHundredLastBytesDecipherFile[100+i] = decipherFileByteArray[decipherFileByteArray.length - (100-i)];
        }

        return Arrays.equals(hundredFirstAndHundredLastBytesOriginalFile,hundredFirstAndHundredLastBytesDecipherFile);
    }

}
