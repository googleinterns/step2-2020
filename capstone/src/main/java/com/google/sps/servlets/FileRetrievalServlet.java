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
import java.util.LinkedList;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;


import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.users.UserService;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;

import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.DatastoreServiceFactory;



@WebServlet("/retrieve_files")
public class FileRetrievalServlet extends HttpServlet {

  private PreparedQuery result;

  private UserService userService = UserServiceFactory.getUserService();
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    List<APK> apks = new ArrayList<>();

    if (userService.isUserLoggedIn()) {

      // Retrieves the user's private APKs in Cloud Storage for display.

      result = retrievePrivateFiles(userService.getCurrentUser(), datastore);

      // The APK class takes filename, ownership and timestamp
      // as its argument. This helps script.js determine the
      // permissions on a file and also helps the display and 
      // delete servlets target a particular APK.
      for (Entity entity : result.asIterable()) {
        apks.add(new APK(((String) entity.getProperty("File_name")).substring(5), 
        "true", (long) entity.getProperty("Time")));
      }

      // Retrieves all public APKs in Cloud Storage for display.

      result = retrieveOwnedPublicFiles(userService.getCurrentUser(), datastore);

      for (Entity entity : result.asIterable()) {
        apks.add(new APK(((String) entity.getProperty("File_name")).substring(5), 
        "true1", (long) entity.getProperty("Time")));
      }
      
    }


    result = retrievePublicFiles(userService, datastore);

    for (Entity entity : result.asIterable()) {
      apks.add(new APK(((String) entity.getProperty("File_name")).substring(5), 
      "false", (long) entity.getProperty("Time")));
    }


    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(apks));

  }

  private PreparedQuery retrievePrivateFiles(final User currentUser, DatastoreService datastorage) {

    Query query = new Query(currentUser.getUserId());

    return datastorage.prepare(query);

  }

  private PreparedQuery retrieveOwnedPublicFiles(final User currentUser, DatastoreService datastorage) {
    
    Filter visibilityFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, currentUser.getUserId());

    Query query = new Query("Vaderker").setFilter(visibilityFilter);

    return datastorage.prepare(query);

  }

  private PreparedQuery retrievePublicFiles(final UserService userServices, DatastoreService datastorage) {

    Query query;

    if (userServices.isUserLoggedIn()) {

      User currentUser = userServices.getCurrentUser();
      Filter visibilityFilter = new FilterPredicate("UserId", FilterOperator.NOT_EQUAL, currentUser.getUserId());

      query = new Query("Vaderker").setFilter(visibilityFilter);
      
    } else { query = new Query("Vaderker"); }

    return datastorage.prepare(query);
    
  }

}