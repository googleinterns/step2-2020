package com.google.sps.data.dexparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

import java.util.zip.DataFormatException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.Scanner;
import java.util.zip.ZipInputStream;
import java.util.logging.Level; 
import java.util.logging.Logger; 


/* Initiates parsing of the DEX files
 *
 * This class can run on its own or its methods can be called 
 */
public class DexLoader {

  private static final Logger LOGGER = Logger.getLogger(DexLoader.class.getName());

  public static void main(String[] args) throws IOException {

    // To run this locally, input file path
    System.out.print("Enter the file path of your APK: ");
    Scanner input = new Scanner(System.in);

    String filePath = input.next();

    // Close scanner
    input.close();

    
    ArrayList<RandomAccessFile> rafs = analyzeApkFeaturesLocally(filePath);
    

    for (RandomAccessFile raf : rafs) {
      DexData dexData = new DexData(raf);

      try {
        dexData.load();
        raf.close();
      } catch (DataFormatException e) {
        e.printStackTrace();
      }

    }  
  }


  /*
   * Wrapper used specifically for parsing with filePath
   */
  public static ArrayList<RandomAccessFile> analyzeApkFeaturesLocally(String filePath) {
    
    ArrayList<RandomAccessFile> rafs = new ArrayList<>();

    try {

      //Declare unzip elements
      FileInputStream is = new FileInputStream(filePath);
      ZipInputStream zis = new ZipInputStream(is);      
      ZipEntry ze = zis.getNextEntry();

      while(ze != null) {
        String fileName = ze.getName();
        LOGGER.info("File Name: " + fileName);

        if(fileName.endsWith(".dex")) {
          RandomAccessFile raf = openClassDexZipFileEntry(zis);
          rafs.add(raf);
        }      

        //close this ZipEntry
        zis.closeEntry();
        ze = zis.getNextEntry();
      }

      System.out.println();

      //close last ZipEntry
      zis.closeEntry();
      zis.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

    return rafs;

  }

  /*
   * Wrapper used specifically for parsing with ZipInputStream
   */
  public static ArrayList<RandomAccessFile> analyzeApkFeaturesOnline(ZipInputStream zis) {

    ArrayList<RandomAccessFile> rafs = new ArrayList<>();

    try {

      //Declare unzip elements
      ZipEntry ze = zis.getNextEntry();

      while(ze != null) {
        String fileName = ze.getName();
        // LOGGER.info("File Name: " + fileName);

        if(fileName.endsWith(".dex")) {
          RandomAccessFile raf = openClassDexZipFileEntry(zis);
          rafs.add(raf);
        }      

        //close this ZipEntry
        zis.closeEntry();
        ze = zis.getNextEntry();
      }

      System.out.println();

      //close last ZipEntry
      zis.closeEntry();
      zis.close();


    } catch (IOException e) {
      e.printStackTrace();
    }

    return rafs;

  }

  /* Runs with the DexParser servlet and local wrapper for DEX parsing*/
  public static RandomAccessFile openClassDexZipFileEntry(ZipInputStream zis) throws IOException {
    
    RandomAccessFile raf = new RandomAccessFile("data.dex", "rw");

		/*
		 * Copy all data from ZipInputStream to file
		 */
		byte[] dataCopyBuffer = new byte[32768]; // Reads the whole data into the buffer without knowing the length

		// Saves the end point of the ZipInputStream
		int actual;

		while (true) {
			actual = zis.read(dataCopyBuffer);

			// Breaks when end of Zip Entry has been reached
			if (actual == -1) {
				System.out.println(actual);
				break;
			}

			raf.write(dataCopyBuffer, 0, actual);

		}
    
    /* For testing purposes
		  raf.seek(0);
		  String code = "";
      for (int i = 0; i < 50; i++) {
        code += raf.readLine() + "\n";
      }
    
		  System.out.println(code);
		*/

    return raf;
	
	}

}
