package com.google.servlets;

import com.google.sps.data.ApkFileTypeFilter;
import com.google.sps.data.ApkUnzipContent;

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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import com.google.api.gax.paging.Page;


@WebServlet("/unzip")
@MultipartConfig
public class FileUnzipServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    // TODO: (https://github.com/googleinterns/step2-2020/issues/20): Hard-coded UserId until the upload and login functions have been fully implemented
    // String userId = request.getParamter("userId");
    String userId = "abcde";
    
    // The ID of GCP project
    String projectId = "step-2020-team-2";

    // The ID of GCS bucket
    String bucketName = "vaderker-uploadedstoragebucket";

    // TODO: (https://github.com/googleinterns/step2-2020/issues/21): Hard-coded name of apk until the upload and login functions have been fully implemented
    // String fileName = request.getParamter("files");
    String nameOfApk = "anubis_debug.apk";

    // The ID of your GCS object
    String objectName = "apks/" + nameOfApk;
    System.out.println(objectName);

    Blob blob = getApkObjectFromCloudStorage(projectId, bucketName, objectName);
    
    // Checks the success of the unzip function and responds with the appropriate values
    boolean checkUnzipSuccess = analyzeApkFeatures(nameOfApk, blob, userId);

    if (checkUnzipSuccess) {
      System.out.println("File has been successfully unzipped.");
      response.sendRedirect("/#/explore");
    } else {
      response.sendError(415);
    }

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

  public static boolean analyzeApkFeatures(String nameOfApk, Blob blob, String userId) {

    byte[] apkBytes = blob.getContent();
    long apkSizeOnDisk = blob.getSize();
    long totalApkSize = 0;
    long filesCount = 0;

    // TODO: (https://github.com/googleinterns/step2-2020/issues/22): Change the retrieved size 
    // from uncompressed to compressed size so that we can show the zip noise due to zip alignment and zipCentralDict
    try {

      //Declare unzip elements
      InputStream is = new ByteArrayInputStream(apkBytes);
      ZipInputStream zis = new ZipInputStream(is);
      ZipEntry ze = zis.getNextEntry();

      // Create class that determines the type of file in APK
      ApkFileTypeFilter fileFilter = new ApkFileTypeFilter();

      //Create class that helps store APK contents in Datastore
      ApkUnzipContent unzipContent = new ApkUnzipContent();

      while(ze != null) {
        String fileName = ze.getName();
        long compressedSize = ze.getCompressedSize();
        long uncompressedSize = ze.getSize();

        // For testing purposes in the console
        System.out.printf("File %s:\n", fileName);
        System.out.printf("Entry Compressed Size %d:\n", compressedSize);

        // Handle cases where ZipEntry returns -1 for unknown sizes and 
        if (uncompressedSize == -1) {
          compressedSize = 0;
          uncompressedSize = 0;
          long startOfFile = is.available();
          long read = 0;
          byte[] buffer = new byte[10000];

          // Calculates uncompressed size
          while ((read = zis.read(buffer, 0, 10000)) > 0) {
            uncompressedSize += read;
          }

          // Calculates the compressed size
          compressedSize = startOfFile - is.available();
          System.out.printf("Real Compressed Size %d:\n", compressedSize);
          System.out.printf("Real Uncompressed Size %d:\n", uncompressedSize);
        }
        System.out.println();

        // Map file types and names to storage
        unzipContent.addApkDataToMapStorage(
          fileName, 
          fileFilter.getApkFileType(fileName),
          uncompressedSize,
          compressedSize
        );

        totalApkSize += compressedSize;

        // Count number of files
        if (!ze.isDirectory()) {
          filesCount++;
        }
        //close this ZipEntry
        zis.closeEntry();
        ze = zis.getNextEntry();
      }

      //Print the feature to the console
      System.out.println(filesCount);
      
      // Set attributes for the entity to be stored in Datastore
      Entity taskEntity = unzipContent.toEntity(userId, nameOfApk, apkSizeOnDisk, totalApkSize, filesCount);

      // Initiate the Datastore service for storage of entity created
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
      
      int ZipSize = zis.available();
      System.out.println("size in KB : " + ZipSize);

      //close last ZipEntry
      zis.closeEntry();
      zis.close();

      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

  }

}