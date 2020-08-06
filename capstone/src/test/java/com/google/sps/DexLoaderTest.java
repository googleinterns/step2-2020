package com.google.sps;

import com.google.sps.data.dexparser.DexLoader;

import com.google.common.io.Resources;

import java.io.RandomAccessFile;

import java.util.ArrayList;

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
    
    
    ArrayList<RandomAccessFile> result = dexLoader.analyzeApkFeaturesLocally(filePath);

    Assert.assertFalse(result.isEmpty());

  }

  /*
   * Tests that loading of DEX parser is unsuccessful with incorrect file path
   */
  @Test
  public void analyzeApkFeaturesLocallyTestWithIncorrectFilePath() {

    DexLoader dexLoader = new DexLoader();

    // Using a fake path to test the DEX Loader
    String fakeFilePath = "/path/to/FileThatDoesNotExist.apk";
    ArrayList<RandomAccessFile> result = dexLoader.analyzeApkFeaturesLocally(fakeFilePath);

    Assert.assertTrue(result.isEmpty());

  }

  /*
   * Tests that loading of DEX parser for APK with only one DEX file is successful with correct file path
   */
  @Test
  public void analyzeApkFeaturesLocallyTestWithCorrectFilePathForOneDexFileInApk() {

    DexLoader dexLoader = new DexLoader();

    // Gets the file path for each APK
    String filePath = Resources.getResource("HelloActivity.apk").getPath();
    
    
    ArrayList<RandomAccessFile> result = dexLoader.analyzeApkFeaturesLocally(filePath);

    Assert.assertEquals(result.size(), 1);

  }

}
