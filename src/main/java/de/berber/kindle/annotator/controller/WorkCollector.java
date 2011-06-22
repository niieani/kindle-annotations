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
package de.berber.kindle.annotator.controller;

import java.io.File;

import org.apache.log4j.Logger;

import de.berber.kindle.annotator.model.WorkingList;

/**
 * Thread to collect all PDF files we want to convert. The files that are
 *   found, will be added to the model.
 *
 * @author Bernhard J. Berger
 */
public class WorkCollector implements Runnable {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(WorkCollector.class);

	/**
	 * The target file or directory.
	 */
	private final File outputFile;
	
	/**
	 * The input file or directory.
	 */
	private final File inputFile;
	
	/**
	 * The internal data model.
	 */
	private final WorkingList workingList;

	/**
	 * Creates a {@code WorkCollector} and initializes the necessary fields.
	 *   The constructor also does the needed parameter checks and will throw
	 *   an {@link IllegalParameterException} if there is a problem.
	 * 
	 * @param inputFile The input file or directory. The input file must exist
	 *   and be readable for the user.
	 *   
	 * @param outputFile The target file or directory or {@code null}. The
	 *   target file has to denote a directory or has to be {@code null} in
	 *   the case that {@code inputFile} is a directory. In the case of being
	 *   a PDF file {@code outpuFile} has to be {@code null}, a file or a
	 *   directory. If there already is an appropriate output file it will be
	 *   overwritten.
	 * 
	 * @param model The internal data model to fill.
	 */
	public WorkCollector(final File inputFile, File outputFile, final WorkingList model) {
		// ensure that the input file exists
		if(!inputFile.exists()) {
			throw new IllegalArgumentException("Input file does not exist");
		} else if(!inputFile.canRead()) {
			throw new IllegalArgumentException("Cannot read input file.");
		}
		
		// check validity of input
		if(inputFile.isFile()) {
			if(!inputFile.toString().toLowerCase().endsWith(".pdf")) {
				throw new IllegalArgumentException("Input file is no pdf file.");
			}
			
			if(outputFile == null) {
				// store changes back to input file
				if(!inputFile.canWrite()) {
					throw new IllegalArgumentException("Cannot write output file " + inputFile);
				}
				
				outputFile = inputFile;
			} else if(outputFile.isFile()) {
				// store changes to new file
				if(!outputFile.canWrite()) {
					throw new IllegalArgumentException("Cannot write output file " + outputFile);
				}
			} else if(outputFile.isDirectory()) {
				 outputFile = new File(inputFile + File.separator + inputFile.getName());
				 
				 if(outputFile.exists()) {
					 LOG.info("Target file " + outputFile + "already exists. Operation will erease it.");
				 }
				 
				 if(!outputFile.canWrite()) {
						throw new IllegalArgumentException("Cannot write output file " + outputFile);
				 }
			}

			this.inputFile  = inputFile;
			this.outputFile = outputFile;
		} else {
			if(!inputFile.isDirectory()) {
				throw new IllegalArgumentException("Input file is not a directory: " + inputFile);
			}
			
			if(outputFile == null) {
				// everything is fine. We will store the results into the original files.
				outputFile = inputFile;
			} else if(!outputFile.exists()) {
				throw new IllegalArgumentException("Output directory does not exist.");
			} else if(!outputFile.isDirectory()) {
				throw new IllegalArgumentException("Output file has got to be a directory.");
			}
			
			this.inputFile  = inputFile;
			this.outputFile = outputFile;
		}
		LOG.info("Converting " +  this.inputFile + " to " + this.outputFile);
		
		this.workingList = model;
		model.collectorStarts();
	}
	
	/**
	 * Checks if there is a PDR file for the given PDF file.
	 */
	private static boolean existsPDR(final File pdfFile) {
		final File pdrFile = new File(pdfFile.toString().substring(0, pdfFile.toString().lastIndexOf('.')) + ".pdr");
		
		LOG.info("Checking for PDR file " +  pdrFile + " for class " + pdfFile);

		boolean result = pdrFile.exists();
		
		if(result) {
			result = pdrFile.canRead();
			
			if(!result) {
				LOG.warn("Cannot read PDR file : " + pdrFile);
			}
		} else {
			LOG.warn("There is no PDR file for: " + pdfFile);
		}
		
		return pdrFile.exists();
	}

	/**
	 * Collects all input files and adds them to the working queue.
	 */
	public void run() {
		if(inputFile.isFile()) {
			// process a single file if we can find a PDR file.
			if(existsPDR(inputFile)) {
				addTask(inputFile, outputFile);
			}
		} else {
			assert inputFile.isDirectory() : "Just an internal consistency check.";
			
			final File [] inputFiles = inputFile.listFiles();
			
			// TODO Make the search recursive
			for(int index = 0; index < inputFiles.length; ++index) {
				final File currentFile = inputFiles[index];
				
				if(!currentFile.isFile()) {
					continue;
				}
				
				if(!currentFile.toString().endsWith(".pdf")) {
					continue;
				}
				
				if(!currentFile.canRead()) {
					LOG.warn("Cannot read PDF file " + currentFile);
					continue;
				}
				
				if(!existsPDR(currentFile)) {
					// PDF file without annotation
					continue;
				}
				
				final File output = new File(outputFile + File.separator + currentFile.getName());
				
				if(output.exists() && !output.canWrite()) {
					LOG.error("Cannot write output file " + output);
					continue;
				}
				
				addTask(currentFile, output);
			}
		}
		
		sendFinishMessage();
	}

	/**
	 * Tell the model that we are finished.
	 */
	private void sendFinishMessage() {
		workingList.collectorFinished();
	}

	/**
	 * Add a conversion task to the model.
	 */
	private void addTask(final File inputFile, final File outputFile) {
		workingList.addTask(inputFile, outputFile);
	}
}
