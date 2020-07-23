package com.google.sps.data.dexparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.logging.Level; 
import java.util.logging.Logger; 
import java.util.logging.*; 

/*Initiates parsing of the DEX files
 *
 *This class can run on its own or its methods can be called 
 */
public class DexLoader {

  private static final Logger LOGGER = Logger.getLogger(DexLoader.class.getName());

  public static void main(String[] args) {

    // To run this locally, input file path
    System.out.print("Enter the file path of your APK: ");
    Scanner input = new Scanner(System.in);

    String filePath = input.next();

    // Close scanner
    input.close();

    analyzeApkFeaturesLocally(filePath);
  }


  /*
   * Wrapper used specifically for running function similar to unzipping locally for parsing
   */
  public static boolean analyzeApkFeaturesLocally(String filePath) {
    long totalApkSize = 0;
    long filesCount = 0;

    try {

      //Declare unzip elements
      FileInputStream is = new FileInputStream(filePath);
      ZipInputStream zis = new ZipInputStream(is);
      ZipEntry ze = zis.getNextEntry();

      while(ze != null) {
        String fileName = ze.getName();
        LOGGER.info("File Name: " + fileName);

        long compressedSize = ze.getCompressedSize();
        long uncompressedSize = ze.getSize();

        // Handle cases where ZipEntry returns -1 for unknown sizes and 
        if (uncompressedSize == -1) {
          compressedSize = 0;
          uncompressedSize = 0;
          long startOfFile = is.available();
          long read = 0;
          byte[] buffer = new byte[10000];

          // Calculates uncompressed size
          while ((read = zis.read(buffer, 0, 10000)) > 0) {
            uncompressedSize += read;
          }

          // Calculates the compressed size
          compressedSize = startOfFile - is.available();
          LOGGER.info("Real Compressed Size: " + compressedSize);
          LOGGER.info("Real Uncompressed Size: " + uncompressedSize);

        } else {
          // For testing purposes in the console
          LOGGER.info("Real Compressed Size " + compressedSize);
          LOGGER.info("Real Uncompressed Size " + uncompressedSize);

        }

        totalApkSize += compressedSize;
        // Count number of files
        if (!ze.isDirectory()) {
          filesCount++;
        }
        //close this ZipEntry
        zis.closeEntry();
        ze = zis.getNextEntry();
      }

      System.out.println();

      //Print the feature to the console
      LOGGER.info("" + filesCount);
      LOGGER.info("" + totalApkSize);

      //close last ZipEntry
      zis.closeEntry();
      zis.close();

      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

  }

}