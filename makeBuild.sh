#!/bin/bash
#
# Rohtash Singh Lakra
#
clear

# Set Home Directories.
export JDEPEND_HOME=./3rdparty/jdepend
export JSCH_HOME=./3rdparty/jsch/lib
export JSCH=${JSCH_HOME}/jsch-0.1.54.jar
export JAVA_HOME=$(/usr/libexec/java_home)
#JAVA_HOME=$(type -p java|xargs readlink -f|xargs dirname|xargs dirname)

mkdir -p "${JSCH_HOME}"
mkdir -p "${JDEPEND_HOME}/lib"
echo
echo "Compile JDepend"
echo
$JAVA_HOME/bin/javac -sourcepath ${JDEPEND_HOME}/src/java -d ${JDEPEND_HOME}/lib ${JDEPEND_HOME}/src/java/jdepend/framework/*.java

rc=$?; if [ $rc != 0 ]; then exit $rc; fi
mkdir -p lib
if [ ! -e "$JSCH" ]; then
	curl --output "${JSCH}" http://central.maven.org/maven2/com/jcraft/jsch/0.1.54/jsch-0.1.54.jar 
	rc=$?; if [ $rc != 0 ]; then echo "Cant'd download ${JSCH}, Check Internet Connection!"; exit $rc; fi
fi

echo
echo "Compile 7Bee"
echo
echo ./lib:${JDEPEND_HOME}/lib:$JSCH
timestamp=$(date +"%c")
echo "package org.bee; public class CompileStamp { public static final String getStamp() { return  \"$timestamp\"; }}" > src/java/org/bee/CompileStamp.java 
$JAVA_HOME/bin/javac -source 1.6 -target 1.6 -classpath ./lib:${JDEPEND_HOME}/lib:$JSCH -sourcepath src/java -d lib src/java/org/bee/processor/*.java src/java/org/bee/util/*.java src/java/org/bee/oper/*.java src/java/org/bee/func/*.java

rc=$?; if [ $rc != 0 ]; then exit $rc; fi
echo "build result JAR"
$JAVA_HOME/bin/jar -cvmf ./manifest.mf lib/bee.jar -C lib org -C 3rdparty/jdepend/lib jdepend
rc=$?; if [ $rc != 0 ]; then exit $rc; fi
while true; do
    read -p "Do you wish to install 7Bee?" yn
    case $yn in
        [Yy]* ) sudo java -jar ./lib/bee.jar install; break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done
