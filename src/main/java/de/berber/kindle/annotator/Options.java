package de.berber.kindle.annotator;

import org.kohsuke.args4j.Option;

public class Options {
    @Option(name="-h", usage="Prints usage", aliases={"-help"})
    boolean help;
    
	@Option(name="-c", usage="Specify a configuration properties file", aliases={"--config"})
	public String config = null; 
	
	@Option(name="-in", usage="Specify an input file.", aliases={"--input"}, required=true)
	public String input;

	@Option(name="-out", usage="Specify an output file.", aliases={"--output"}, required=true)
	public String output;
}
