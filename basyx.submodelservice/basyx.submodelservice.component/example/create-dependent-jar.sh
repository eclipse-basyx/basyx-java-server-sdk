#!/bin/bash

SRC_DIR="tmp/src"
BIN_DIR="tmp/bin"
JAR_FOLDER="jars"
JAR_FILE="HelloWorld.jar"

mkdir -p "$SRC_DIR"
mkdir -p "$BIN_DIR"

cat <<EOF > "$SRC_DIR/HelloWorld.java"
public class HelloWorld {

    public String sayHello() {
        return "Hello World!";
    }
}
EOF

javac -d "$BIN_DIR" "$SRC_DIR/HelloWorld.java"

cd "$BIN_DIR"
jar cf "$JAR_FILE" HelloWorld.class

cp --force $JAR_FILE ../../$JAR_FOLDER/$JAR_FILE

cd ../..

echo "jar file created: $JAR_FOLDER/$JAR_FILE"