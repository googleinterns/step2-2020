package com.google.sps;

import com.google.sps.data.dexparser.DexLoader;

import com.google.common.io.Resources;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DexLoaderTest {
  
  /*
   * Tests that loading of DEX parser is successful with correct file path
   */
  @Test
  public void analyzeApkFeaturesLocallyTestWithCorrectFilePath() {

    DexLoader dexLoader = new DexLoader();

    // Gets the file path for each APK
    String filePath = Resources.getResource("HelloActivity.apk").getPath();
    
    boolean result = dexLoader.analyzeApkFeaturesLocally(filePath);

    Assert.assertTrue(result);

  }

  /*
   * Tests that loading of DEX parser is unsuccessful with incorrect file path
   */
  @Test
  public void analyzeApkFeaturesLocallyTestWithIncorrectFilePath() {

    DexLoader dexLoader = new DexLoader();

    // Using a fake path to test the DEX Loader
    String fakeFilePath = "/path/to/FileThatDoesNotExist.apk";
    boolean result = dexLoader.analyzeApkFeaturesLocally(fakeFilePath);

    Assert.assertFalse(result);

  }

}