package com.google.sps;

import com.google.sps.data.dexparser.DexFeaturesUpdate;
import com.google.sps.data.dexparser.DexStructure.HeaderItem;

import com.google.appengine.api.datastore.Entity;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DexFeaturesUpdateTest {
  
  private final LocalServiceTestHelper helper = 
  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


  @Before
  public void setUps() {

    helper.setUp();

  }

  /*
   * Tests that creation of an entity with given parameters is successful
   */
  @Test
  public void updateDexStatsTest() {

    HeaderItem mHeaderItem = new HeaderItem();
    mHeaderItem.setHeaderSize(112);
    mHeaderItem.setFileSize(1000);
    mHeaderItem.setStringIdsSize(5200);
    mHeaderItem.setTypeIdsSize(290);
    mHeaderItem.setProtoIdsSize(1009);
    mHeaderItem.setFieldIdsSize(460);
    mHeaderItem.setMethodIdsSize(590);
    mHeaderItem.setClassDefsSize(3900);

    String userId = "1928364761813763";
    long timeStamp = 1596473671630L;
    String fileName = "TestFile.apk";

    DexFeaturesUpdate dexUpdate = new DexFeaturesUpdate();
    Entity testEntity = dexUpdate.updateDexStats(mHeaderItem, userId, timeStamp, fileName);

    // Compares each property in the entity since comparing entity objects will always fail due to different keys
    Assert.assertEquals(testEntity.getProperty("UserId"),"1928364761813763");
    Assert.assertEquals(testEntity.getProperty("FileName"), "TestFile.apk");
    Assert.assertEquals(testEntity.getProperty("Timestamp"), 1596473671630L);
    Assert.assertEquals(testEntity.getProperty("Header_Size"), mHeaderItem.getHeaderSize());
    Assert.assertEquals(testEntity.getProperty("File_Size"), mHeaderItem.getFileSize());
    Assert.assertEquals(testEntity.getProperty("StringIdsSize"), mHeaderItem.getStringIdsSize());
    Assert.assertEquals(testEntity.getProperty("TypeIdsSize"), mHeaderItem.getTypeIdsSize());
    Assert.assertEquals(testEntity.getProperty("ProtoIdsSize"), mHeaderItem.getProtoIdsSize());
    Assert.assertEquals(testEntity.getProperty("FieldIdsSize"), mHeaderItem.getFieldIdsSize());
    Assert.assertEquals(testEntity.getProperty("MethodIdsSize"), mHeaderItem.getMethodIdsSize());
    Assert.assertEquals(testEntity.getProperty("ClassDefsSize"), mHeaderItem.getClassDefsSize());

  }

  @After
  public void tearDowns() {
    helper.tearDown();
  }
}
