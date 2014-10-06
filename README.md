klang
=====

Klang is a binary file editor which can open and view any file, but it specifically designed to read structued binary data in files such as WAV, AVI, and PNG. When editing one of the known file types, the program allows viewing of the hierarchical 'chunks' inside the file, as well as editing of certain chunks.

Klang was written in Java in 2009, and, while it works, it covers only a handful of content types and chunk types. I might get back to it one day, but in the meantime I'm moving it over to GitHub for safekeeping.

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


