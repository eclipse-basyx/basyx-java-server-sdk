#!/bin/sh
OLD_WORK_DIR=$(pwd)
trap 'cd $OLD_WORK_DIR' EXIT

cd $(dirname "${BASH_SOURCE[0]}")/..

MAVEN_OPS='-Xmx2048 -Xms1024' mvn clean install -DskipTests -Ddocker.namespace=aas-registry-test -Ddocker.password=""

read -p "Press any key to continue... " -n1 -s
cd $OLD_WORK_DIR


