#!/bin/bash

OLD_WORK_DIR=$(pwd)
trap 'cd $OLD_WORK_DIR' EXIT

cd $(dirname "${BASH_SOURCE[0]}")

docker-compose up -d --build --force-recreate

echo Done!
echo ""
echo Portainer: http://localhost:9091
echo AKHQ: http://localhost:8087
echo MongoExpress: http://localhost:8082
echo Registry - kafka,mongodb: http://localhost:8021/api/v3.0
echo Registry - kafka,mem: http://localhost:8031/api/v3.0
echo Registry - log,mongodb: http://localhost:8051/api/v3.0
echo Registry - log,mem: http://localhost:8041/api/v3.0
echo ""
read -p "Press any key to continue... " -n1 -s
cd $OLD_WORK_DIR
