# Installation #
There is no real installation, just unzip the release.

# Usage #
The Kindle PDF annotator is currently a command line tool. Maybe I will add a simple UI in the future. It has some parameters you can pass and configuration file for more fine-grained control.

You have to call `java -jar kindle-annotator-0.3.jar <parameters>` to run the annotator.

# Parameters #
Currently there are four parameters you can pass to the annotator:
  * **-c**/**--config** _filename_ Specify a configuration properties file.
  * **-h**/**-help**    Prints usage
  * **-in**/**--input** _filename_ Specify an input file.
  * **-out**/**--output** _filename_ Specify an output file.

# Configuration #
The configuration file is a simple [Java properties file](http://en.wikipedia.org/wiki/.properties). You can add the following properties:
  * **debugLevel** One of _DEBUG_, _INFO_, _WARN_, _ERROR_.
  * **dumpDebugFile** _true_ or _false_. If this switch is true a .log file is dumped by the PDR reader.
  * **markings.color** Color of a marking, given with a [web color](http://en.wikipedia.org/wiki/Web_color)
  * **markings.opacity** Opacity of markings. `0.0 <= opacity <= 1.0`
  * **comments.color** Color of a comment, given with a [web color](http://en.wikipedia.org/wiki/Web_color)
  * **comments.opacity** Opacity of comments. `0.0 <= opacity <= 1.0`