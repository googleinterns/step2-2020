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

import com.google.sps.data.APK;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

import com.google.api.gax.paging.Page;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet("/retrieve_files")
public class FileRetrievalServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private Storage storage = StorageOptions.newBuilder().setProjectId(PROJECTID)
  .build().getService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    // The boolean below is meant to bypass the 
    // generation of a folder as an APK file.
    boolean isDirectory = true;

    Bucket bucket = storage.get(BUCKETNAME);

    List<APK> apks = new ArrayList<>();

    // Retrieves all APKs in Cloud Storage for display.
    Page<Blob> blobs = bucket.list();
    for (Blob blob : blobs.iterateAll()) {
        if (isDirectory) {isDirectory = false; continue;}
        apks.add(new APK(blob.getName()));
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(apks));

  }
}