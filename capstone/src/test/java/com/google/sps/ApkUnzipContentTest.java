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

package com.google.sps;

import com.google.sps.data.ApkUnzipContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ApkUnzipContentTest {
  // Test scenarios that can occur while mapping file types and file names in the ApkUnzipContent

  @Test
  public void testApkDataToMapEntryStorageWhenFileTypeMappingIsEmpty() {
    ApkUnzipContent unzipContent = new ApkUnzipContent();
    HashMap<String, ArrayList<Long>> apkPackageMapTest = new HashMap<String, ArrayList<Long>>();
    apkPackageMapTest.put("res", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("dex", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("assets", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("lib", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("arsc", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("misc", new ArrayList<Long>(Arrays.asList(0L, 0L)));


    Assert.assertEquals(apkPackageMapTest, unzipContent.getApkPackageContentMap());
  }

  @Test
  public void testApkDataToMapEntryStorageForOneFileType() {
    ApkUnzipContent unzipContent = new ApkUnzipContent();
    
    unzipContent.addApkDataToMapStorage("res/file/name", "res", 679003L, 56000L);
    
    HashMap<String, ArrayList<Long>> apkPackageMapTest = new HashMap<String, ArrayList<Long>>();
    apkPackageMapTest.put("res", new ArrayList<Long>(Arrays.asList(679003L, 56000L)));
    apkPackageMapTest.put("dex", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("assets", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("lib", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("arsc", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("misc", new ArrayList<Long>(Arrays.asList(0L, 0L)));


    Assert.assertEquals(apkPackageMapTest, unzipContent.getApkPackageContentMap());
  }

  @Test
  public void testApkDataToMapEntryStorageForMoreThanOneFileType() {
    ApkUnzipContent unzipContent = new ApkUnzipContent();
    
    unzipContent.addApkDataToMapStorage("res/file/name", "res", 679003L, 56000L);
    unzipContent.addApkDataToMapStorage("assets/file/name", "assets", 203L, 100L);
    unzipContent.addApkDataToMapStorage("lib/file/name", "lib", 5703L, 5000L);

    HashMap<String, ArrayList<Long>> apkPackageMapTest = new HashMap<String, ArrayList<Long>>();
    apkPackageMapTest.put("res", new ArrayList<Long>(Arrays.asList(679003L, 56000L)));
    apkPackageMapTest.put("dex", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("assets", new ArrayList<Long>(Arrays.asList(203L, 100L)));
    apkPackageMapTest.put("lib", new ArrayList<Long>(Arrays.asList(5703L, 5000L)));
    apkPackageMapTest.put("arsc", new ArrayList<Long>(Arrays.asList(0L, 0L)));
    apkPackageMapTest.put("misc", new ArrayList<Long>(Arrays.asList(0L, 0L)));


    Assert.assertEquals(apkPackageMapTest, unzipContent.getApkPackageContentMap());
  }

  @Test
  public void testApkDataToMapEntryStorageForOneFileName() {
    ApkUnzipContent unzipContent = new ApkUnzipContent();
    
    unzipContent.addApkDataToMapStorage("res/file/name", "res", 679003L, 56000L);
    
    HashMap<String, ArrayList<Long>> apkFilesListMapTest = new HashMap<String, ArrayList<Long>>();
    apkFilesListMapTest.put("res/file/name", new ArrayList<Long>(Arrays.asList(679003L, 56000L)));
    

    Assert.assertEquals(apkFilesListMapTest, unzipContent.getApkFilesListMap());
  }

  @Test
  public void testApkDataToMapEntryStorageForMoreThanOneFileName() {
    ApkUnzipContent unzipContent = new ApkUnzipContent();
    
    unzipContent.addApkDataToMapStorage("res/file/name", "res", 679003L, 56000L);
    unzipContent.addApkDataToMapStorage("assets/file/name", "assets", 9003L, 6500L);
    
    HashMap<String, ArrayList<Long>> apkFilesListMapTest = new HashMap<String, ArrayList<Long>>();
    apkFilesListMapTest.put("res/file/name", new ArrayList<Long>(Arrays.asList(679003L, 56000L)));
    apkFilesListMapTest.put("assets/file/name", new ArrayList<Long>(Arrays.asList(9003L, 6500L)));
    

    Assert.assertEquals(apkFilesListMapTest, unzipContent.getApkFilesListMap());
  }
}