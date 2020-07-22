// Copyright 2020 Google LLC
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Mockito;

import java.lang.reflect.Method;

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
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;


@RunWith(JUnit4.class)
public class FileRetrievalServletTest {

  private Filter visibilityFilter;

  private Query public_apks_query;
  private Query private_apks_query;
  private Query publicly_owned_apks_query;

  private PreparedQuery public_apks;
  private PreparedQuery private_apks;
  private PreparedQuery publicly_owned_apks;
  
  private PreparedQuery publicFilesResult;
  private PreparedQuery publiclyOwnedFilesResult;
  private PreparedQuery privateFilesResult;

  private Method publicFiles;
  private Method personalFiles;
  private Method publiclyOwnedFiles;

  private FileRetrievalServlet fileRetrievalServlet;

  private UserService userServices;
  private DatastoreService datastore;
  private User user = new User("chronos@gmail.com", "appspot.com", "1928364761813763");

  private long currentTime = System.currentTimeMillis();

  private final LocalServiceTestHelper helper = 
  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUps() {

    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();
    userServices = Mockito.mock(UserService.class);

    Mockito.when(userServices.isUserLoggedIn()).thenReturn(true);
    Mockito.when(userServices.getCurrentUser()).thenReturn(user);

    Assert.assertTrue(userServices.isUserLoggedIn());
    Assert.assertEquals(user, userServices.getCurrentUser());

  }

  @Before
  public void initializeFileRetrievalMethods() throws Exception {

    fileRetrievalServlet = new FileRetrievalServlet();

    personalFiles = FileRetrievalServlet.class.getDeclaredMethod("retrievePrivateFiles", 
    User.class, DatastoreService.class);
    publicFiles = FileRetrievalServlet.class.getDeclaredMethod("retrievePublicFiles", 
    UserService.class, DatastoreService.class);
    publiclyOwnedFiles = FileRetrievalServlet.class.getDeclaredMethod("retrieveOwnedPublicFiles", 
    User.class, DatastoreService.class);

    publicFiles.setAccessible(true);
    personalFiles.setAccessible(true);
    publiclyOwnedFiles.setAccessible(true);

  }


  @Before
  public void querySetups() {

    visibilityFilter = new FilterPredicate("UserId", FilterOperator.EQUAL, user.getUserId());

    public_apks_query = new Query("Vaderker");
    private_apks_query = new Query(user.getUserId());
    publicly_owned_apks_query = new Query("Vaderker").setFilter(visibilityFilter);

  }

  @Test
  public void userCredentialsVerification() {

    Assert.assertEquals("chronos@gmail.com", user.getEmail());
    Assert.assertEquals("1928364761813763", user.getUserId());
    Assert.assertEquals("appspot.com", user.getAuthDomain());
  }

  @Test
  public void retrievingEmptyEntities() throws Exception {

    // This block of code retrieves the true results
    // from datastore to be tested against the results
    // returned by the functions called in the next block of code.
    public_apks = datastore.prepare(public_apks_query);
    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertEquals(public_apks.countEntities(withLimit(6)), publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
    
  }

  @Test
  public void retrievingOtherPublicEntities() throws Exception {

    // This function is ensuring that retrievePublicFiles returns the
    // correct query results which contains no duplicates in 
    // retrieveOwnedPublicFiles
    Entity public_file1 = new Entity("Vaderker");
    public_file1.setProperty("File_name", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity public_file2 = new Entity("Vaderker");
    public_file2.setProperty("File_name", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity("Vaderker");
    public_file3.setProperty("File_name", "Phylum.apk");
    public_file3.setProperty("UserId", "85425758352584589");
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Vaderker");
    public_file4.setProperty("File_name", "Kingdoms.apk");
    public_file4.setProperty("UserId", "4295948459425854");
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    public_apks = datastore.prepare(public_apks_query);
    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertEquals(public_apks.countEntities(withLimit(6)), publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
    
  }

  @Test
  public void retrievingAllPublicEntities() throws Exception {

    // This function checks if all public entities are retrieved
    // and if so, by the appropriate function calls to prevent duplicate
    // results from being generated
    Entity public_file1 = new Entity("Vaderker");
    public_file1.setProperty("File_name", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity public_file2 = new Entity("Vaderker");
    public_file2.setProperty("File_name", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity("Vaderker");
    public_file3.setProperty("File_name", "Phylum.apk");
    public_file3.setProperty("UserId", user.getUserId());
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Vaderker");
    public_file4.setProperty("File_name", "Kingdoms.apk");
    public_file4.setProperty("UserId", user.getUserId());
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertEquals(2, publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
    
  }

  @Test
  public void retrievingAllEntities() throws Exception {

    Entity public_file1 = new Entity("Vaderker");
    public_file1.setProperty("File_name", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity public_file2 = new Entity("Vaderker");
    public_file2.setProperty("File_name", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity(user.getUserId());
    public_file3.setProperty("File_name", "Phylum.apk");
    public_file3.setProperty("UserId", user.getUserId());
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Vaderker");
    public_file4.setProperty("File_name", "Kingdoms.apk");
    public_file4.setProperty("UserId", user.getUserId());
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertEquals(2, publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
    
  }

  @Test
  public void retrievingEmptyQueries() throws Exception {

    // This function is testing to ensure that the correct
    // kind of entities are retrieved when needed.
    Entity public_file1 = new Entity("VADERKER");
    public_file1.setProperty("File_name", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity public_file2 = new Entity("VADERker");
    public_file2.setProperty("File_name", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity("Public ");
    public_file3.setProperty("File_name", "Phylum.apk");
    public_file3.setProperty("UserId", "85425758352584589");
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Private Vaderker");
    public_file4.setProperty("File_name", "Kingdoms.apk");
    public_file4.setProperty("UserId", "4295948459425854");
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    public_apks = datastore.prepare(public_apks_query);
    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertEquals(public_apks.countEntities(withLimit(6)), publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
  
  }

  @Test
  public void notSignedIn() throws Exception {

    Mockito.when(userServices.isUserLoggedIn()).thenReturn(false);

    // This function is verifying if all
    // public files are reported or shown
    // to a user who is not logged in.
    Entity public_file1 = new Entity("Vaderker");
    public_file1.setProperty("File_name", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity private_file = new Entity("5566495842542584");
    private_file.setProperty("File_name", "Avalon.apk");
    private_file.setProperty("UserId", "5566495842542584");
    private_file.setProperty("Time", currentTime);

    datastore.put(private_file);

    Entity public_file2 = new Entity("Vaderker");
    public_file2.setProperty("File_name", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity("Vaderker");
    public_file3.setProperty("File_name", "Phylum.apk");
    public_file3.setProperty("UserId", "85425758352584589");
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Vaderker");
    public_file4.setProperty("File_name", "Kingdoms.apk");
    public_file4.setProperty("UserId", "4295948459425854");
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    public_apks = datastore.prepare(public_apks_query);
    private_apks = datastore.prepare(private_apks_query);
    publicly_owned_apks = datastore.prepare(publicly_owned_apks_query);

    privateFilesResult = (PreparedQuery) personalFiles.invoke(fileRetrievalServlet,  user, datastore);
    publicFilesResult = (PreparedQuery) publicFiles.invoke(fileRetrievalServlet, userServices, datastore);
    publiclyOwnedFilesResult = (PreparedQuery) publiclyOwnedFiles.invoke(fileRetrievalServlet,  user, datastore);

    Assert.assertFalse(userServices.isUserLoggedIn());
    Assert.assertEquals(public_apks.countEntities(withLimit(6)), publicFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(private_apks.countEntities(withLimit(6)), privateFilesResult.countEntities(withLimit(6)));
    Assert.assertEquals(publicly_owned_apks.countEntities(withLimit(6)), publiclyOwnedFilesResult.countEntities(withLimit(6)));
  
  }

  @After
  public void tearDowns() {helper.tearDown();}

}