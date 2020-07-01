package com.google.sps.data;

import java.util.zip.ZipEntry;

public class ApkFileTypeFilter {

  public String getApkFileType(ZipEntry ze) {
    String fileName = ze.getName();

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