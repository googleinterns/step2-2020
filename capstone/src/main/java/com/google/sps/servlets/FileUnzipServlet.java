package com.google.servlets;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Arrays;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.api.gax.paging.Page;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;


@WebServlet("/unzip")
@MultipartConfig
public class FileUnzipServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    // String userId = request.getParamter("userId");
    //Hard-coded UserId
    String userId = "abcde";
    
    // The ID of GCP project
    String projectId = "step-2020-team-2";

    // The ID of GCS bucket
    String bucketName = "vaderker-uploadedstoragebucket";

    // String fileName = request.getParamter("files");
    //Hard-coded apkName
    String nameOfApk = "ApiDemos-debug.apk";

    // The ID of your GCS object
    String objectName = "apks/" + nameOfApk;
    System.out.println(objectName);

    Blob blob = getApkObjectFromCloudStorage(projectId, bucketName, objectName);
    analyzeApkFeatures(nameOfApk, blob, userId);

    System.out.println("File " + nameOfApk + " uploaded to bucket " + bucketName + " as " + objectName);

    response.setContentType("text/html;charset=UTF-8");

  }
  
  public static Blob getApkObjectFromCloudStorage(String projectId, String bucketName, String objectName) {
    
    // Initiate bucket details from CloudStorage for apk retrieval
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Bucket bucket = storage.get(bucketName);
    
    // Prints the entire apk in Cloud Storage to the console
    Page<Blob> blobs = bucket.list();
    for (Blob blob1 : blobs.iterateAll()) {
      System.out.println(blob1.getName());
    }

    // Retrieve the blob for the apk 
    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    System.out.println(blob.getName());
    
    return blob;

   
  }

  public static void analyzeApkFeatures(String nameOfApk, Blob blob, String userId) {
    
    byte[] apkBytes = blob.getContent();
    long dexFileSize = 0, resFileSize = 0, libraryFileSize  = 0, assetsFileSize = 0, resourcesFileSize = 0, miscFileSize = 0, totalApkSize = 0;
    int filesCount = 0;
    long timestamp = System.currentTimeMillis();

    try {

      //Declare unzip elements
      InputStream is = new ByteArrayInputStream(apkBytes);
      ZipInputStream zis = new ZipInputStream(is);
      ZipEntry ze = zis.getNextEntry();

      while(ze != null) {
        String fileName = ze.getName();
        totalApkSize += ze.getSize();
        if (fileName.startsWith("res/")) {
            resFileSize += ze.getSize();
        } else if (fileName.startsWith("lib/")) {
            libraryFileSize += ze.getSize();
        } else if (fileName.startsWith("assets/")) {
            assetsFileSize += ze.getSize();
        } else if (fileName.endsWith(".dex")) {
            dexFileSize += ze.getSize();
        } else if (fileName.endsWith(".arsc")) {
            resourcesFileSize += ze.getSize();
        } else {
            miscFileSize += ze.getSize();
        }
        
        if (!ze.isDirectory()) {
          filesCount++;
        }
        //close this ZipEntry
        zis.closeEntry();
        ze = zis.getNextEntry();
      }

      //Print the features to the console
      System.out.println(filesCount);
      System.out.println("\n" + resFileSize + "\n" + libraryFileSize + "\n" + assetsFileSize + "\n" + dexFileSize + "\n" + resourcesFileSize + "\n" + miscFileSize + "\n");
      
      // Initiate the Datastore service for storage of entity created
      Entity taskEntity = new Entity("UserFileFeature");
      taskEntity.setProperty("UserId", userId);
      taskEntity.setProperty("File_Name", nameOfApk);
      taskEntity.setProperty("Res_File_Size", resFileSize);
      taskEntity.setProperty("Dex_File_Size", dexFileSize);
      taskEntity.setProperty("Lib_File_Size", libraryFileSize);
      taskEntity.setProperty("Asset_File_Size", assetsFileSize);
      taskEntity.setProperty("Resource_File_Size", resourcesFileSize);
      taskEntity.setProperty("Misc_File_Size", miscFileSize);
      taskEntity.setProperty("Total_Apk_size", totalApkSize);
      taskEntity.setProperty("Files_Count", filesCount);
      taskEntity.setProperty("Timestamp", timestamp);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
     
      //close last ZipEntry
      zis.closeEntry();
      zis.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}