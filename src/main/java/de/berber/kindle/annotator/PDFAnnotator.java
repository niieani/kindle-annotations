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
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;

/**
 * PDFAnnotator main class if you use it as an standalone application.
 * 
 * @author Bernhard J. Berger
 */
public class PDFAnnotator {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(PDFAnnotator.class);
	
	private final File pdfFile;
	private final File outFile;
	private final CompositeConfiguration cc;

	public PDFAnnotator(final CompositeConfiguration cc, final String pdfFilename, final String outFilename) {
		this.pdfFile = new File(pdfFilename);
		this.outFile = new File(outFilename);
		this.cc = cc;
		
		assert pdfFile.exists() && pdfFile.canRead();
	}

	/**
	 * Main function of PDFAnnotator. Expects a PDF file as parameter.
	 * 
	 * @param args List of command line parameters.
	 * 
	 * java -jar pdfannotator.jar input.pdf [output.pdf]
	 */
	public static void main(String[] args) {
		final CompositeConfiguration cc = new CompositeConfiguration();
		
		try {
			// configure logging
			PatternLayout layout = new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n");
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			Logger.getRootLogger().addAppender(consoleAppender);
			Logger.getRootLogger().setLevel(Level.WARN);

			// read commandline
			Options options = new Options();
	        final CmdLineParser parser = new CmdLineParser(options);
            parser.setUsageWidth(80);

            try {
            	// parse the arguments.
            	parser.parseArgument(args);

            	if (options.help) {
            		parser.printUsage(System.err);
            		return;
            	}
             } catch (CmdLineException e) {
            	 // if there's a problem in the command line, 
                 // you'll get this exception. this will report
                 // an error message.
                 System.err.println(e.getMessage());
                 System.err.println("Usage:");
                 // print the list of available options
                 parser.printUsage(System.err);
                 System.err.println();

                 // print option sample. This is useful some time
                 System.err.println("  Example: java -jar <jar> "
                                 + parser.printExample(ExampleMode.ALL));

                 return;
             }
			
			// read configs
			final URL defaultURL = PDFAnnotator.class.getClassLoader().getResource("de/berber/kindle/annotator/PDFAnnotator.default");

			if(options.config != null) {
				final File configFile = new File(options.config);
				
				if(!configFile.exists() || !configFile.canRead()) {
					LOG.error("Specified config file does not exist.");
				} else {
					cc.addConfiguration(new PropertiesConfiguration(configFile));
				}
			}
			
			cc.addConfiguration(new PropertiesConfiguration(defaultURL));

			Logger.getRootLogger().setLevel(Level.toLevel(cc.getString("debugLevel", "WARN")));
		
			final File inputFile = new File(options.input);
			File outputFile = new File(options.output);
			
			if(!inputFile.exists()) {
				LOG.fatal("Input file does not exist.");
				System.exit(1);
			}
			
			if(inputFile.isFile()) {
				if(outputFile.exists() && outputFile.isDirectory()) {
					outputFile = new File(outputFile.toString() + File.separator + inputFile.getName());
				}
				new PDFAnnotator(cc, options.input, options.output).run();
			} else if(inputFile.isDirectory()) {
				if(!outputFile.exists() || !outputFile.isDirectory()) {
					LOG.error("Output file must be an existing directory.");
					System.exit(1);
				}
				
				final File [] inputFiles = inputFile.listFiles();
				
				for(int index = 0; index < inputFiles.length; ++index) {
					final File currentFile = inputFiles[index];
					
					if(!currentFile.isFile()) {
						continue;
					}
					
					if(!currentFile.toString().endsWith(".pdf")) {
						continue;
					}
					
					final File pdrFile = new File(currentFile.toString().substring(0, currentFile.toString().lastIndexOf('.')) + ".pdr");
					
					if(!pdrFile.exists()) {
						continue;
					}
					
					final File output = new File(outputFile + File.separator + currentFile.getName());
					LOG.info(currentFile.toString() + " --> " + output);
					new PDFAnnotator(cc, currentFile.toString(), output.toString()).run();
				}
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void run() {
		final List<Annotation> annotations = new KindleAnnotationReader(cc, pdfFile).read();
		
		// annotate pdf
		try {
			final PDFParser parser = new PDFParser(new FileInputStream(pdfFile));
			parser.parse();
			
			final PDDocument inDocument = parser.getPDDocument();
			//inDocument.decrypt(pass);
			final PDDocumentOutline documentOutline = inDocument.getDocumentCatalog().getDocumentOutline();
			
			assert documentOutline != null;
			
			int pageNumber = 0;
			for(PDPage page : (List<PDPage>)inDocument.getDocumentCatalog().getAllPages()) {
				for(Annotation dxAnn : annotations) {
					dxAnn.toPDAnnotation(pageNumber, documentOutline, page);
				}
				
				pageNumber++;
			}
			
			//inDocument.setAllSecurityToBeRemoved(true);
			inDocument.save(outFile.toString());		
			inDocument.close();
		} catch(FileNotFoundException e) {
			LOG.error("Could not find input file " + pdfFile);
			System.exit(1);
		} catch(IOException e) {
			LOG.error("IOError while writing result file " + outFile);
			System.exit(1);
		} catch (COSVisitorException e) {
			LOG.error("PDFBox error while storing result file " + outFile);
			System.exit(1);
		}
	}
}
