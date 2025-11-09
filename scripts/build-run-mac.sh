#!/usr/bin/env bash
set -euo pipefail

# If you installed JavaFX via Homebrew, update the module-path to that location.
# Or keep using lib/*.jar shipped in the repo.

mkdir -p ../bin
find ../src -name "*.java" > sources.txt
javac -d ../bin -cp "../lib/*:../resources" @sources.txt
rsync -a ../resources/ ../bin/

pushd ..
java --module-path lib --add-modules javafx.controls,javafx.fxml \
 -cp "bin:lib/*" client.CarpoolClient
popd
