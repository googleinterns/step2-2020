package com.google.sps.data.dexparser;

import com.google.sps.data.dexparser.DexStructure.HeaderItem;

import com.google.appengine.api.datastore.Entity;

public class DexFeaturesUpdate {

  public Long timeStamp, headerSize, fileSize, stringIdsSize, typeIdsSize, protoIdsSize, fieldIdsSize, methodIdsSize, classDefsSize;
  public String fileName, userId;

  public DexFeaturesUpdate() {

  }

  // Create a class for statistics from DEX file once data has been retreived from Datastore
  public DexFeaturesUpdate(Entity entity) {
    this.timeStamp = (long) entity.getProperty("Timestamp");
    this.fileName = (String) entity.getProperty("FileName");
    this.userId = (String) entity.getProperty("UserId");
    this.headerSize = (long) entity.getProperty("Header_Size");
    this.fileSize = (long) entity.getProperty("File_Size");
    this.stringIdsSize = (long) entity.getProperty("StringIdsSize");
    this.typeIdsSize = (long) entity.getProperty("TypeIdsSize");
    this.protoIdsSize = (long) entity.getProperty("ProtoIdsSize");
    this.fieldIdsSize = (long) entity.getProperty("FieldIdsSize");
    this.methodIdsSize = (long) entity.getProperty("MethodIdsSize");
    this.classDefsSize = (long) entity.getProperty("ClassDefsSize");

  }

  // Returns an entity for statistics processed from DEX file 
  public Entity updateDexStats(HeaderItem mHeaderItem, String userId, Long timeStamp, String fileName) {
    int headerSize = mHeaderItem.getHeaderSize();
    int fileSize = mHeaderItem.getFileSize();
    int stringIdsSize = mHeaderItem.getStringIdsSize();
    int typeIdsSize = mHeaderItem.getTypeIdsSize();
    int protoIdsSize =  mHeaderItem.getProtoIdsSize();
    int fieldIdsSize = mHeaderItem.getFieldIdsSize();
    int methodIdsSize = mHeaderItem.getMethodIdsSize();
    int classDefsSize = mHeaderItem.getClassDefsSize();

    Entity fileEntity = new Entity("UserDexFeature");
    fileEntity.setProperty("UserId", userId);
    fileEntity.setProperty("FileName", fileName);
    fileEntity.setProperty("Timestamp", timeStamp);
    fileEntity.setProperty("Header_Size", headerSize);
    fileEntity.setProperty("File_Size", fileSize);
    fileEntity.setProperty("StringIdsSize", stringIdsSize);
    fileEntity.setProperty("TypeIdsSize", typeIdsSize);
    fileEntity.setProperty("ProtoIdsSize", protoIdsSize);
    fileEntity.setProperty("FieldIdsSize", fieldIdsSize);
    fileEntity.setProperty("MethodIdsSize", methodIdsSize);
    fileEntity.setProperty("ClassDefsSize", classDefsSize);

    return fileEntity;
    
  }


}