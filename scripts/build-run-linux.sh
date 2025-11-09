#!/usr/bin/env bash
set -euo pipefail

# Optional: export JAVA_HOME to a bundled jdk folder
# export JAVA_HOME="$(pwd)/../jdk-17"
# export PATH="$JAVA_HOME/bin:$PATH"

mkdir -p ../bin

# Compile
find ../src -name "*.java" > sources.txt
javac -d ../bin -cp "../lib/*:../resources" @sources.txt

# Copy resources
rsync -a ../resources/ ../bin/

# Run
pushd ..
java --module-path lib --add-modules javafx.controls,javafx.fxml \
 -cp "bin:lib/*" client.CarpoolClient
popd
