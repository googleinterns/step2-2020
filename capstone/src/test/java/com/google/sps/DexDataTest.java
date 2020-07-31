package com.google.sps;

import com.google.sps.data.dexparser.DexData;

import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;

import java.io.RandomAccessFile;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DexDataTest {
  
  /*
   * Tests that DEX magic number is verified with the valid DEX magic
   */
  @Test
  public void verifyMagicNumberThatExists() {
    
    byte[] DEX_FILE_MAGIC_v037 = 
	    "dex\n037\0".getBytes(StandardCharsets.US_ASCII);

    DexData dexData = new DexData(); 

    Assert.assertTrue(dexData.verifyMagicNumber(DEX_FILE_MAGIC_v037));


  }

  /*
   * Tests that DEX magic number is verified with the invalid DEX magic
   */
  @Test
  public void verifyMagicNumberThatDoesNotExists() {
    byte[] DEX_FILE_MAGIC_FAKE = 
      "dex\n010\0".getBytes(StandardCharsets.US_ASCII);

    DexData dexData = new DexData(); 

    Assert.assertFalse(dexData.verifyMagicNumber(DEX_FILE_MAGIC_FAKE));
    

  }

  
}
