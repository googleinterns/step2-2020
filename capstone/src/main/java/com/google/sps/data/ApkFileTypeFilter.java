package com.google.sps.data;

public class ApkFileTypeFilter {
  /*Returns the file type of files in APK  for mapping */
  
  public String getApkFileType(String fileName) {

    if (fileName.startsWith("res/")) {
      return "res";
    } else if (fileName.startsWith("lib/")) {
      return "lib";
    } else if (fileName.startsWith("assets/")) {
      return "assets";
    } else if (fileName.endsWith(".dex")) {
      return "dex";
    } else if (fileName.endsWith(".arsc")) {
      return "arsc";
    } else {
      return "misc";
    }
  }

}