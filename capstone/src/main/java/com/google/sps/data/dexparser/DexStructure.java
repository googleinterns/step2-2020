package com.google.sps.data.dexparser;

import java.nio.charset.StandardCharsets;

public class DexStructure {
	
	public static class HeaderItem {
	  private int headerSize;
	  private int fileSize;
	  private int endianTag;
	  private int stringIdsSize, stringIdsOffset;
	  private int typeIdsSize, typeIdsOffset;
	  private int protoIdsSize, protoIdsOffset;
	  private int fieldIdsSize, fieldIdsOffset;
	  private int methodIdsSize, methodIdsOffset;
	  private int classDefsSize, classDefsOffset;
	
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

		public int getHeaderSize() {
			return headerSize;
		}

		public void setHeaderSize(int headerSize) {
			this.headerSize = headerSize;
		}

		public int getFileSize() {
			return fileSize;
		}

		public void setFileSize(int fileSize) {
			this.fileSize = fileSize;
		}

		public int getEndianTag() {
			return endianTag;
		}

		public void setEndianTag(int endianTag) {
			this.endianTag = endianTag;
		}

		public int getStringIdsSize() {
			return stringIdsSize;
		}

		public void setStringIdsSize(int stringIdsSize) {
			this.stringIdsSize = stringIdsSize;
		}

		public int getStringIdsOffset() {
			return stringIdsOffset;
		}

		public void setStringIdsOffset(int stringIdsOffset) {
			this.stringIdsOffset = stringIdsOffset;
		}

		public int getTypeIdsSize() {
			return typeIdsSize;
		}

		public void setTypeIdsSize(int typeIdsSize) {
			this.typeIdsSize = typeIdsSize;
		}

		public int getTypeIdsOffset() {
			return typeIdsOffset;
		}

		public void setTypeIdsOffset(int typeIdsOffset) {
			this.typeIdsOffset = typeIdsOffset;
		}

		public int getProtoIdsSize() {
			return protoIdsSize;
		}

		public void setProtoIdsSize(int protoIdsSize) {
			this.protoIdsSize = protoIdsSize;
		}

		public int getProtoIdsOffset() {
			return protoIdsOffset;
		}

		public void setProtoIdsOffset(int protoIdsOffset) {
			this.protoIdsOffset = protoIdsOffset;
		}

		public int getFieldIdsSize() {
			return fieldIdsSize;
		}

		public void setFieldIdsSize(int fieldIdsSize) {
			this.fieldIdsSize = fieldIdsSize;
		}

		public int getFieldIdsOffset() {
			return fieldIdsOffset;
		}

		public void setFieldIdsOffset(int fieldIdsOffset) {
			this.fieldIdsOffset = fieldIdsOffset;
		}

		public int getMethodIdsSize() {
			return methodIdsSize;
		}

		public void setMethodIdsSize(int methodIdsSize) {
			this.methodIdsSize = methodIdsSize;
		}

		public int getMethodIdsOffset() {
			return methodIdsOffset;
		}

		public void setMethodIdsOffset(int methodIdsOffset) {
			this.methodIdsOffset = methodIdsOffset;
		}

		public int getClassDefsSize() {
			return classDefsSize;
		}

		public void setClassDefsSize(int classDefsSize) {
			this.classDefsSize = classDefsSize;
		}

		public int getClassDefsOffset() {
			return classDefsOffset;
		}

		public void setClassDefsOffset(int classDefsOffset) {
			this.classDefsOffset = classDefsOffset;
		} 

  }   

}