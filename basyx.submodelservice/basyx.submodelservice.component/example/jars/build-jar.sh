#!/bin/bash

AAS4J_VERSION=1.0.4

rm -f HelloWorld.class SubstractOperation.class
rm -f aas4j-model-${AAS4J_VERSION}.jar 
rm -f HelloWorld.jar

mvn dependency:copy -Dartifact=org.eclipse.digitaltwin.aas4j:aas4j-model:${AAS4J_VERSION} -DoutputDirectory=.

javac -cp aas4j-model-1.0.4.jar HelloWorld.java SubstractOperation.java
jar cf HelloWorld.jar HelloWorld.class SubstractOperation.class

rm -f HelloWorld.class SubstractOperation.class
rm -f aas4j-model-${AAS4J_VERSION}.jar