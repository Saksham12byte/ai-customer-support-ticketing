#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper script for Unix/macOS/Linux
# ----------------------------------------------------------------------------
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`" >/dev/null
APP_DIR="`pwd -P`"
cd "$SAVED" >/dev/null

if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="java"
fi

# Locate Apache Maven in scratch or PATH
if [ -x "/Users/sakshamraghuwanshi/.gemini/antigravity/scratch/apache-maven-3.9.9/bin/mvn" ]; then
  exec "/Users/sakshamraghuwanshi/.gemini/antigravity/scratch/apache-maven-3.9.9/bin/mvn" "$@"
elif which mvn >/dev/null 2>&1; then
  exec mvn "$@"
else
  echo "Error: Maven not found. Please install Maven or configure JAVA_HOME."
  exit 1
fi
