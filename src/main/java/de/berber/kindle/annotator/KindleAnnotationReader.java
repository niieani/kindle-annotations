/*
 *
 * Copyright 2011, Bernhard J. Berger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.berber.kindle.annotator;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

/**
 * Reader for Kindle annotation files.
 *
 * @author Bernhard J. Berger
 */
public class KindleAnnotationReader {
	/**
	 * Magic file header value
	 */
	private static final int MAGIC_VALUE = 0xDEADCABB;

	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(KindleAnnotationReader.class);

	
	/**
	 * Source of annotations
	 */
	private final File pdrFile;

	/**
	 * PDR stream
	 */
	private FileInputStream fileStream;
	private DataInputStream pdrStream;
	
	private OutputStream debugStream = null;
	
	private final CompositeConfiguration cc;
	
	/**
	 * Creates a new annotation reader for the kindle device. 
	 * 
	 * @param pdfFile The pdf file you want to read annotations for.
	 */
	public KindleAnnotationReader(final CompositeConfiguration cc, final File pdfFile) {
		pdrFile = new File(pdfFile.toString().substring(0, pdfFile.toString().length() - 1) + "r");
		this.cc = cc;

		if(!pdrFile.exists()) {
			LOG.error("Cannot find pdr-file for " + pdfFile);
		}

		if(cc.getBoolean("dumpDebugFile", false)) {
			try {
				debugStream = new FileOutputStream(pdfFile.toString() + ".log");
			} catch (FileNotFoundException e) {
				debugStream = null;
			}
		}
	}

	/**
	 * Reads the pdr file and extracts all annotation information.
	 *  
	 * @return A list of annotations.
	 */
	public List<Annotation> read() {
		final List<Annotation> result = new LinkedList<Annotation>();

		if(!pdrFile.exists()) {
			return result;
		}
		
		if(!pdrFile.canRead()) {
			LOG.error("Cannnot read pdr-file " + pdrFile);
			return result;
		}
		
		try {
			fileStream = new FileInputStream(pdrFile);
			pdrStream = new DataInputStream(fileStream);
			
			final int magic = readUnsigned32();
			if(magic != MAGIC_VALUE) {
				LOG.error("Magic file header is wrong " + Integer.toHexString(magic));
				return result;
			}
			
			writeDebug("[Magic String]\n");
			
			skipBytes(1);
			@SuppressWarnings("unused")
			int lastOpenedPage = readUnsigned32();
			writeDebug("\n[Last opened page]\n");
			
			int numberOfBookmarks = readUnsigned32();
			LOG.info("Number of bookmarks " + numberOfBookmarks);

			for(int i = 0; i < numberOfBookmarks; ++i) {
				skipBytes(1);                        // skipping unknown data
				int page = pdrStream.readInt();      // reading page number
				writeDebug(" [page]");
				readPascalString();                  // page name
				writeDebug(" [page name]\n");
				
				result.add(new Bookmark(cc, page));
			}
			
			skipBytes(20); // skipping unknown data
			
			final int numberOfMarkings = pdrStream.readInt();
			LOG.info("Number of markings " + numberOfMarkings);
			writeDebug("\n[Number of markings " + numberOfMarkings + "]\n");
			
			for(int i = 0; i < numberOfMarkings; ++i) {
				// read start
				skipBytes(1);                        // skipping unknown data
				int page1 = pdrStream.readInt();      // reading page number
				writeDebug(" [page]");
				readPascalString();                  // page name
				writeDebug(" [page name]");
				readPascalString();                  // skipping pdfloc entry
				writeDebug(" [pdfloc] ");
				writeDebug("["+pdrStream.readFloat()+"]");
				//skipBytes(4);                        // skipping unknown data
				double x1 = pdrStream.readDouble(),  // start x
			           y1 = pdrStream.readDouble();  // start y
				writeDebug(" [x1]");
				writeDebug(" [y1]");

				// read end
				int page2 = pdrStream.readInt();      // reading page number
				writeDebug(" [page]");
				readPascalString();                  // page name
				writeDebug(" [page name]");
				readPascalString();                  // skipping pdfloc entry
				writeDebug(" [pdfloc] ");
				writeDebug("["+pdrStream.readFloat()+"]");
				//qskipBytes(4);                        // skipping unknown data
				double x2 = pdrStream.readDouble(),  // end x
			           y2 = pdrStream.readDouble();  // end y
				writeDebug(" [x2]");
				writeDebug(" [y2] ");
				skipBytes(2);                        // skipping unknown data
				writeDebug("\n");
				
				result.add(new Marking(cc, page1, x1, y1, page2, x2, y2));
			}

			int numberOfComments = pdrStream.readInt();
			LOG.info("Number of comments " + numberOfComments);
			writeDebug("\n[Number of comments " + numberOfComments + "]\n");

			for(int i = 0; i < numberOfComments; ++i) {
				skipBytes(1);                        // skipping unknown data
				int page = pdrStream.readInt();      // reading page number
				writeDebug(" [page]");
				readPascalString();                  // page name
				writeDebug(" [page name]");
				double x = pdrStream.readDouble(),   // reading x
				       y = pdrStream.readDouble();   // reading y
				writeDebug(" [x]");
				writeDebug(" [y]");

				readPascalString();                  // skipping pdfloc entry
				writeDebug(" [pdfloc]");
		        String content = readPascalString(); // reading comment
				writeDebug(" [content]\n");
		        
		        result.add(new Comment(cc, page, x, y, content));
			}
			
			int finalEntry = readUnsigned32();
			
			writeDebug("\n[Final entry " + finalEntry + "]");
			
			LOG.info("Number of available bytes " + pdrStream.available());

			closePdrStream();
			closeDebugStream();
		} catch (FileNotFoundException e) {
			LOG.error("Cannot find pdr-file " + pdrFile);
		} catch (IOException e) {
			LOG.error("IO error occured while reading " + pdrFile);
		}
		
		return result;
	}

	private void closePdrStream() {
		try {
			pdrStream.close();
			fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void closeDebugStream() {
		if(debugStream != null) {
			try {
				debugStream.close();
			} catch (IOException e) {
				LOG.warn("Error while closing debug stream");
			}
		}
	}

	private void writeDebug(final String message) {
		if(debugStream != null) {
			try {
				debugStream.write(message.getBytes());
			} catch (IOException e) {
				LOG.warn("Error while writing debug log");
			}
		}
	}

	private String readPascalString() throws IOException {
		int length = pdrStream.readShort();
		byte [] rawString = new byte[length];
		
		pdrStream.readFully(rawString);
		return new String(rawString);
	}

	private void skipBytes(int byteCount) throws IOException {
		byte skippedData[] = new byte[byteCount];
		pdrStream.readFully(skippedData);
		
		if(debugStream != null) {
			boolean first = true;
			for(int index = 0; index < skippedData.length; ++ index) {
				if(first) {
					first = false;
				} else {
					debugStream.write(" ".getBytes());
				}
				
				String hexString = Integer.toHexString(skippedData[index]);
				if(hexString.length() == 1) {
					hexString = "0" + hexString;
				} else if(hexString.startsWith("ffffff")) {
					hexString = hexString.substring(6, hexString.length());
				}
				debugStream.write(hexString.getBytes());
			}
		}
	}

	private int readUnsigned32() throws IOException {
		return pdrStream.readInt();
	}
}
