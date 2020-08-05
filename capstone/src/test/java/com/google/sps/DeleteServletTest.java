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
public class DeleteServletTest {
  
  private final String BUCKETNAME = "vaderker-uploadedstoragebucket";

  private long currentTime = System.currentTimeMillis();

  private Query public_apks_query;
  private Query private_apks_query;
  private Query unzipped_apks_query;

  private PreparedQuery results;

  private Method trackedEntityDeletion;
  private Method unzippedEntityDeletion;

  private FileDeletionServlet deleteServlet;

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
  public void initializeFileRetrievalMethods() throws Exception {

    deleteServlet = new FileDeletionServlet();

    trackedEntityDeletion = FileDeletionServlet.class.getDeclaredMethod("deleteTrackedFile", 
    DatastoreService.class, String.class, String.class, String.class);
    unzippedEntityDeletion = FileDeletionServlet.class.getDeclaredMethod("deleteUnzippedApk", 
    DatastoreService.class, String.class, String.class);

    trackedEntityDeletion.setAccessible(true);
    unzippedEntityDeletion.setAccessible(true);

  }


  @Before
  public void querySetups() {

    public_apks_query = new Query("Vaderker");
    private_apks_query = new Query(user.getUserId());
    unzipped_apks_query = new Query("UserFileFeature");

  }

  @Test
  public void userCredentialsVerification() {

    Assert.assertTrue(userService.isUserLoggedIn());
    Assert.assertEquals("step2@gmail.com", user.getEmail());
    Assert.assertEquals("1928364761813763", user.getUserId());
    Assert.assertEquals("appspot.com", user.getAuthDomain());
  }

  @Test
  public void verifyThatFilesAreUploaded() throws Exception {

    // This function verifies that binary file are being uploaded
    // so the next test can confidently call the delete method
    // knowing that the file exists.
    File file = new File(ClassLoader.getSystemClassLoader().getResource("HelloActivity.apk").getFile());

    BlobId blobId = BlobId.of(BUCKETNAME, "HelloActivity.apk");
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(file.toPath()));

    Assert.assertEquals("HelloActivity.apk", storage.get(blobId).getName());

  }

  @Test
  public void verifyThatFilesAreDeleted() throws Exception {

    // This function simulates a situation where the
    // delete button is clicked and is testing how the servlet will respond.
    File file = new File(ClassLoader.getSystemClassLoader().getResource("HelloActivity.apk").getFile());

    BlobId blobId = BlobId.of(BUCKETNAME, "HelloActivity.apk");
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(file.toPath()));

    Entity private_file = new Entity(user.getUserId());
    private_file.setProperty("FileName", "HelloActivity.apk");
    private_file.setProperty("UserId", user.getUserId());
    private_file.setProperty("Time", currentTime);

    datastore.put(private_file);

    Entity unzippedAPK = new Entity("UserFileFeature");
    unzippedAPK.setProperty("FileName", "HelloActivity.apk");
    unzippedAPK.setProperty("UserId", user.getUserId());
    unzippedAPK.setProperty("Time", currentTime);

    datastore.put(unzippedAPK);

    trackedEntityDeletion.invoke(deleteServlet, datastore, "HelloActivity.apk", user.getUserId(), "true");

    Assert.assertTrue(storage.delete(blobId));
    Assert.assertEquals(0, datastore.prepare(private_apks_query).countEntities(withLimit(6)));
    Assert.assertTrue((boolean) unzippedEntityDeletion.invoke(deleteServlet, datastore, "HelloActivity.apk", user.getUserId()));

  }

  @Test
  public void deletingAllEntities() throws Exception {

    // This function is testing the delete servlet's
    // ability to verify if the delete request is authentic
    // before taking action.
    Entity public_file1 = new Entity("Vaderker");
    public_file1.setProperty("FileName", "HelloActivity.apk");
    public_file1.setProperty("UserId", "93884584564745785");
    public_file1.setProperty("Time", currentTime);

    datastore.put(public_file1);

    Entity public_file2 = new Entity("Vaderker");
    public_file2.setProperty("FileName", "Avalon.apk");
    public_file2.setProperty("UserId", "5566495842542584");
    public_file2.setProperty("Time", currentTime);

    datastore.put(public_file2);

    Entity public_file3 = new Entity(user.getUserId());
    public_file3.setProperty("FileName", "Phylum.apk");
    public_file3.setProperty("UserId", user.getUserId());
    public_file3.setProperty("Time", currentTime);

    datastore.put(public_file3);

    Entity public_file4 = new Entity("Vaderker");
    public_file4.setProperty("FileName", "Kingdoms.apk");
    public_file4.setProperty("UserId", "4295948459425854");
    public_file4.setProperty("Time", currentTime);

    datastore.put(public_file4);

    Entity unzippedAPK = new Entity("UserFileFeature");
    unzippedAPK.setProperty("FileName", "Phylum.apk");
    unzippedAPK.setProperty("UserId", user.getUserId());
    unzippedAPK.setProperty("Time", currentTime);

    datastore.put(unzippedAPK);

    Entity unzipped_apk = new Entity("UserFileFeature");
    unzipped_apk.setProperty("FileName", "Kingdoms.apk");
    unzipped_apk.setProperty("UserId", "4295948459425854");
    unzipped_apk.setProperty("Time", currentTime);

    datastore.put(unzipped_apk);

    trackedEntityDeletion.invoke(deleteServlet, datastore, "Phylum.apk", user.getUserId(), "true");
    trackedEntityDeletion.invoke(deleteServlet, datastore, "Avalon.apk", "5566495842542584", "false");
    trackedEntityDeletion.invoke(deleteServlet, datastore, "Kingdoms.apk", "4295948459425854", "false");
    trackedEntityDeletion.invoke(deleteServlet, datastore, "HelloActivity.apk", "93884584564745785", "false");

    Assert.assertFalse((boolean) unzippedEntityDeletion.invoke(deleteServlet, datastore, "Phylum.apk", "4295948459425854"));
    Assert.assertTrue((boolean) unzippedEntityDeletion.invoke(deleteServlet, datastore, "Phylum.apk", user.getUserId()));
    Assert.assertFalse((boolean) unzippedEntityDeletion.invoke(deleteServlet, datastore, "Kingdoms.apk", "85425758352584589"));
    Assert.assertTrue((boolean) unzippedEntityDeletion.invoke(deleteServlet, datastore, "Kingdoms.apk", "4295948459425854"));

    Assert.assertEquals(0, datastore.prepare(public_apks_query).countEntities(withLimit(6)));
    Assert.assertEquals(0, datastore.prepare(private_apks_query).countEntities(withLimit(6)));
    Assert.assertEquals(0, datastore.prepare(unzipped_apks_query).countEntities(withLimit(6)));
    
  }

  @After
  public void tearDowns() {helper.tearDown();}

}