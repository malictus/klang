/**
 * This file is distributed under a BSD-style license. See the included LICENSE.txt file
 * for more information. 
 * Copyright (c) 2009, James Halliday
 * All rights reserved.
 */
package malictus.klang.chunk;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import malictus.klang.KlangConstants;
import malictus.klang.KlangUtil;
import malictus.klang.primitives.*;
import malictus.klang.file.*;

/**
 * This class represents the RIFF container chunk for a RIFF file (such as WAV).
 * It also represents LIST chunks, since they work exactly the same way.
 * @author Jim Halliday
 */
public class RIFFChunk extends ContainerChunk implements EditableContainerChunk {

	public RIFFChunk(KlangFile parentFile, long startpos, long endpos, long datastartpos, long dataendpos, int chunkType, String chunkName, ContainerChunk parentChunk, Boolean isBigEndian, RandomAccessFile raf) throws IOException {
		super(parentFile, startpos, endpos, datastartpos, dataendpos, chunkType, chunkName, parentChunk, isBigEndian, raf);
		this.usesPadByte = true;
		raf.seek(datastartpos);
		/*
		 * LOAD PRIMITIVE OBJECTS (UNINITIALIZED)
		 */
		FourCC name = new FourCC();
		PrimitiveData nameData;
		/*
		 * READ FROM FILE TO FILL PRIMITIVE DATA
		 */
		if (endpos >= raf.getFilePointer() + 4) {
			try {
				long bytepos = raf.getFilePointer();
				name.setValueFromFile(raf);
				nameData = new PrimitiveData(name, KlangConstants.PRIMITIVE_DATA_RIFF_CHUNK_NAME, bytepos, raf.getFilePointer());
			} catch (BadValueException er) {
				nameData = new PrimitiveData(name, KlangConstants.PRIMITIVE_DATA_RIFF_CHUNK_NAME, null, null);
			}
		} else {
			nameData = new PrimitiveData(name, KlangConstants.PRIMITIVE_DATA_RIFF_CHUNK_NAME, null, null);
		}
		/*
		 * ADD PRIMITIVES TO VECTOR
		 */
		this.primitives.add(nameData);
		/*
		 * ADD SUBCHUNKS
		 */
		raf.seek(datastartpos + 4);
		long pointer = raf.getFilePointer();
		while (pointer < dataendpos) {
    		Chunk cChunk = ChunkFactory.createChunk(parentFile, pointer, KlangConstants.CHUNKTYPE_RIFF, this, raf, isBigEndian, null, null);
    		this.chunks.add(cChunk);
    		pointer = cChunk.getEndPosition();
    	}
	}
	
	public void addChunkFromFile(File theFile, String name, int position, RandomAccessFile raff) throws IOException {
		if (!(this.getParentFile() instanceof EditableFileBase)) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
		long newsize = (this.dataEndPosition - this.dataStartPosition) + theFile.length() + 8;
		boolean addPad = false;
		if (KlangUtil.adjustForPadByte(theFile.length()) != theFile.length()) {
			addPad = true;
			newsize = newsize + 1;
		}
		if (newsize < 0) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
		try {
			//first write new file size
			Primitive size;
			if (isBigEndian) {
				size = new Int4ByteUnsignedBE();
			} else {
				size = new Int4ByteUnsignedLE();
			}
			size.setValueFromString(Long.toString(newsize));
			raff.seek(startPosition + 4);
			size.writeValueToFile(raff);
			//now warn parent chunks
			this.chunkIsAboutToChangeSize(newsize, raff);
			//now make the addition
			long startingplace = 0;
			if (position > this.getSubChunks().size() + 2) {
				position = this.getSubChunks().size() + 1;
			}
			if (position == this.getSubChunks().size() + 1) {
				if (this.getSubChunks().size() == 0) {
					startingplace = this.endPosition;
				} else {
					startingplace = this.getSubChunks().get(position - 2).getEndPosition();
				}
			} else {
				startingplace = this.getSubChunks().get(position - 1).getStartPosition();
			}
			//insert file itself
			KlangUtil.insertIntoFile(theFile, this.getParentFile().getFile(), startingplace);
			if (addPad) {
				//add pad byte
				KlangUtil.insertIntoFile(new byte[1], this.getParentFile().getFile(), startingplace + theFile.length());
			}
			//insert name and size
			KlangUtil.insertIntoFile(new byte[8], this.getParentFile().getFile(), startingplace);
			raff.seek(startingplace);
			FourCC fcc = new FourCC(name);
			fcc.writeValueToFile(raff);
			
			Primitive chunksize;
			if (isBigEndian) {
				chunksize = new Int4ByteUnsignedBE();
			} else {
				chunksize = new Int4ByteUnsignedLE();
			}
			chunksize.setValueFromString(Long.toString(theFile.length()));
			chunksize.writeValueToFile(raff);
			//now update and reparse
			((EditableFileBase)this.getParentFile()).reparseFile(raff);
			this.chunkJustChanged(raff);
		} catch (BadValueException err) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
	}
	
	public void addChunkFromArray(byte[] bytes, String name, int position, RandomAccessFile raff) throws IOException {
		if (!(this.getParentFile() instanceof EditableFileBase)) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
		long newsize = (this.dataEndPosition - this.dataStartPosition) + bytes.length + 8;
		if (KlangUtil.adjustForPadByte(bytes.length) != bytes.length) {
			//grow byte array by 1
			newsize = newsize + 1;
			byte[] newBytes = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
			bytes = newBytes;
		}
		if (newsize < 0) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
		try {
			//first write new file size
			Primitive size;
			if (isBigEndian) {
				size = new Int4ByteUnsignedBE();
			} else {
				size = new Int4ByteUnsignedLE();
			}
			size.setValueFromString(Long.toString(newsize));
			raff.seek(startPosition + 4);
			size.writeValueToFile(raff);
			//now warn parent chunks
			this.chunkIsAboutToChangeSize(newsize, raff);
			//now make the addition
			long startingplace = 0;
			if (position > this.getSubChunks().size() + 2) {
				position = this.getSubChunks().size() + 1;
			}
			if (position == this.getSubChunks().size() + 1) {
				if (this.getSubChunks().size() == 0) {
					startingplace = this.endPosition;
				} else {
					startingplace = this.getSubChunks().get(position - 2).getEndPosition();
				}
			} else {
				startingplace = this.getSubChunks().get(position - 1).getStartPosition();
			}
			KlangUtil.insertIntoFile(bytes, this.getParentFile().getFile(), startingplace);
			//insert name and size
			KlangUtil.insertIntoFile(new byte[8], this.getParentFile().getFile(), startingplace);
			raff.seek(startingplace);
			long pos = raff.getFilePointer();
			FourCC fcc = new FourCC(name);
			fcc.writeValueToFile(raff);
			Primitive chunksize;
			if (isBigEndian) {
				chunksize = new Int4ByteUnsignedBE();
			} else {
				chunksize = new Int4ByteUnsignedLE();
			}
			chunksize.setValueFromString(Long.toString(bytes.length));
			chunksize.writeValueToFile(raff);
			//now update and reparse
			((EditableFileBase)this.getParentFile()).reparseFile(raff);
			Chunk c = KlangFile.findDeepestChunkFor(this.getParentFile().getChunks(), pos);
			c.chunkJustChanged(raff);
		} catch (BadValueException err) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
	}
	
	public void subChunkWillChangeSize(Chunk chunk, long newsize, RandomAccessFile raf) throws IOException {
		try {
			long newDsize = newsize - 8;
			if (newDsize >= 0) {
				//write size var to chunk itself
				Primitive subCsize;
				if (isBigEndian) {
					subCsize = new Int4ByteUnsignedBE();
				} else {
					subCsize = new Int4ByteUnsignedLE();
				}	
				subCsize.setValueFromString(Long.toString(newDsize));
				raf.seek(chunk.getStartPosition() + 4);
				subCsize.writeValueToFile(raf);			
			}
			//now write this chunk's size variable
			Primitive size;
			if (isBigEndian) {
				size = new Int4ByteUnsignedBE();
			} else {
				size = new Int4ByteUnsignedLE();
			}	
			long sizevar = chunk.getEndPosition() - chunk.getStartPosition();
			//sizevar may be negative now
			sizevar = newsize - sizevar;
			long cursize = this.getEndPosition() - this.getStartPosition();
			size.setValueFromString(Long.toString(cursize + sizevar - 8));
			raf.seek(startPosition + 4);
			size.writeValueToFile(raf);
		} catch (BadValueException err) {
			err.printStackTrace();
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
	}
	
	public void subChunkHasChanged(Chunk chunk, RandomAccessFile raf) throws IOException {
		//for RIFF chunks, do nothing
	}
	
	public void editSubChunkName(Chunk chunk, String newName, RandomAccessFile raff) throws IOException {
		try {
			raff.seek(chunk.getStartPosition());
			FourCC fcc = new FourCC();
			fcc.setValueFromString(newName);
			fcc.writeValueToFile(raff);
		} catch (BadValueException err) {
			throw new IOException(KlangConstants.ERROR_GENERAL_FILE);
		}
	}
	
}
