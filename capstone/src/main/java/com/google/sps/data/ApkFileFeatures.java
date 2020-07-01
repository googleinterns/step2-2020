package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;

public class ApkFileFeatures {

  // Declare variables for content in APK
  private long dexFileSize, resFileSize, libraryFileSize, assetsFileSize, resourcesFileSize, miscFileSize, totalApkSize, filesCount;

  // Create APK class once data has been received from Datastore
  public ApkFileFeatures(Entity entity) {
    this.dexFileSize = (long) entity.getProperty("Dex_File_Size");
    this.resFileSize = (long) entity.getProperty("Res_File_Size");
    this.libraryFileSize = (long) entity.getProperty("Lib_File_Size");
    this.assetsFileSize = (long) entity.getProperty("Asset_File_Size");
    this.resourcesFileSize = (long) entity.getProperty("Resource_File_Size");
    this.miscFileSize = (long) entity.getProperty("Misc_File_Size");
    this.totalApkSize = (long) entity.getProperty("Total_Apk_size");
    this.filesCount = (long) entity.getProperty("Files_Count");

  }

}