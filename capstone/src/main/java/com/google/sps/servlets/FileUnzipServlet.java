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

import com.google.api.gax.paging.Page;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


@WebServlet("/unzip")
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

    String nameOfApk = (String) request.getAttribute("file_name");

    // The ID of your GCS object
    String objectName = (String) request.getAttribute("object_name");

    Blob blob = getApkObjectFromCloudStorage(projectId, bucketName, objectName);
    
    // Checks the success of the unzip function and responds with the appropriate values
    boolean checkUnzipSuccess = analyzeApkFeatures(nameOfApk, blob, userId);

    if (checkUnzipSuccess) {

      response.setContentType("text/html;charset=UTF-8");
      response.getWriter().println("success");
      
      System.out.println("File has been successfully unzipped.");
      
    } else {response.sendError(415);}
  }
  
  public static Blob getApkObjectFromCloudStorage(String projectId, String bucketName, String objectName) {
    
    // Initiate bucket details from CloudStorage for apk retrieval
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Retrieve the blob for the apk 
    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    //System.out.println(blob.getName());
    
    return blob;
  }

  public static boolean analyzeApkFeatures(String nameOfApk, Blob blob, String userId) {

    byte[] apkBytes = blob.getContent();
    long apkSizeOnDisk = blob.getSize();
    long totalApkSize = 0;
    long filesCount = 0;

    // TODO: (https://github.com/googleinterns/step2-2020/issues/22): Change the retrieved size from uncompressed to compressed size so that we can show the zip noise due to zip alignment and zipCentralDict
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
        unzipContent.addApkDataToMapStorage(
          fileName, 
          fileFilter.getApkFileType(fileName),
          ze.getSize(),
          ze.getCompressedSize()
        );
      
        totalApkSize += ze.getCompressedSize();
        
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
      Entity fileEntity = unzipContent.toEntity(userId + nameOfApk, apkSizeOnDisk, totalApkSize, filesCount);


      // Initiate the Datastore service for storage of entity created
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(fileEntity);
     
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