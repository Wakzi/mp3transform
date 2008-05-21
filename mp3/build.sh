#!/bin/sh
if [ -z "$JAVA_HOME" ] ; then
  echo "Error: JAVA_HOME is not defined."
fi
if [ ! -f "bin/org/mp3transform/build/Build.class" ] ; then
  if [ ! -d "bin" ] ; then
    mkdir bin
  fi
  javac -sourcepath src/tools -d bin src/tools/org/mp3transform/build/*.java
fi
java -cp "bin:$JAVA_HOME/lib/tools.jar:temp" org.mp3transform.build.Build $@
