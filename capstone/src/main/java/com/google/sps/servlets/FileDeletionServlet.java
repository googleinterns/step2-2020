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

import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;



@WebServlet("/delete_file")
public class FileDeletionServlet extends HttpServlet {

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String fileName;
  private String fileVisibility;

  private BlobId blobId;

  private final UserService userService = UserServiceFactory.getUserService();
  private final DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
  private final Storage storage = StorageOptions.newBuilder().setProjectId(PROJECTID)
  .build().getService();

  private User currentUser;


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    // The two lines below retrieve certain details about the desired apk
    // so the wrong files aren't deleted.
    fileName = request.getParameter("file_name");
    fileVisibility = request.getParameter("ownership");

    // The condition below is a safety belt for situations involving
    // the manipulation of permissions for public APKs
    if (!userService.isUserLoggedIn()) {response.sendError(403);}

    currentUser = userService.getCurrentUser();

    // This condition below is a second safety belt that ensures the requested
    // file targeted for deletion belongs to the user making the request.
    if (!deleteUnzippedApk(dataStore, fileName, currentUser.getUserId())) {response.sendError(404);}

    deleteTrackedFile(dataStore, "apks/" + fileName, currentUser.getUserId(), fileVisibility.trim());

    // The block of code below creates an ID that cloud storage
    // uses to locate the desired APK and deletes it.
    blobId = BlobId.of(BUCKETNAME, "apks/" + currentUser.getUserId() + "/" + fileName);
    storage.delete(blobId);

    response.sendRedirect("/#/explore");

  }

  // The functions below delete all entries of the target APK so no
  // records of it are kept.
  private boolean deleteUnzippedApk(DatastoreService datastore, String apk_name, String userId) {

    Filter userIdFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, userId);
    Filter fileNameFilter = new FilterPredicate("File_name", FilterOperator.EQUAL, apk_name);

    CompositeFilter targetFileFilter =
    CompositeFilterOperator.and(fileNameFilter, userIdFilter);


    Query query = new Query("UserFileFeature").setFilter(targetFileFilter);

    PreparedQuery results = datastore.prepare(query);

    int numberOfEntities = 0;

    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
      numberOfEntities++;
    }

    // This condition below is the bulwark of this servlet.
    // It ensures that the file targeted for deletion belongs to
    // the requester by checking if the filters generated any results.
    if (numberOfEntities == 0) {return false;}

    return true;
  }

  private void deleteTrackedFile(DatastoreService datastore, 
  final String apk_name, final String userId, final String file_visibility) {

    Filter userIdFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, userId);
    Filter fileNameFilter = new FilterPredicate("File_name", FilterOperator.EQUAL, apk_name);

    CompositeFilter targetFileFilter =
    CompositeFilterOperator.and(fileNameFilter, userIdFilter);

    Query query;

    // This condition is the pivot for the function. It tells the function where
    // the target file could be found and deleted.
    if (file_visibility.equals("true")) { query = new Query(userId).setFilter(fileNameFilter); }
    else { query = new Query("Vaderker").setFilter(targetFileFilter); }

    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
    }
  }

}