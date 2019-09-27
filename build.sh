#!/bin/sh
set -e
SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
cd $SCRIPTPATH
ant build-for-javascript -Dautomated=true
cd dist
unzip result.zip
rm result.zip
cp SweteApp*.zip ../SweteApp.zip
cp SweteApp*.war ../SweteApp.war