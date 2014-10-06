klang
=====

Klang is a binary file editor with a full GUI which can open and view any file, but it specifically designed to read structued binary data in files such as WAV, AVI, and PNG. When editing one of the known file types, the program allows viewing of the hierarchical 'chunks' inside the file, as well as editing of certain chunks.

Klang was written in Java in 2009, and, while it works, it covers only a handful of content types and chunk types. I might get back to it one day, but in the meantime I'm moving it over to GitHub for safekeeping.

IMPORTANT: Since this program edits files directly it is important to BACK UP ALL FILES before
opening them with Klang!

Supported File Types
--------------------
* RIFF (including WAV, AVI, etc.)
* FORM/IFF (including AIFF)
* PNG 
* Plain Text (XML, XHTML, HTML, TXT, etc.)

Supported Chunk Types (Full editing)
------------------------------------
WAV Files
* FACT
* FMT
* IXML (as plain text only)
* AXML (as plain text only)
	
PNG Files
* IHDR
* PHYS
* SRGB
* GAMA
* CHRM
* TEXT
	
Generic IFF Chunks
* FORM
	
Generic RIFF Chunks
* [RIFF "INFO" chunks] - IARL, IART, ICMS, ICMT, ICOP, ICRD, ICRP, IDIM,IDPI, IENG, IGNR, IKEY, ILGT, IMED, INAM, IPLT, IPRD, ISBJ, ISFT, ISHP, ISRC, ISRF, ITCH
* LIST
* RIFF
* RIFX

Supported Chunk Types (Partial or no editing)
---------------------------------------------
AIFF Files
* COMM

WAV Files
* DATA
	
PNG Files
* IEND
* SBIT
* IDAT
* MKBF
* MKTS
* MKBS
* MKBT
* PRVW

How To Add A Primitive Types
----------------------------
* create a class in malictus.klang.primitives that implements Primitive and (optionally) implements PrimitiveInt and/or PrimitiveFixedByte
* add description to KlangConstants.getPrimitiveDescriptionFor()
* add a test to test.PrimitiveTest
* if the primitive is implements PrimitiveFixedByte, and you want it to appear in the raw edit panel combo box, add a value to KlangConstants.KLANGEDITOR_DISPLAY_PRIMS

Adding Chunk Types
------------------
* add a KlangConstants.CHUNKTYPE_xxx value
* add a description to KlangConstants.getChunkTypeDescriptionFor()
* modify ChunkFactory.createChunk() to correctly parse out data, and to create correct class

Adding Chunk Names
------------------
* add a KlangConstants.CHUNKNAME_xxx value
* add a description to KlangConstants.getChunkNameDescriptionFor()
* (if the chunk will be parsed to a new classs) add a class that represents the chunk
* (if chunk should be parsed) add to ChunkFactory.createChunk() to correctly identify chunk
* (if the chunk will be parsed to a new class) the class (1) should read primitives to the primitives vector (see Adding Primitive Data below), and (2) extend ContainerChunk if it contains subchunks, and (3) implement EditableContainerChunk if those chunks can be edited, and (4) overwrite the reparseChunkPrimitives() method if the chunk has a complex way of generating primitives

Adding Primitive Data for a Chunk
---------------------------------
* add a section in the chunk's constructor to create the PrimitiveData object
* add a unique string to KlangConstants.PRIMITIVE_DATA_xxx
* add a description of the data to KlangConstants.getPrimitiveDataDescriptionFor()
( if the primitive data has value strings associated with it, add an entry to KlangConstants.getValueStringsFor(), and a Hashtable named KlangConstants.VALUESTRINGS_xxx

Adding File Types
-----------------
* add a class to malictus.klang.file that should extend KlangFile and (optionally) implement EditableFileBase, or might possibly extend another file type instead (WAVFile extends RIFFFile, for example)
* add to method KlangFileFactory.makeNewKlangFile() to appropriately create new file object
* add a description to KlangConstants.getFileTypeDescriptionFor()
* add file extensions to KlangConstants.KLANG_SUPPORTED_FILE_EXTENSIONS

Rebuilding application
----------------------
- run the build.xml file to create two clickable jars, one with source and the other without source


