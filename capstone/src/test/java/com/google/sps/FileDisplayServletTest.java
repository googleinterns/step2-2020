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

import java.util.ArrayList;
import java.util.Arrays;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;


@RunWith(JUnit4.class)
public class FileDisplayServletTest {
  
  private PreparedQuery results;

  private FileDisplayServlet fileDisplay;

  private DatastoreService datastore;

  private final LocalServiceTestHelper helper = 
  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUps() {

    // This function sets ups local versions
    // of datastore for testing
    // purposes.
    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();

  }

  /*
   *Tests that only one APK features is retrieved from Datastore
   */
  @Test
  public void retrievalOfSingleApkFeature() {

    Entity testEntity = new Entity("UserFileFeature");
    testEntity.setProperty("FileName", "TestFile.apk");
    testEntity.setProperty("UserId", "93884584564745785");
    testEntity.setProperty("Res_File_Size", new ArrayList<Long>(Arrays.asList(2000L, 1600L)));
    testEntity.setProperty("Dex_File_Size", new ArrayList<Long>(Arrays.asList(500L, 490L)));
    testEntity.setProperty("Lib_File_Size", new ArrayList<Long>(Arrays.asList(4000L, 2000L)));
    testEntity.setProperty("Assets_File_Size", new ArrayList<Long>(Arrays.asList(590L, 340L)));
    testEntity.setProperty("Resources_File_Size", new ArrayList<Long>(Arrays.asList(200L, 100L)));
    testEntity.setProperty("Misc_File_Size", new ArrayList<Long>(Arrays.asList(1090L, 590L)));
    testEntity.setProperty("Total_Apk_size", 10000L);
    testEntity.setProperty("Files_Count", 911L);
    testEntity.setProperty("Apk_Disk_Size", 11000L);
    testEntity.setProperty("Apk_Lost_Size", 1000L);
    testEntity.setProperty("Timestamp", 1596473671630L);

    datastore.put(testEntity);
    
    fileDisplay = new FileDisplayServlet();
    
    results = fileDisplay.retrieveFileFeatures("TestFile.apk", 1596473671630L, datastore);

    int resultCount = 0;

    for (Entity entity : results.asIterable()) {
      resultCount++;
    }

    Assert.assertEquals(1, resultCount);

  }

  
  @After
  public void tearDowns() {
    helper.tearDown();
  }

}