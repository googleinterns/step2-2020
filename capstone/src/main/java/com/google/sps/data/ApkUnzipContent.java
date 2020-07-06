package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ApkUnzipContent {
  /* Creates a class for each APK uploaded before unzipping for easy structuring of APK contents and testing */

  // Declare variable for storage
  public long timestamp = System.currentTimeMillis();
  
  // Declare map to store individual APK files and their characteristics
  private HashMap<String, ArrayList<Long>> apkFilesListMap = new HashMap<String, ArrayList<Long>>();
  
  // Declare map to store categorized APK contents according to file types
  private HashMap<String, ArrayList<Long>> apkPackageContentMap = new HashMap<String, ArrayList<Long>>();
  
  public HashMap<String,ArrayList<Long>> getApkPackageContentMap() {
    return this.apkPackageContentMap;
  }

  public HashMap<String,ArrayList<Long>> getApkFilesListMap() {
    return this.apkFilesListMap;
  }

  // Creates a constructor class for each APK with initialized map values to avoid null exceptions that may arise in Datastore from the absence of any category in APK
  public ApkUnzipContent() {
    apkPackageContentMap.put("res", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageContentMap.put("dex", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageContentMap.put("assets", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageContentMap.put("lib", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageContentMap.put("arsc", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageContentMap.put("misc", new ArrayList<Long>(Arrays.asList(0L, 0L)));

  }



  // Cumulates sizes of file types and file names to their keys in the maps
  public void addApkDataToMapStorage(String nameOfFile, String typeOfFile, Long uncompressedSize, Long compressedSize) {
    
    ArrayList<Long> apkContentSizeDiff;

    if((apkPackageContentMap.containsKey(typeOfFile))) {
      apkContentSizeDiff = apkPackageContentMap.get(typeOfFile);
      apkContentSizeDiff.set(0, apkContentSizeDiff.get(0) + uncompressedSize);
      apkContentSizeDiff.set(1, apkContentSizeDiff.get(1) + compressedSize);
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

  // Creates entity to be stored in Datastore using contents retrieved from the unzip function
  public Entity toEntity(String userId, String nameOfApk, Long apkSizeOnDisk, Long totalApkSize, Long filesCount) {
    Entity taskEntity = new Entity("UserFileFeature");
    taskEntity.setProperty("UserId", userId);
    taskEntity.setProperty("File_Name", nameOfApk);
    taskEntity.setProperty("Res_File_Size", apkPackageContentMap.get("res"));
    taskEntity.setProperty("Dex_File_Size", apkPackageContentMap.get("dex"));
    taskEntity.setProperty("Lib_File_Size", apkPackageContentMap.get("lib"));
    taskEntity.setProperty("Assets_File_Size", apkPackageContentMap.get("assets"));
    taskEntity.setProperty("Resources_File_Size", apkPackageContentMap.get("arsc"));
    taskEntity.setProperty("Misc_File_Size", apkPackageContentMap.get("misc"));
    taskEntity.setProperty("Total_Apk_size", totalApkSize);
    taskEntity.setProperty("Files_Count", filesCount);
    taskEntity.setProperty("Apk_Disk_Size", apkSizeOnDisk);
    taskEntity.setProperty("Apk_Lost_Size", (apkSizeOnDisk - totalApkSize));
    taskEntity.setProperty("Timestamp", timestamp);

    return taskEntity;
  }
    
}