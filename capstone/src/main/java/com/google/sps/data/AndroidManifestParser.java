package com.google.sps.data;

public class AndroidManifestParser {


    public void displayAndroidManifestContent(byte[] byteFile){
        String androidPermission= "android.permission";
        int lnt = androidPermission.length();
        String compare = "";
        // for (int i = 0; i < byteFile.length; i++) {
        //         compare += ((char)byteFile[i]);
        //     if(i % lnt!=0){
        //             //System.out.print(compare);   
        //         if(androidPermission != compare){
        //             System.out.print(compare);   
        //             //System.out.print((char) byteFile[i]);
        //         }
                     
        //         compare ="";
        //     }
                     
        // }
            for (int i = 0; i < byteFile.length; i++) {
                System.out.print((char) byteFile[i]);
            }
          
    }
}
