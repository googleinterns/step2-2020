package com.google.sps.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


//This class converts the content of the AndriodManifest.xml into a
//byte array and displays it. This will be later sent to a parser class. 
public class AndroidManifestWrapper {

    public static void main(String args[]){     
      File file = new File (args [0]);
      AndroidManifestParser ManifestParser = new AndroidManifestParser();
      ManifestParser.decompressXML(readContentIntoByteArray(file));
    }

    public static byte[] readContentIntoByteArray(File file) {

      FileInputStream fileInputStream = null;
      byte[] bFile = new byte[(int) file.length()];

      try {
        //convert file into array of bytes
        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();
    }
    catch (Exception e) {
        e.printStackTrace();
    }

    return bFile;
    }
}
