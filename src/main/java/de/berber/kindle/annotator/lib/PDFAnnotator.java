/*
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
package de.berber.kindle.annotator.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;

/**
 * The PDF annotator annotates a single PDF-file with the contents found in its
 *   associated PDR-file.
 * 
 * @author Bernhard J. Berger
 */
public class PDFAnnotator {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(PDFAnnotator.class);
	
	/**
	 * Input PDF-file.
	 */
	private final File pdfFile;
	
	/**
	 * Output PDF-file.
	 */
	private final File outFile;
	
	/**
	 * Configuration object.
	 */
	private final CompositeConfiguration cc;

	/**
	 * Creates a new PDF annotator object and sets its data.
	 */
	public PDFAnnotator(final CompositeConfiguration cc, final String pdfFilename, final String outFilename) {
		this.pdfFile = new File(pdfFilename);
		this.outFile = new File(outFilename);
		this.cc = cc;
		
		
		assert pdfFile.exists() && pdfFile.isFile() && pdfFile.canRead();
		assert !outFile.exists() || outFile.canWrite();
	}

	@SuppressWarnings("unchecked")
	public boolean run() {
		// read all annotations
		final List<Annotation> annotations = new KindleAnnotationReader(cc, pdfFile).read();
		
		if(annotations.size() == 0) {
			return true;
		}
		
		PDDocument document = null;
		// annotate pdf
		try {
			 document = PDDocument.load(pdfFile);

			//inDocument.decrypt(pass);
			// get outline for bookmarks
			PDDocumentOutline documentOutline = document.getDocumentCatalog().getDocumentOutline();
			
			if(documentOutline == null) {
				// if there is no document outline we have to create a new one.
				documentOutline = new PDDocumentOutline();
				document.getDocumentCatalog().setDocumentOutline(documentOutline);
			}
		
			assert documentOutline != null;
			
			// convert annotations for each page
			int pageNumber = 0;
			for(PDPage page : (List<PDPage>)document.getDocumentCatalog().getAllPages()) {
				for(final Annotation dxAnn : annotations) {
					dxAnn.toPDAnnotation(pageNumber, documentOutline, page);
				}
				
				pageNumber++;
			}

			//inDocument.setAllSecurityToBeRemoved(true);
			document.save(outFile.toString());	
		} catch(FileNotFoundException e) {
			LOG.error("Could not find input file " + pdfFile);
			return false;
		} catch(IOException e) {
			LOG.error("IOError while writing result file " + outFile);
			return false;
		} catch (COSVisitorException e) {
			LOG.error("PDFBox error while storing result file " + outFile);
			return false;
		}
		finally {
			if(document != null) {
				try {
					document.close();
				} catch (IOException e) {
					LOG.error("Error while closing PDF document " + pdfFile);
				}
			}
		}
		
		return true;
	}
}
