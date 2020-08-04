package com.google.sps.servlets;

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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;

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

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* Receives request from client side to retrieve APK features from Datastore and return those features for rendering to client */
    
    // The two lines below retrieve certain details about the desired apk
    // for the correct results to be generated.
    long time = Long.parseLong(request.getParameter("timeStamp"));
    String fileName = request.getParameter("apk_name");

    // Retrieve query based on filter from Datastore
    PreparedQuery results = retrieveFileFeatures(fileName, time, datastore);

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

  public PreparedQuery retrieveFileFeatures(String fileName, long time, DatastoreService datastore) {
    
    // Create a filter for retrieval of APKs specific to a certain user with a timestamp nad filename
    Filter timeFilter = new FilterPredicate("Timestamp", FilterOperator.EQUAL, time);
    Filter fileNameFilter = new FilterPredicate("File_name", FilterOperator.EQUAL, fileName);

    CompositeFilter targetFileFilter =
    CompositeFilterOperator.and(fileNameFilter, timeFilter);

    Query query = new Query("UserFileFeature").setFilter(targetFileFilter);

    return datastore.prepare(query);

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Call the get() method through the post() method
    doGet(request, response);
  }
  
}