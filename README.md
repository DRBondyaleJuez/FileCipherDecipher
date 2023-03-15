# 🔐 __Concurrent File Cipher & Decipher__ 👨‍💻
## A simple project to practice encryption and concurrent programming in Java.

___

![GitHub contributors](https://img.shields.io/github/contributors/DRBondyaleJuez/FileCipherDecipher)
![GitHub repo size](https://img.shields.io/github/repo-size/DRBondyaleJuez/FileCipherDecipher)
___

## __DESCRIPTION__

The program is simple, it ciphers files into several encrypted smaller files and its then able to decipher and recuperate the original file. Dividing, ciphering and deciphering the files are performed using concurrent coding, this is, using Thread Class, Runnable inteface and synchronized methods. It has also a small user interface to select the files to cipher and directories with the ciphered files to decipher. This view is managed using the JavaFXML framework.

### __Ciphering Process__

More precisely, the program begins by subdividing the file into maximum segments of 500Mb if they are smaller this is not necesary.

Then the system creates in the proyects directory a folder for each of the processes the file undergoes. Each file is divided and stored in as many <5Mb files as needed and then each of these files is ciphered based on a key of 256 bytes and stored in another folder with the extension .CIPHER. Both these process take place concurrently using a total of 7 threads. These threads are coordinated by a monitor type class named FileCipherDeposit with several synchronized methods which limit the access to critical zones to one thread at a time. Some were even conditional synchronized.

For example, the thread that retreived 5Mb files when they were divided could only do this if there were files divided to cipher. If there wasn't it was programmed to wait and the moment a file was ready to cipher it notified waiting threads.

### __Deciphering Process__

To decipher, a folder containing the key and the ciphered <5Mb files must be selected. Then a similar reverse process takes place where each small file is deciphered using the key and then reassembled together and saved in another folder. This process uses 4 concurrent threads and again are coordinated by the use of a monitor in the form of a class called FileDecipherDeposit with several synchronized methods that limit the access to critical zones. Some were even conditional synchronized.

For example, the runnable class in charged of joining (FileJoiner) had only one thread while the deciphering runnable class (FileDecipher) had 3. During deciphering big files a memory out of bound error appear due to the high amount of byte arrays generated. To provisionally solve this a conditional synchronization was applied to addition of further byte aray by the FileDecipher threads. If the diference between the files deciphered and the files joined was to high (in this case 10 was chosen) then the addition of further decipher byte arrays would wait. Then when a deciphered byte array was added  to the final reassembled file, and also eliminated from the queu, it notified the other threads so they could add more byte arrays.



<div style="text-align: center;">

![Diagram Cipher Decipher](https://user-images.githubusercontent.com/98281752/223583292-cebd6340-4c8b-498e-a832-177a9eb07a6d.png)

</div>

___
___

## __USAGE__
The interface is very simple. An image and a menu. The file menu has the options open to browse the file you want to cipher and the option decipher to browse for a directory with the name of a ciphered file "_[extension]" that should contain all files with a serialized number or code with the extension ".CIPHER" and a "key.CIPHER" file too. 

<div style="text-align: center;">

![gui](https://user-images.githubusercontent.com/98281752/223583430-267199b6-1011-4cdf-b9e1-b469f38fca7d.png)

</div>

The divided files are stored in a folder in the proyect named defaultFolder/fileDivisions, ciphered files in defaultFolder/fileCipher and deciphered files in defaultFolder/fileDecipher. Deciphered files have an additional "(DECIPHER)" in the name before the extension.

The process for file below 5Mb in size and below 100Kb is slightly different. These not divided as much into smaller files. Really large files above 500Mb will also present in their ciphered files a serialized number up to 99 and the code "PART" with another serialized number corresponding to the initial division of the file to avoid memory out of bound exceptions.

___
___

## __INSTALLATION INSTRUCTIONS__
### __For IDE:__
<!-- OL -->
1. Clone the repository in your local server
2. Run the project's Main Class in your IDE
 
### __For Ubuntu (In terminal):__
<!-- OL -->
1. If necessary [install java version 11 or higher](https://stackoverflow.com/questions/52504825/how-to-install-jdk-11-under-ubuntu)


    ```bash 
        sudo apt-get install openjdk-11-jdk
    ```


2. If necessary [install maven version 3.6.3 or higher](https://phoenixnap.com/kb/install-maven-on-ubuntu)

	```bash 
    	sudo apt install maven
    ``` 

3. If necessary [install git](https://www.digitalocean.com/community/tutorials/how-to-install-git-on-ubuntu-20-04)

	```bash 
        apt install git
    ```

4. Clone the repository	

	```bash 
        git clone https://github.com/DRBondyaleJuez/FileCipherDecipher.git
    ```

5. Go to the project folder. Make sure the [pom.xml](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) is there.

6.  Create [.jar file](https://en.wikipedia.org/wiki/JAR_(file_format)) executable in target folder using the following code:

    ```bash
		mvn install 
    ```

7. This code uses javafxml so we recommend the use of the following code  to run the program :

    ([*Source*](https://github.com/openjfx/javafx-maven-plugin))

	```bash 
        mvn javafx:run
    ```

___
___
## __INSTRUCTIONS FOR CONTRIBUTORS__
The objective of the project was to practice and apply java knowledge. No further contributions will be need all of this is just a training excercise.  

Hope you may find the code useful and please acknowledge its origin and authorship if use for any other purpose.
