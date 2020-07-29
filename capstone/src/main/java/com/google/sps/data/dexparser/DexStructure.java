package com.google.sps.data.dexparser;

import java.nio.charset.StandardCharsets;

public class DexStructure {
	
	static class HeaderItem {
	  public int headerSize;
	  public int fileSize;
	  public int endianTag;
	  public int stringIdsSize, stringIdsOffset;
	  public int typeIdsSize, typeIdsOffset;
	  public int protoIdsSize, protoIdsOffset;
	  public int fieldIdsSize, fieldIdsOffset;
	  public int methodIdsSize, methodIdsOffset;
	  public int classDefsSize, classDefsOffset;
	
	  /*Dex magic values expected for certain versions*/
	
	  // Dex file magic number for API LEVEL 13 and earlier
	  public static final byte[] DEX_FILE_MAGIC_v035 = 
	    "dex\n035\0".getBytes(StandardCharsets.US_ASCII);
	
	  // Dex file magic number for API LEVEL 24
	  public static final byte[] DEX_FILE_MAGIC_v037 = 
	    "dex\n037\0".getBytes(StandardCharsets.US_ASCII);
	  
	  // Dex file magic number for API LEVEL 26
	  public static final byte[] DEX_FILE_MAGIC_v038 = 
	    "dex\n038\0".getBytes(StandardCharsets.US_ASCII);
	   
	  // Dex file magic number for API LEVEL 28 
	  public static final byte[] DEX_FILE_MAGIC_v039 = 
	    "dex\n039\0".getBytes(StandardCharsets.US_ASCII);
	  
	  //values used to indicate endianness of file contents
	  public static final int ENDIAN_CONSTANT = 0x12345678;
	  
	  public static final int REVERSE_ENDIAN_CONSTANT = 0x78563412;
	}
	
	/*
  * Holds the contents of a type_id_item.
  *
  * This is chiefly a list of indices into the string table.  We need
  * some additional bits of data, such as whether or not the type ID
  * represents a class defined in this DEX, so we use an object for
  * each instead of a simple integer.  
  */
  static class TypeIdItem {
    public int descriptorIdx;       // index into string_ids
    public boolean internal;        // checks if its defined within this DEX file
  }
    

}