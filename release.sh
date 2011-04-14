TARGET=kindle-annotations
VERSION=`xpath -q -e "//project/version/text()" pom.xml`


rm -rf $TARGET
rm -rf target
mkdir $TARGET

mvn install

cp target/kindle-annotator-$VERSION.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/pdfbox/1.5.0/pdfbox-1.5.0.jar $TARGET
cp ${HOME}/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar $TARGET
cp ${HOME}/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/jempbox/1.5.0/jempbox-1.5.0.jar $TARGET
cp ${HOME}/.m2/repository/org/apache/pdfbox/fontbox/1.5.0/fontbox-1.5.0.jar $TARGET
cp ${HOME}/.m2/repository/args4j/args4j/2.0.12/args4j-2.0.12.jar $TARGET
cp ${HOME}/.m2/repository/commons-configuration/commons-configuration/1.6/commons-configuration-1.6.jar $TARGET
cp ${HOME}/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar $TARGET
cp ${HOME}/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar $TARGET

zip -r9 kindle-annotations-$VERSION.zip $TARGET