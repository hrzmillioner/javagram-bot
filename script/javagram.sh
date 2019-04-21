#!/usr/bin/env bash
#  Copyright (c) 2019 Jakub Pomykała
#
#  Permission is hereby granted, free of charge, to any person obtaining
#  a copy of this software and associated documentation files (the
#  "Software"), to deal in the Software without restriction, including
#  without limitation the rights to use, copy, modify, merge, publish,
#  distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so, subject to
#  the following conditions:
#
#  The above copyright notice and this permission notice shall be
#  included in all copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
#  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
#  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
#  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
#  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
#  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

INSTALLATION_DIR=~/.javagram/javagram.jar
DOWNLOAD_URL="https://github.com/jpomykala/javagram-bot/releases/download/1.0.2/javagram.jar"
THIS_FILE=`basename "$0"`

mkdir -p ~/.javagram

if [[ ! -f ${INSTALLATION_DIR} ]]; then
  curl -L ${DOWNLOAD_URL} --silent --output ${INSTALLATION_DIR}
fi


# is cygwin
cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [[ -h "$PRG" ]] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Set the current directory to the installation directory
INSTALL_DIR=`dirname "$PRG"`

if [[ -x "$INSTALL_DIR/jre/bin/java" ]]; then
 JAVA_CMD=${INSTALL_DIR}/jre/bin/java
else
 # Use JAVA_HOME if it is set
 if [[ -z ${JAVA_HOME} ]]; then
  JAVA_CMD=java
 else
  JAVA_CMD=${JAVA_HOME}/bin/java
 fi
fi

CP="$INSTALL_DIR/*"

if ${cygwin}; then
  CP=$(cygpath -pw "$CP")
fi

echo "Running SimpleLocalize"
"$JAVA_CMD" -cp ${INSTALLATION_DIR} me.jpomykala.javagram.Main "$@"

# exit using the same code returned from Java
exit $?
