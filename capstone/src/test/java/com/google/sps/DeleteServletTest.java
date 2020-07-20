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

/*package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.lang.reflect.Method;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.BlobInfo;

import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;


@RunWith(JUnit4.class)
public class UploadServletTest {
  
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private long currentTime = System.currentTimeMillis();

  private Query first_query;
  private Query second_query;
  private PreparedQuery results;

  private APKUploadServlet uploadServlet;

  private UserService userService;
  private DatastoreService datastore;
  private User user = new User("step2@gmail.com", "appspot.com", "1928364761813763");

  private final LocalServiceTestHelper helper = 
  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig())
  .setEnvIsLoggedIn(true);

  private Storage storage = LocalStorageHelper.getOptions().getService();


  @Before
  public void setUps() {

    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();
    userService = UserServiceFactory.getUserService();

  }


  @Before
  public void querySetups() {

    first_query = new Query("Vaderker");
    second_query = new Query(user.getUserId());

  }

  @Test
  public void userCredentialsVerification() {

    Assert.assertTrue(userService.isUserLoggedIn());
    Assert.assertEquals("step2@gmail.com", user.getEmail());
    Assert.assertEquals("1928364761813763", user.getUserId());
    Assert.assertEquals("appspot.com", user.getAuthDomain());
  }

  @Test
  public void checkIfEntitiesExistInDatastore() {

    Entity public_file = new Entity("Vaderker");
    public_file.setProperty("File_name", "HelloActivity.apk");
    public_file.setProperty("UserId", "1928364761813763");
    public_file.setProperty("Time", currentTime);

    datastore.put(public_file);

    Entity private_file = new Entity(user.getUserId());
    private_file.setProperty("File_name", "HelloActivity.apk");
    private_file.setProperty("UserId", "1928364761813763");
    private_file.setProperty("Time", currentTime);

    datastore.put(private_file);

    results = datastore.prepare(first_query);

    Entity retrieved_entity = new Entity("Hero");

    for (Entity entity : results.asIterable()) {retrieved_entity = entity;}

    Assert.assertEquals(1, results.countEntities(withLimit(3)));
    Assert.assertEquals(currentTime, (long) retrieved_entity.getProperty("Time"));
    Assert.assertEquals("1928364761813763", (String) retrieved_entity.getProperty("UserId"));
    Assert.assertEquals("HelloActivity.apk", (String) retrieved_entity.getProperty("File_name"));

  }

  @Test
  public void arePrivateAPKsUploaded() {

    Entity public_file = new Entity(user.getUserId());
    public_file.setProperty("File_name", "ApiDemos.apk");
    public_file.setProperty("UserId", "1928364761813763");
    public_file.setProperty("Time", currentTime);

    datastore.put(public_file);

    Entity private_file = new Entity(user.getUserId());
    private_file.setProperty("File_name", "Avalon.apk");
    private_file.setProperty("UserId", "1928364761813763");
    private_file.setProperty("Time", currentTime);

    datastore.put(private_file);

    results = datastore.prepare(second_query);
    int resultCount = 0;

    for (Entity entity : results.asIterable()) {resultCount++;}

    Assert.assertEquals(2, resultCount);

  }

  @Test
  public void uploadingFilesToCloudStorage() throws IOException {

    File file = new File(ClassLoader.getSystemClassLoader().getResource("HelloActivity.apk").getFile());

    BlobId blobId = BlobId.of(BUCKETNAME, "HelloActivity.apk");
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(file.toPath()));

    Assert.assertEquals("HelloActivity.apk", storage.get(blobId).getName());

  }

  @Test
  public void verifyingThatFilesAreTracked() throws Exception {

    uploadServlet = new APKUploadServlet();

    Method trackingFiles = APKUploadServlet.class.getDeclaredMethod("storeTrackedFiles", 
    String.class, String.class, DatastoreService.class, long.class, User.class);

    trackingFiles.setAccessible(true);

    trackingFiles.invoke(uploadServlet, "ApiDemos.apk", "Private", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "Football.apk", "Private", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "HelloActivity.apk", "Private", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "ContactManager.apk", "Private", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "PasswordTracker.apk", "Private", datastore, currentTime, user);

    trackingFiles.invoke(uploadServlet, "Avalon.apk", "Public", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "addresses.apk", "Public", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "rankings.apk", "Unknown", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "legends.apk", "Mendeleev", datastore, currentTime, user);
    trackingFiles.invoke(uploadServlet, "worldTour.apk", "hola_chronos", datastore, currentTime, user);

    results = datastore.prepare(first_query);
    PreparedQuery public_apks = datastore.prepare(second_query);

    Assert.assertEquals(5, results.countEntities(withLimit(6)));
    Assert.assertEquals(5, public_apks.countEntities(withLimit(6)));

  }

  @Test
  public void uploadingFilesInChunks() throws Exception {

    uploadServlet = new APKUploadServlet();

    InputStream testingFile = Thread.currentThread().getContextClassLoader()
    .getResourceAsStream("code_1.46.0-1591780013_amd64.deb");

    BlobId blobId = BlobId.of(BUCKETNAME, "code_1.46.0-1591780013_amd64.deb");
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

    Method fileStorage = APKUploadServlet.class.getDeclaredMethod("writeFilesToCloudStorage", 
    Storage.class, BlobInfo.class, InputStream.class);

    fileStorage.setAccessible(true);

    fileStorage.invoke(uploadServlet, storage, blobInfo, testingFile);
    
    Assert.assertEquals("code_1.46.0-1591780013_amd64.deb", storage.get(blobId).getName());

  }

  @After
  public void tearDowns() {helper.tearDown();}

}*/