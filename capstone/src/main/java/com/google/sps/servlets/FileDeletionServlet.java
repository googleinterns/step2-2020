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

import java.io.IOException;

import javax.servlet.http.HttpServlet;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.StorageOptions;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.DatastoreServiceFactory;



@WebServlet("/delete_file")
public class FileDeletionServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String fileName;

  private BlobId blobId;

  private Storage storage = StorageOptions.newBuilder().setProjectId(PROJECTID)
  .build().getService();

  private DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    fileName = request.getParameter("file_name");

    // The block of code below creates an ID that cloud storage
    // uses to locate the desired APK and deletes it.
    blobId = BlobId.of(BUCKETNAME, "apks/" + fileName);
    storage.delete(blobId);

    deleteUnzippedApk(dataStore, fileName);

    response.sendRedirect("/#/explore");

  }

  private void deleteUnzippedApk(DatastoreService datastore, String apk_name) {

    Filter userIdFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, "abcde" + apk_name);

    Query query = new Query("UserFileFeature").setFilter(userIdFilter);

    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
    }
  }

}