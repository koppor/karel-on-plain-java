#!/bin/sh
# Formats (or with --check: checks) the engine with Netflix jfmt, the Java formatter this project follows.
# Only src/karel/Karel.java: jfmt expands "import static karel.Karel.*;" to single imports, which the student files must keep.
# Usage: ./jfmt.sh [--check]
set -e
VERSION=0.9.0
SHA256=0b4e41a2c8a579ddabda070b7c9e4c4138301be8d5e6501cfa1bcd2a4c6874e1
JAR="${TMPDIR:-/tmp}/com.netflix.tools.jfmt-$VERSION.jar"
if [ ! -f "$JAR" ]; then
  curl -sSL -o "$JAR" "https://repo1.maven.org/maven2/com/netflix/com.netflix.tools.jfmt/$VERSION/com.netflix.tools.jfmt-$VERSION.jar"
fi
echo "$SHA256  $JAR" | sha256sum -c - >/dev/null
exec java   --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED   --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED   -jar "$JAR" "$@" src/karel/Karel.java
