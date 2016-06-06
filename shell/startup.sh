#!/bin/sh

# -----------------------------------------------------------------------------
# Start Script for the SimulateGateway Server

# -----------------------------------------------------------------------------

# resolve links - $0 may be a softlink
PRG="$0"
JAVA_HOME=/home/app/jdk1.7.0_80/
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
export LANG=zh_CN
export LC_ALL=zh_CN.UTF-8
echo $PRGDIR
# Only set SGW_HOME if not already set
SGW_HOME=/home/data/submsg-message
echo $SGW_HOME
STARTUP_CLASS_NAME=com.submsg.message.utils.MsgSendUtils
CLASSPATH=.:"$SGW_HOME"/conf
if [ -d "$SGW_HOME"/lib ]; then
  for i in $SGW_HOME/lib/*.jar; do
    CLASSPATH="$CLASSPATH":$i
  done
fi

JAVA_OPTS="-Xloggc:$SGW_HOME/logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SGW_HOME/logs -Xms512m -Xmx1024m"

"$JAVA_HOME"/bin/java $JAVA_OPTS -classpath "$CLASSPATH" $STARTUP_CLASS_NAME "$@" >> "$SGW_HOME"/logs/std.out 2>&1 &
echo $! > "$SGW_HOME"/logs/PID
