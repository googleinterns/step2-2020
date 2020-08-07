package com.google.servlets;

import com.google.servlets.FileUnzipServlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;

import java.util.logging.Level; 
import java.util.logging.Logger; 
import java.util.zip.DataFormatException;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.google.gson.Gson;

import com.google.sps.data.dexparser.DexLoader;
import com.google.sps.data.dexparser.DexData;
import com.google.sps.data.dexparser.DexStructure.HeaderItem;
import com.google.sps.data.dexparser.DexFeaturesUpdate;
import com.google.sps.data.ApkFileFeatures;




@WebServlet("/dexparser")
public class DexParserServlet extends HttpServlet {
  
  private static final Logger LOGGER = Logger.getLogger(DexParserServlet.class.getName());

  private final String PROJECTID = "step-2020-team-2";
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private String fileName;
  private String userId;
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private final UserService userService = UserServiceFactory.getUserService();


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /* Receives request from client side to retrieve DEX features for an APK from Datastore and return those features for rendering to client */
    
    // The two lines below retrieve certain details about the desired apk
    // for the correct results to be generated.
    long time = Long.parseLong(request.getParameter("timeStamp"));
    String fileName = request.getParameter("fileName");

    // Create a filter for retrieval of DEX stats specific to a certain user and APK with a timestamp and filename
    Filter timeFilter = new FilterPredicate("Timestamp", FilterOperator.EQUAL, time);
    Filter fileNameFilter = new FilterPredicate("FileName", FilterOperator.EQUAL, fileName);

    CompositeFilter targetFileFilter =
    CompositeFilterOperator.and(fileNameFilter, timeFilter);

    Query query = new Query("UserDexFeature").setFilter(targetFileFilter);;

    // Retrieve query based on filter from Datastore
    PreparedQuery results = datastore.prepare(query);

    // Categorize features of APK in a class and store in list for better conversion to the client side
    ArrayList<DexFeaturesUpdate> dexFeatures = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      dexFeatures.add(new DexFeaturesUpdate(entity));
    }

    // Convert list to json using gson
    Gson gson = new Gson();
    response.setContentType("application/json");
    response.getWriter().println(gson.toJson(dexFeatures));
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    long time = Long.parseLong(request.getParameter("timeStamp"));

    // Name of APK
    fileName = request.getParameter("fileName");

    // Create a filter for retrieval of APKs specific to a certain user with a timestamp and filename
    Filter timeFilter = new FilterPredicate("Timestamp", FilterOperator.EQUAL, time);
    Filter fileNameFilter = new FilterPredicate("FileName", FilterOperator.EQUAL, fileName);

    CompositeFilter targetFileFilter =
    CompositeFilterOperator.and(fileNameFilter, timeFilter);

    Query query = new Query("UserDexFeature").setFilter(targetFileFilter);

    // Retrieve query based on filter from Datastore
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    // Checks if the DEX file has been processed before and simply retrieves it from Datastore
    if(!(results.isEmpty())) {
      doGet(request, response);
      return;
    }

    Query userIdQuery = new Query("UserFileFeature").setFilter(targetFileFilter);

    PreparedQuery queryResult = datastore.prepare(userIdQuery);

    for (Entity entity : queryResult.asIterable()) {
      userId = entity.getProperty("UserId").toString();
    }

    // The ID of your GCS object
    String objectName =  "apks/" + userId + "/" + fileName;

    // Retrieves the Blob from Cloud Storage using a method in the FileUnzip Servlet
    FileUnzipServlet fileUnzip = new FileUnzipServlet();

    Blob blob = fileUnzip.getApkObjectFromCloudStorage(PROJECTID, BUCKETNAME, objectName);

    DexLoader dexLoader = new DexLoader();
    InputStream is = new ByteArrayInputStream(blob.getContent());
    ZipInputStream zis = new ZipInputStream(is);

    // Supports more than one .dex file in the APKS
    ArrayList<RandomAccessFile> rafs = dexLoader.analyzeApkFeaturesOnline(zis);

    for (RandomAccessFile raf : rafs) {

      DexData dexData = new DexData(raf);

      try {
        dexData.load();
        raf.close();
      } catch (DataFormatException e) {
        e.printStackTrace();
      }

      HeaderItem mHeaderItem = dexData.getHeaderItem();

      // Creates entity for features of a DEX file
      DexFeaturesUpdate dexUpdate = new DexFeaturesUpdate();
      Entity entity = dexUpdate.updateDexStats(mHeaderItem, userId, time, fileName);
      datastore.put(entity);

    }
    
    // Calls the doGet method immediately so that the client side has the DEX stats
    doGet(request, response);

    
  }

}