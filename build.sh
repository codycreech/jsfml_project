#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/JSFML Project"
rm -rf build/classes build/test-classes
mkdir -p build/classes build/test-classes
if command -v javac >/dev/null 2>&1; then
  javac --release 8 -cp jsfml.jar -d build/classes src/jsfmlproject/*.java
  javac --release 8 -cp build/classes -d build/test-classes test/jsfmlproject/*.java
else
  java com.sun.tools.javac.Main --release 8 -cp jsfml.jar -d build/classes src/jsfmlproject/*.java
  java com.sun.tools.javac.Main --release 8 -cp build/classes -d build/test-classes test/jsfmlproject/*.java
fi
java -cp build/classes:build/test-classes jsfmlproject.GameModelTest
printf 'Manifest-Version: 1.0\nMain-Class: jsfmlproject.Main\nClass-Path: ../jsfml.jar\n\n' > build/MANIFEST.MF
if command -v jar >/dev/null 2>&1; then
  jar cfm build/orbit-run.jar build/MANIFEST.MF -C build/classes .
else
  java sun.tools.jar.Main cfm build/orbit-run.jar build/MANIFEST.MF -C build/classes .
fi
printf 'Built JSFML Project/build/orbit-run.jar\n'
