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

import com.google.sps.data.ApkFileTypeFilter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ApkFileTypeFilterTest {
  // Test scenarios that can occur while using the ApkFileTypeFilter

  @Test
  public void testApkFileTypeFilterForResFiles() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("res/layouts");

    Assert.assertEquals("res", expectedFileType);
  }

  @Test
  public void testApkFileTypeFilterForDexFiles() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("classes.dex");

    Assert.assertEquals("dex", expectedFileType);
  }

  @Test
  public void testApkFileTypeFilterForAssetFiles() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("assets/main");

    Assert.assertEquals("assets", expectedFileType);
  }

  @Test
  public void testApkFileTypeFilterForLibFiles() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("lib/meta");

    Assert.assertEquals("lib", expectedFileType);
  }

  @Test
  public void testApkFileTypeFilterForResourceFiles() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("resources.arsc");

    Assert.assertEquals("arsc", expectedFileType);
  }

  @Test
  public void testApkFileTypeFilterForMiscFilesOrOtherFileNames() {
    ApkFileTypeFilter fileTypeFilter = new ApkFileTypeFilter();

    String expectedFileType = fileTypeFilter.getApkFileType("Manifest.xml");

    Assert.assertEquals("misc", expectedFileType);
  }

}