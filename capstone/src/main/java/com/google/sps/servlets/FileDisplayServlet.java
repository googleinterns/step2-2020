package com.google.servlets;

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
import javax.servlet.annotation.MultipartConfig;
import java.io.PrintWriter;
import com.google.gson.Gson; //Convert json to string



@WebServlet("/display")
@MultipartConfig
public class FileDisplayServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    // String userId = request.getParamter("userId");
    // Hard-coded UserId
    String userId = "abcde";

    Filter keyFilter =
    new FilterPredicate("UserId", FilterOperator.EQUAL, userId);
    Query query = new Query("UserFileFeature").setFilter(keyFilter).addSort("Timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    PreparedQuery results = datastore.prepare(query);

    ArrayList<String> filesFeatures = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long resFileSize = (long) entity.getProperty("Res_File_Size");
      long dexFileSize = (long) entity.getProperty("Dex_File_Size");
      long assetsFileSize = (long) entity.getProperty("Asset_File_Size");
      long libraryFileSize = (long) entity.getProperty("Lib_File_Size");
      long resourcesFileSize = (long) entity.getProperty("Resource_File_Size");
      long miscFileSize = (long) entity.getProperty("Misc_File_Size");
      long totalFileSize = (long) entity.getProperty("Total_Apk_size");
      long filesCount = (long) entity.getProperty("Files_Count");
      long timestamp = (long) entity.getProperty("Timestamp");
      String file = totalFileSize + "";
      filesFeatures.add(file);
      System.out.println("\n" + resFileSize + "\n" + libraryFileSize + "\n" + assetsFileSize + "\n" + dexFileSize + "\n" + resourcesFileSize + "\n" + miscFileSize + "\n");
      
      PrintWriter out = response.getWriter();
      out.println(resFileSize + "\n" + totalFileSize + "\n" + libraryFileSize + "\n" + assetsFileSize + "\n" + dexFileSize + "\n" + resourcesFileSize + "\n" + miscFileSize + "\n");
    }

    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(filesFeatures));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);

  }
  
  

}