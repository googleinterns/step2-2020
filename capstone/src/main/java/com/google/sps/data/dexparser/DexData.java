package com.google.sps.data.dexparser;

import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.logging.Level; 
import java.util.logging.Logger; 

import com.google.sps.data.dexparser.DexStructure.HeaderItem;


public class DexData {

  private static final Logger LOGGER = Logger.getLogger(DexData.class.getName());

	private RandomAccessFile mDexFile;
	private HeaderItem mHeaderItem;

  public HeaderItem getHeaderItem() {
    return mHeaderItem;
  }
	
	/*
   * For comparing order of bytes in DEX file
   */
  private byte[] tmpBuf = new byte[4];
	private ByteOrder mByteOrder = ByteOrder.LITTLE_ENDIAN;


  /* 
   * Constructors for DexData
   */
  public DexData() {

  }

	public DexData(RandomAccessFile raf) {
		mDexFile = raf;
	}

	/*
	 * Load the contents of DEX files into data structures.
	 * 
	 * @throws IOException if we encounter a problem while reading
	 * @throws DataFormatException if the DEX contents look bad
	 */
	public void load() throws IOException, DataFormatException {
		parseHeaderItem();
		
	}
	
	
	/*
	 * Checks that the magic number is valid
	 */
	public boolean verifyMagicNumber(byte[] magicNumber) {
		return Arrays.equals(magicNumber, HeaderItem.DEX_FILE_MAGIC_v035)
				|| Arrays.equals(magicNumber, HeaderItem.DEX_FILE_MAGIC_v037)
				|| Arrays.equals(magicNumber, HeaderItem.DEX_FILE_MAGIC_v038)
				|| Arrays.equals(magicNumber, HeaderItem.DEX_FILE_MAGIC_v039);
	}

	public void parseHeaderItem() throws IOException, DataFormatException {
		mHeaderItem = new DexStructure.HeaderItem();

		seek(0);

		byte[] magicNumber = new byte[8];

    try {
      mDexFile.readFully(magicNumber);
    } catch (EOFException e) {
      System.out.println("");
      LOGGER.info("End of file reached");
    } catch (IOException e) {}

		// Checks the magic number
		if (!verifyMagicNumber(magicNumber)) {
			LOGGER.info("Magic number is wrong. Are you sure this is a dex file?");
			throw new DataFormatException();
		} else {
      LOGGER.info("Magic number has been verified.");
		}

		/*
		 * Skip the magic number, checksum, signature, file size, header size and place
		 * pointer at the beginning of endian tag to get endianess. Offset: 40
		 */
		seek(8 + 4 + 20 + 4 + 4);

		mHeaderItem.setEndianTag(readInt());
		LOGGER.info("Endian Tag: " + mHeaderItem.getEndianTag());

		if (mHeaderItem.getEndianTag() == HeaderItem.ENDIAN_CONSTANT) {
      LOGGER.info("DEX file is based on Little Endian.");
		} else if (mHeaderItem.getEndianTag() == HeaderItem.REVERSE_ENDIAN_CONSTANT) {
      LOGGER.info("DEX file is based on Big (Reverse) Endian.");
		} else {
      LOGGER.info("Endian Constant has unexpected value " + Integer.toHexString(mHeaderItem.getEndianTag()));
			throw new DataFormatException();
		}

		// set offset after magic, checksum, signature
		seek(8 + 4 + 20);
		ByteBuffer buffer = readByteBuffer(Integer.BYTES * 20);
		mHeaderItem.setFileSize(buffer.getInt());
		mHeaderItem.setHeaderSize(buffer.getInt());
		/*mHeaderItem.endianTag =*/ buffer.getInt(); // Don't have to read some of these ones
    /*mHeaderItem.inkSize =*/ buffer.getInt();
    /*mHeaderItem.linkOff =*/ buffer.getInt();
    /*mHeaderItem.mapOff =*/ buffer.getInt();
    mHeaderItem.setStringIdsSize(buffer.getInt());
    mHeaderItem.setStringIdsOffset(buffer.getInt());
    mHeaderItem.setTypeIdsSize(buffer.getInt());
    mHeaderItem.setTypeIdsOffset(buffer.getInt());
    mHeaderItem.setProtoIdsSize(buffer.getInt());
    mHeaderItem.setProtoIdsOffset(buffer.getInt());
    mHeaderItem.setFieldIdsSize(buffer.getInt());
    mHeaderItem.setFieldIdsOffset(buffer.getInt());
    mHeaderItem.setMethodIdsSize(buffer.getInt());
    mHeaderItem.setMethodIdsOffset(buffer.getInt());
    mHeaderItem.setClassDefsSize(buffer.getInt());
    mHeaderItem.setClassDefsOffset(buffer.getInt());
    /*mHeaderItem.dataSize =*/ buffer.getInt();
    /*mHeaderItem.dataOff =*/ buffer.getInt();

	}
	
	
	
  // ------- Helper functions for parsing DEX ----------

	/*
	 * Reads a signed 32-bit integer, byte-swapping if necessary.
	 */
	public int readInt() throws IOException {
		mDexFile.readFully(tmpBuf, 0, 4);
		if (mByteOrder == ByteOrder.BIG_ENDIAN) {
			return (tmpBuf[3] & 0xff) | ((tmpBuf[2] & 0xff) << 8) | ((tmpBuf[1] & 0xff) << 16)
					| ((tmpBuf[0] & 0xff) << 24);
		} else {
			return (tmpBuf[0] & 0xff) | ((tmpBuf[1] & 0xff) << 8) | ((tmpBuf[2] & 0xff) << 16)
					| ((tmpBuf[3] & 0xff) << 24);
		}
	}

	/*
	 * Sets the file-pointer offset, measured from the beginning of the file to the
	 * specified absolute solution.
	 */
	public void seek(int position) throws IOException {
		mDexFile.seek(position);
	}

	/*
	 * Reads bytes and transforms them into a ByteBuffer with the desired byte order
	 * set from which primitive values can be read.
	 */
	public ByteBuffer readByteBuffer(int size) throws IOException {
		byte[] bytes = new byte[size];
		mDexFile.read(bytes);
		return ByteBuffer.wrap(bytes).order(mByteOrder);
	}
	
	

}