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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.nio.ByteBuffer;
import com.google.cloud.storage.StorageOptions;
import javax.servlet.annotation.MultipartConfig;


@WebServlet("/binary_upload")
@MultipartConfig
public class APKUploadServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String fileName;
  private byte[] apk_file;

  private BlobId blobId;
  private BlobInfo blobInfo;

  private Storage storage = StorageOptions.newBuilder().setProjectId(PROJECTID)
  .build().getService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {

    // The line below only retrieves files with sizes greater than 
    // 0 bytes from the server making the post.
    List<Part> apks = request.getParts().stream()
    .filter(part -> "files".equals(part.getName()) && part.getSize() > 0)
    .collect(Collectors.toList());

    for (Part file : apks) {

      fileName = "apks/" + file.getSubmittedFileName();

      if ( !(fileName.trim().endsWith(".apk")) ) {continue;}

      // The line below retrieves the contents of the files 
      // in their bytes form for easy upload.

      // The block of code below uploads only APK files to cloud storage in chunks.
      blobId = BlobId.of(BUCKETNAME, fileName);
      blobInfo = BlobInfo.newBuilder(blobId).build();

      // The snippet below upload files to cloud storage bits by bits. This
      // is essential to allow the upload of larger files through the use of
      // a write channel.
      try (WriteChannel writer = storage.writer(blobInfo)) {

        apk_file = new byte[9000000];
        try (InputStream input = file.getInputStream()) {
            int limit;

            // The loop below writes data to cloud storage in pieces rather
            // than an entire chunk.
            while ((limit = input.read(apk_file)) >= 0) {
                writer.write(ByteBuffer.wrap(apk_file, 0, limit));
            }
        }
      }
    }
    response.sendRedirect("/#/explore");
  }
}