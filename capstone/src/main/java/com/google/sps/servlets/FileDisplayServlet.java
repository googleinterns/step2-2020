package com.google.servlets;

import com.google.sps.data.ApkFileFeatures;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.gson.Gson; //Convert json to string



@WebServlet("/display")
public class FileDisplayServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* Receives request from client side to retrieve APK features from Datastore and return those features for rendering to client */
    
    
    // TODO: (https://github.com/googleinterns/step2-2020/issues/19): Hard-coded UserId until the upload and login functions have been fully implemented
    // String userId = request.getParamter("userId");
    String userId = "abcde";
    String fileName = request.getParameter("apk_name");

    // Create a filter for retrieval of APKs specific to a certain user with UserId
    Filter userIdFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, userId + fileName);

    Query query = new Query("UserFileFeature").setFilter(userIdFilter);
    query.addSort("Timestamp", SortDirection.DESCENDING);

    // Initiate Datastore service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Retrieve query based on filter from Datastore
    PreparedQuery results = datastore.prepare(query);

    // Categorize features of APK in a class and store in list for better conversion to the client side
    ArrayList<ApkFileFeatures> filesFeatures = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      filesFeatures.add(new ApkFileFeatures(entity));      
    }

    // Convert list to json using gson
    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(filesFeatures));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Call the get() method through the post() method
    doGet(request, response);
  }
  
}