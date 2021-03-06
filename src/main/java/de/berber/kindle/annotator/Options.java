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

import org.kohsuke.args4j.Option;

/**
 * All available command line options
 * 
 * @author Bernhard J. Berger
 */
public class Options {
    @Option(name="-h", usage="Prints usage", aliases={"-help"})
    boolean help;
    
	@Option(name="-c", usage="Specify a configuration properties file", aliases={"--config"})
	public String config = null; 
	
	@Option(name="-i", usage="Specify an input PDF file or an input directory.", aliases={"--input"})
	public String input = null;

	@Option(name="-o", usage="Specify an output PDF file or an output directory.", aliases={"--output"})
	public String output = null;
	
	@Option(name="-n", usage="Do not show graphical user interface.", aliases={"--nogui"})
	public boolean noGUI = false;
}
