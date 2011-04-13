TARGET=kindle-annotations
rm -rf $TARGET
mkdir $TARGET

mvn install

cp target/kindle-annotator-0.1-SNAPSHOT.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/pdfbox/1.5.0/pdfbox-1.5.0.jar $TARGET
cp ${HOME}/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar $TARGET
cp ${HOME}/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/jempbox/1.5.0/jempbox-1.5.0.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/fontbox/1.5.0/fontbox-1.5.0.jar $TARGET

zip -r9 kindle-annotations.zip $TARGET