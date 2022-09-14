import controller.Controller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
        long startTime = System.currentTimeMillis();
        boolean result = testDecipheringFile(fileName);
        long endTime = System.currentTimeMillis();
        System.out.println("Time spent: " + (endTime - startTime));
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    //Testing if divided, ciphered and deciphered file around 1Gb is the same
    //TODO: Files.readAllBytes is not able to work without Out of memory error with very big files.
    // Even when it could with files closer to 900Mb it takes a super long time. Use inputStream a byte array of a particular size and a loop.
    // Remember to determine size of file use a long not an int
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

    //Testing if divided, ciphered and deciphered file around 2Gb is the same
    //TODO: Files.readAllBytes is not able to work without Out of memory error with very big files.
    // Even when it could with files closer to 900Mb it takes a super long time. Use inputStream a byte array of a particular size and a loop.
    // Remember to determine size of file use a long not an int
    @Test
    public void testDivideMassiveFile1(){

        String fileName = "around2GbFile.mp4";
        boolean result = testDividingFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and divided parts");
    }
    @Test
    public void testDecipheredMassiveFile1(){

        String fileName = "around2GbFile.mp4";
        boolean result = testDecipheringFile(fileName);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }

    private boolean testDividingFile(String fileName){
        //Initializing Controller for Test
        Controller controllerForTests = new Controller();

        //Getting original file and its byte array
        String originalFileTestedPath = "WorkingFolder\\testingFiles\\" + fileName;
        File originalFileTested = new File(originalFileTestedPath);
        long fileLength = originalFileTested.length();
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(originalFileTested,"r");
        } catch (FileNotFoundException e) {
            System.out.println("Was unable to create random access file");
            return false;
        }
        byte[] originalFileByteArray100First = new byte[100];
        byte[] originalFileByteArray100Last = new byte[100];

        try {
            randomAccessFile.read(originalFileByteArray100First,0,100);
            randomAccessFile.seek(fileLength-100);
            randomAccessFile.read(originalFileByteArray100Last,0,100);
        } catch (IOException e) {
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        byte[] hundredFirstAndHundredLastBytesOriginalFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesOriginalFile[i] = originalFileByteArray100First[i];
            hundredFirstAndHundredLastBytesOriginalFile[100+i] = originalFileByteArray100Last[i];
        }

        //Dividing and ciphering
        controllerForTests.cipherFile(originalFileTested);
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
        long fileLength = originalFileTested.length();
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(originalFileTested,"r");
        } catch (FileNotFoundException e) {
            System.out.println("Was unable to create random access file");
            return false;
        }
        byte[] originalFileByteArray100First = new byte[100];
        byte[] originalFileByteArray100Last = new byte[100];

        try {
            randomAccessFile.read(originalFileByteArray100First,0,100);
            randomAccessFile.seek(fileLength-100);
            randomAccessFile.read(originalFileByteArray100Last,0,100);
        } catch (IOException e) {
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        byte[] hundredFirstAndHundredLastBytesOriginalFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesOriginalFile[i] = originalFileByteArray100First[i];
            hundredFirstAndHundredLastBytesOriginalFile[100+i] = originalFileByteArray100Last[i];
        }

        //Dividing and ciphering
        controllerForTests.cipherFile(originalFileTested);

        //Decipher
        String[] fileNameAndFormat = fileName.split("\\.");
        String fileFormat = fileNameAndFormat[fileNameAndFormat.length-1];

        File cipherFilesDir= new File("WorkingFolder\\defaultFolder\\fileCipher\\" + fileName.replace("."+fileFormat,"_"+fileFormat));
        controllerForTests.decipherFiles(cipherFilesDir);

        File decipheredFile= new File("WorkingFolder\\defaultFolder\\fileDecipher\\" + fileName.replace("."+fileFormat," (DECIPHER)."+fileFormat));
        long decipheredFileLength = originalFileTested.length();
        RandomAccessFile randomAccessDecipherFile;
        try {
            randomAccessDecipherFile = new RandomAccessFile(decipheredFile,"r");
        } catch (FileNotFoundException e) {
            System.out.println("Was unable to create random access file");
            return false;
        }
        byte[] decipheredFileByteArray100First = new byte[100];
        byte[] decipheredFileByteArray100Last = new byte[100];

        try {
            randomAccessDecipherFile.read(decipheredFileByteArray100First,0,100);
            randomAccessDecipherFile.seek(fileLength-100);
            randomAccessDecipherFile.read(decipheredFileByteArray100Last,0,100);
        } catch (IOException e) {
            System.out.println("Was not able to convert file to byte array");
            return false;
        }

        byte[] hundredFirstAndHundredLastBytesDecipheredFile = new byte[200];

        for (int i = 0; i < 100; i++) {
            hundredFirstAndHundredLastBytesDecipheredFile[i] = decipheredFileByteArray100First[i];
            hundredFirstAndHundredLastBytesDecipheredFile[100+i] = decipheredFileByteArray100Last[i];
        }

        return Arrays.equals(hundredFirstAndHundredLastBytesOriginalFile,hundredFirstAndHundredLastBytesDecipheredFile);
    }

    //The first bytes when solving for massive (around 2Gb) files were different in the original and decipher files
    @Test
    public void testToSolveIssuesDecipheringMassiveFiles(){

        String fileName = "around2GbFile_mp4_Section_2_part00000001.txt";
        String dividedPart0FileTestedPath = "WorkingFolder\\defaultFolder\\fileDivisions\\around2GbFile_mp4\\"+fileName;

        fileName = "around2GbFile_mp4_Section_2_part00000001.cipher";
        String cipheredPart0FileTestedPath = "WorkingFolder\\defaultFolder\\fileCipher\\around2GbFile_mp4\\"+fileName;

        //Getting files
        File dividedPart0FileTested = new File(dividedPart0FileTestedPath);
        File cipheredPart0FileTested = new File(cipheredPart0FileTestedPath);
        File keyFile = new File("WorkingFolder\\defaultFolder\\fileCipher\\around2GbFile_mp4\\KEY.cipher");

        //Getting ByteArrays
        byte[] dividedPart0ByteArray;
        byte[] cipheredPart0ByteArray;
        byte[] decipherKeyByeArray;

        try {
            dividedPart0ByteArray = Files.readAllBytes(dividedPart0FileTested.toPath());
            cipheredPart0ByteArray = Files.readAllBytes(cipheredPart0FileTested.toPath());
            decipherKeyByeArray = Files.readAllBytes(keyFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //UnXORting
        byte[] unXoredArray = new byte[cipheredPart0ByteArray.length];
        for (int i = 0; i < cipheredPart0ByteArray.length; i++) {
            unXoredArray[i] = (byte) (cipheredPart0ByteArray[i] ^ decipherKeyByeArray[i%256]);
        }


        boolean result = Arrays.equals(dividedPart0ByteArray,unXoredArray);
        Assertions.assertTrue(result, "The first 100 bytes and last 100 bytes don't match the original and decipher");
    }


}
