package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;

import java.util.ArrayList;
import java.util.List;

public class ApkFileFeatures {

  // Declare variables and lists for content in APK
  private ArrayList<Long> dexFileSize, resFileSize, libraryFileSize, assetsFileSize, resourcesFileSize, miscFileSize;
  private Long totalApkSize, filesCount, lostSize;

  // Create APK class once data has been received from Datastore
  public ApkFileFeatures(Entity entity) {
    this.dexFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Dex_File_Size"));
    this.resFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Res_File_Size"));
    this.libraryFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Lib_File_Size"));
    this.assetsFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Assets_File_Size"));
    this.resourcesFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Resources_File_Size"));
    this.miscFileSize = new ArrayList<Long>((List<Long>)entity.getProperty("Misc_File_Size"));
    this.totalApkSize = (long) entity.getProperty("Total_Apk_size");
    this.filesCount = (long) entity.getProperty("Files_Count");
    this.lostSize = (long) entity.getProperty("Apk_Lost_Size");

  }

  public ApkFileFeatures(List<Long> dexFiles, List<Long> resFiles, List<Long> libFiles, List<Long> assetsFiles, List<Long> resourcesFiles, List<Long> miscFiles, Long numOfFiles, Long totalSize, Long sizeLost) {
    // Creates class for apk features and does not require entity as a parameter
    
    this.dexFileSize = new ArrayList<Long>(dexFiles);
    this.resFileSize = new ArrayList<Long>(resFiles);
    this.libraryFileSize = new ArrayList<Long>(libFiles);
    this.assetsFileSize = new ArrayList<Long>(assetsFiles);
    this.resourcesFileSize = new ArrayList<Long>(resourcesFiles);
    this.miscFileSize = new ArrayList<Long>(miscFiles);
    this.totalApkSize = (long) totalSize;
    this.filesCount = (long) numOfFiles;
    this.lostSize = (long) sizeLost;
  } 

}