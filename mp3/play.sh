#!/bin/sh
if [ -z "$JAVA_HOME" ] ; then
  if [ -d "/System/Library/Frameworks/JavaVM.framework/Home" ] ; then
    export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
  else
    echo "Error: JAVA_HOME is not defined."
  fi
fi
"$JAVA_HOME/bin/java" -Xms10m -Xmx10m -XX:PerfDataSamplingInterval=10000 -server -jar bin/*.jar
