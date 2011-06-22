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
package de.berber.kindle.annotator;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import de.berber.kindle.annotator.lib.PDFAnnotator;

/**
 * The batch command line main program.
 * 
 * TODO Use the default data model framework to avoid duplications.
 * 
 * @author Bernhard J. Berger
 */
public class BatchMain extends AbstractMain {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Main.class);

	public BatchMain(final @Nonnull Options options,
			         final @Nonnull CompositeConfiguration cc) {
		super(options, cc);
	}

	@Override
	public void run() {
		final File inputFile = new File(options.input);
		File outputFile = new File(options.output);

		if (!inputFile.exists()) {
			LOG.fatal("Input file does not exist.");
			System.exit(1);
		}

		if (inputFile.isFile()) {
			if (outputFile.exists() && outputFile.isDirectory()) {
				outputFile = new File(outputFile.toString() + File.separator
						+ inputFile.getName());
			}
			new PDFAnnotator(cc, options.input, options.output).run();
		} else if (inputFile.isDirectory()) {
			if (!outputFile.exists() || !outputFile.isDirectory()) {
				LOG.error("Output file must be an existing directory.");
				System.exit(1);
			}

			final File[] inputFiles = inputFile.listFiles();

			for (int index = 0; index < inputFiles.length; ++index) {
				final File currentFile = inputFiles[index];

				if (!currentFile.isFile()) {
					continue;
				}

				if (!currentFile.toString().endsWith(".pdf")) {
					continue;
				}

				final File pdrFile = new File(currentFile.toString().substring(
						0, currentFile.toString().lastIndexOf('.'))
						+ ".pdr");

				if (!pdrFile.exists()) {
					continue;
				}

				final File output = new File(outputFile + File.separator
						+ currentFile.getName());
				LOG.info(currentFile.toString() + " --> " + output);
				new PDFAnnotator(cc, currentFile.toString(), output.toString())
						.run();
			}
		}
	}

}
