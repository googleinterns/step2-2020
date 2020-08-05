// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.util.List;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.BlobInfo;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.ByteBuffer;
import com.google.cloud.storage.StorageOptions;
import javax.servlet.annotation.MultipartConfig;

import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;


@WebServlet("/binary_upload")
@MultipartConfig
public class APKUploadServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String fileName;

  private BlobId blobId;
  private BlobInfo blobInformation;

  private String file_visibility;
  private long currentTime;

  private final UserService userService = UserServiceFactory.getUserService();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final Storage storage = StorageOptions.newBuilder().setProjectId(PROJECTID)
  .build().getService();


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {

    if (!userService.isUserLoggedIn()) {response.sendError(403);}

    file_visibility = request.getParameter("visibility");

    // The line below only retrieves files with sizes greater than 
    // 0 bytes from the server making the post.
    List<Part> apks = request.getParts().stream()
    .filter(part -> "files".equals(part.getName()) && part.getSize() > 0)
    .collect(Collectors.toList());

    RequestDispatcher unzip = this.getServletContext()
    .getRequestDispatcher("/unzip");


    for (Part file : apks) {

      fileName = "apks/" + file.getSubmittedFileName();
      

      if ( !(fileName.trim().endsWith(".apk")) ) {continue;}

      // The block of code below uploads only APK files to cloud storage in chunks.
      blobId = BlobId.of(BUCKETNAME, "apks/" + userService.getCurrentUser().getUserId() + "/" + file.getSubmittedFileName());
      blobInformation = BlobInfo.newBuilder(blobId).build();

      writeFilesToCloudStorage(storage, blobInformation, file.getInputStream());

      // The attributes below send information essential for identifying
      // the stored blobs later on to the unzip servlet for cohesive data storage.
      request.setAttribute("object_name", fileName);
      request.setAttribute("fileName", file.getSubmittedFileName());
      request.setAttribute("userId", userService.getCurrentUser().getUserId());
      unzip.include(request, response);

      currentTime = (long) request.getAttribute("Time");
      
      storeTrackedFiles(fileName, file_visibility, datastore, currentTime, userService.getCurrentUser());

    }
    
    response.sendRedirect("/#/explore");
  }

  // This function marks files as public or private so
  // the correct content a generated for a user
  private void storeTrackedFiles(final String file_name, final String visible, 
  DatastoreService datastorage, long time, final User currentUser) {

    Entity file;

    if ("Private".equals(visible)) {file = new Entity(currentUser.getUserId());}
    else {file = new Entity("Vaderker");}
    file.setProperty("FileName", file_name);
    file.setProperty("UserId", currentUser.getUserId());
    file.setProperty("Time", time);

    datastorage.put(file);

  }

  private void writeFilesToCloudStorage(final Storage cloud_storage, 
  final BlobInfo blobInfo, final InputStream content) {

    // The snippet below upload files to cloud storage bits by bits. This
    // is essential to allow the upload of larger files through the use of
    // a write channel.
    try (WriteChannel writer = cloud_storage.writer(blobInfo)) {

      // Files are being written in 10 KB chunks to
      // cloud storage. This makes it possible to upload 20 MB
      // files to cloud storage without memory limitations 
      int chunk_size = 10_240;

      byte[] apk_file = new byte[chunk_size];

      try {
        int limit;

        // The loop below writes data to cloud storage in pieces rather
        // than an entire chunk.
        while ((limit = content.read(apk_file)) >= 0) {
          writer.write(ByteBuffer.wrap(apk_file, 0, limit));
        }

      } catch (Exception e) {System.out.println("Out Of Memory");}
      
    } catch (Exception e) {System.out.println("Error!");}

  }

}