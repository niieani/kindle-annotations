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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * PDFAnnotator main class if you use it as an standalone application.
 * 
 * @author Bernhard Berger
 */
public class PDFAnnotator {

	private final File pdfFile;
	private final File outFile;

	public PDFAnnotator(final String pdfFilename, final String outFilename) {
		this.pdfFile = new File(pdfFilename);
		this.outFile = new File(outFilename);
		
		assert pdfFile.exists() && pdfFile.canRead();
	}

	/**
	 * Main function of PDFAnnotator. Expects a PDF file as parameter.
	 * 
	 * @param args List of command line parameters.
	 * 
	 * java -jar pdfannotator.jar input.pdf [output.pdf]
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, COSVisitorException {
		try {
			PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			Logger.getRootLogger().addAppender(consoleAppender);
			Logger.getRootLogger().setLevel(Level.DEBUG);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		if(args.length < 1 ) {
			System.err.println("Please specify at least a pdf file.");
			System.exit(1);
		} else if(args.length > 2) {
			System.err.println("Please specify at just an input and an output pdf file.");
			System.exit(1);
		}
		
		final String inputFilename = args[0];
		final String outputFilename = args.length == 2 ? args[1] : args[0];
		new PDFAnnotator(inputFilename, outputFilename).run();
	}

	@SuppressWarnings("unchecked")
	private void run() throws FileNotFoundException, IOException, COSVisitorException {
		final List<Annotation> annotations = new KindleAnnotationReader(pdfFile).read();
		
		// annotate pdf
		final PDFParser parser = new PDFParser(new FileInputStream(pdfFile));
		parser.parse();
		final PDDocument inDocument = parser.getPDDocument();
		
		int pageNumber = 0;
		for(PDPage page : (List<PDPage>)inDocument.getDocumentCatalog().getAllPages()) {
			for(Annotation dxAnn : annotations) {
				dxAnn.toPDAnnotation(pageNumber, page);
			}
			
			pageNumber++;
		}
		
		inDocument.save(outFile.toString());		
		inDocument.close();
	}
}
