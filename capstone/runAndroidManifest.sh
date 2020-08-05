#!/bin/bash
javac -sourcepath src/main -d build src/main/java/com/google/sps/data/AndroidManifestWrapper.java src/main/java/com/google/sps/data/AndroidManifestParser.java

java -cp .:build:**/*.class com.google.sps.data.AndroidManifestWrapper "$@"
