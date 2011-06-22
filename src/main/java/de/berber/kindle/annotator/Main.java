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
import java.net.URL;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;

import de.berber.kindle.annotator.gui.GUIMain;
import de.berber.kindle.annotator.lib.PDFAnnotator;

/**
 * PDFAnnotator main class if you use it as an standalone commandline tool or
 * the graphical user interface.
 * 
 * @author Bernhard J. Berger
 */
public class Main {
	/**
	 * The log instance
	 */
	private final static Logger LOG = Logger.getLogger(Main.class);

	/**
	 * Main function of PDFAnnotator. For command line parameters see the {@see
	 * Options} class.
	 * 
	 * @param args
	 *            List of command line parameters.
	 */
	public static void main(String[] args) {
		final CompositeConfiguration cc = new CompositeConfiguration();

		try {
			// configure logging
			final PatternLayout layout = new PatternLayout(
					"%d{ISO8601} %-5p [%t] %c: %m%n");
			final ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			Logger.getRootLogger().addAppender(consoleAppender);
			Logger.getRootLogger().setLevel(Level.WARN);

			// read commandline
			final Options options = new Options();
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

			// read default configuration file
			final URL defaultURL = PDFAnnotator.class.getClassLoader()
					.getResource("de/berber/kindle/annotator/PDFAnnotator.default");

			// read config file specified at the command line
			if (options.config != null) {
				final File configFile = new File(options.config);

				if (!configFile.exists() || !configFile.canRead()) {
					LOG.error("Specified configuration file does not exist.");
				} else {
					cc.addConfiguration(new PropertiesConfiguration(configFile));
				}
			}

			cc.addConfiguration(new PropertiesConfiguration(defaultURL));

			AbstractMain main = null;
			if (options.noGUI) {
				main = new BatchMain(options, cc);
			} else {
				main = new GUIMain(options, cc);
			}
			
			main.run();
		} catch (Exception ex) {
			LOG.error("Error while executing Kindle Annotator. Please report a bug.");
			ex.printStackTrace();
		}
	}
}
