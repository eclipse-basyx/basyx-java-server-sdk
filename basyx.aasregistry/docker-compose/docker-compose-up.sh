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
echo Registry - kafka,mongodb: http://localhost:8020/api/v3.0
echo Registry - kafka,mongodb - external config - : http://localhost:8024/api/v3.0
echo Registry - kafka,mem: http://localhost:8030/api/v3.0
echo Registry - log,mongodb: http://localhost:8050/api/v3.0
echo Registry - log,mem: http://localhost:8040/api/v3.0
echo ""
read -p "Press any key to continue... " -n1 -s
cd $OLD_WORK_DIR
