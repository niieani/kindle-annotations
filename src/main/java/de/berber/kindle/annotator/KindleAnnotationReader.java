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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
	private DataInputStream pdrStream;
	
	/**
	 * Creates a new annotation reader for the kindle device. 
	 * 
	 * @param pdfFile The pdf file you want to read annotations for.
	 */
	public KindleAnnotationReader(final File pdfFile) {
		pdrFile = new File(pdfFile.toString().substring(0, pdfFile.toString().length() - 1) + "r");

		if(!pdrFile.exists()) {
			LOG.error("Cannot find pdr-file for " + pdfFile);
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
			pdrStream = new DataInputStream(new FileInputStream(pdrFile));
			
			final int magic = readUnsigned32();
			if(magic != MAGIC_VALUE) {
				LOG.error("Magic file header is wrong " + Integer.toHexString(magic));
				return result;
			}
			
			skipBytes(31); // skipping unknown data
			
			final int numberOfMarkings = pdrStream.readShort();
			LOG.info("Number of markings " + numberOfMarkings);
			
			for(int i = 0; i < numberOfMarkings; ++i) {
				int page = 0; // TODO read page number
				
				// read start
				skipBytes(8);                        // skipping unknown data
				readPascalString();                  // skipping pdfloc entry
				skipBytes(4);                        // skipping unknown data
				double x1 = pdrStream.readDouble(),  // start x
			           y1 = pdrStream.readDouble();  // start y

				// read end
				skipBytes(7);                        // skipping unknown data
				readPascalString();                  // skipping pdfloc entry
				skipBytes(4);                        // skipping unknown data
				double x2 = pdrStream.readDouble(),  // end x
			           y2 = pdrStream.readDouble();  // end y
				skipBytes(2);                        // skipping unknown data
				
				result.add(new Marking(page, x1, y1, x2, y2));
			}

			skipBytes(2);                            // skipping unknown data
			int numberOfComments = pdrStream.readShort();
			LOG.info("Number of comments " + numberOfComments);

			for(int i = 0; i < numberOfComments; ++i) {
				skipBytes(3);                        // skipping unknown data
				int page = pdrStream.readShort();    // reading page number
				skipBytes(3);                        // skipping unknown data
				double x = pdrStream.readDouble(),   // reading x
				       y = pdrStream.readDouble();   // reading y

				readPascalString();                  // skipping pdfloc entry
		        String content = readPascalString(); // reading comment
		        
		        result.add(new Comment(page, x, y, content));
			}
			
			LOG.info("Number of available bytes " + pdrStream.available());
		} catch (FileNotFoundException e) {
			LOG.error("Cannot find pdr-file " + pdrFile);
		} catch (IOException e) {
			LOG.error("IO error occured while reading " + pdrFile);
		}
		
		return result;
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
	}

	private int readUnsigned32() throws IOException {
		return pdrStream.readInt();
	}
}
