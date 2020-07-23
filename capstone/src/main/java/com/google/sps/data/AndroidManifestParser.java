package com.google.sps.data;

public class AndroidManifestParser {

    public static void main(String[] args) {    
      AndroidManifestParser ManifestParser = new AndroidManifestParser();
    }
    public void displayAndroidManifestContent(byte[] byteFile){
        for (int i = 0; i < byteFile.length; i++) {
            System.out.print((char) byteFile[i]);
        }
    }
}
