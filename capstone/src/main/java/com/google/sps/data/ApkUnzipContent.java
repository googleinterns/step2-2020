package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.HashMap;

public class ApkUnzipContent {

  // Declare variables for content in APK
  private long dexFileSize, resFileSize, libraryFileSize, assetsFileSize, resourcesFileSize, miscFileSize;
  public long timestamp = System.currentTimeMillis();
  public HashMap<String, ArrayList<Long>> apkPackageContentMap = new HashMap<String, ArrayList<Long>>();
  public HashMap<String, ArrayList<Long>> apkFilesListMap = new HashMap<String, ArrayList<Long>>();


  public void addApkDataToMapStorage(String nameOfFile, String typeOfFile, Long uncompressedSize, Long compressedSize) {
    
    ArrayList<Long> apkContentSizeDiff;
    if(apkPackageContentMap.containsKey(typeOfFile)) {
      apkContentSizeDiff = apkPackageContentMap.get(typeOfFile);
      apkContentSizeDiff.set(0, apkContentSizeDiff.get(0) + uncompressedSize);
      apkContentSizeDiff.set(1, apkContentSizeDiff.get(1) + compressedSize);
      apkPackageContentMap.put(typeOfFile, apkContentSizeDiff);
      apkPackageContentMap.put(typeOfFile, apkContentSizeDiff);
    } else {
      apkContentSizeDiff = new ArrayList<Long>();
      apkContentSizeDiff.add(uncompressedSize);
      apkContentSizeDiff.add(compressedSize);
      apkPackageContentMap.put(typeOfFile, apkContentSizeDiff);
    }

    ArrayList<Long> apkFileSizeDiff = new ArrayList<Long>();
    apkFileSizeDiff.add(uncompressedSize);
    apkFileSizeDiff.add(compressedSize);
    apkFilesListMap.put(nameOfFile, apkFileSizeDiff);
    
    
  }

  public Entity toEntity(String userId, String nameOfApk, Long apkSizeOnDisk, Long totalApkSize, Long filesCount) {
    Entity taskEntity = new Entity("UserFileFeature");
    taskEntity.setProperty("UserId", userId);
    taskEntity.setProperty("File_Name", nameOfApk);
    taskEntity.setProperty("Res_File_Size", apkPackageContentMap.get("res"));
    taskEntity.setProperty("Dex_File_Size", apkPackageContentMap.get("dex"));
    taskEntity.setProperty("Lib_File_Size", apkPackageContentMap.get("lib"));
    taskEntity.setProperty("Asset_File_Size", apkPackageContentMap.get("assets"));
    taskEntity.setProperty("Resource_File_Size", apkPackageContentMap.get("arsc"));
    taskEntity.setProperty("Misc_File_Size", apkPackageContentMap.get("misc"));
    taskEntity.setProperty("Total_Apk_size", totalApkSize);
    taskEntity.setProperty("Files_Count", filesCount);
    taskEntity.setProperty("Apk_Disk_Size", apkSizeOnDisk);
    taskEntity.setProperty("Timestamp", timestamp);

    return taskEntity;
  }
    
}