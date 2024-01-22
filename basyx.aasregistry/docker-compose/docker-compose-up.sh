#!/bin/bash

OLD_WORK_DIR=$(pwd)
trap 'cd $OLD_WORK_DIR' EXIT

cd $(dirname "${BASH_SOURCE[0]}")

docker-compose up -d --build --force-recreate

echo Done!
echo ""
echo Portainer: http://localhost:9090
echo AKHQ: http://localhost:8086
echo MongoExpress: http://localhost:8081
echo Registry - kafka,mongodb: http://localhost:8020
echo Registry - kafka,mongodb: http://localhost:8021/api/v3.0.1
echo Registry - kafka,mongodb: http://localhost:8024
echo Registry - kafka,mem: http://localhost:8030
echo Registry - kafka,mem: http://localhost:8031/api/v3.0.1
echo Registry - log,mongodb: http://localhost:8050
echo Registry - log,mongodb: http://localhost:8051/api/v3.0.1
echo Registry - log,mem: http://localhost:8040
echo Registry - log,mem: http://localhost:8066/api/v3.0.1
echo ""
read -p "Press any key to continue... " -n1 -s
cd $OLD_WORK_DIR

