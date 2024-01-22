#!/bin/sh
OLD_WORK_DIR=$(pwd)
trap 'cd $OLD_WORK_DIR' EXIT

cd $(dirname "${BASH_SOURCE[0]}")/..

MAVEN_OPS='-Xmx2048 -Xms1024' mvn clean install -DskipTests -Ddocker.namespace=submodel-registry-test -Ddocker.password=""

cd $OLD_WORK_DIR


