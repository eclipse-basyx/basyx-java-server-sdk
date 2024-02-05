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
echo Registry - kafka,mongodb: http://localhost:8024
echo Registry - kafka,mongodb: http://localhost:8025/api/v3.0.1
echo Registry - kafka,mem: http://localhost:8035
echo Registry - kafka,mem: http://localhost:8036/api/v3.0.1
echo Registry - log,mongodb: http://localhost:8053
echo Registry - log,mongodb: http://localhost:8054/api/v3.0.1
echo Registry - log,mem: http://localhost:8043
echo Registry - log,mem: http://localhost:8044/api/v3.0.1
echo ""
read -p "Press any key to continue... " -n1 -s
cd $OLD_WORK_DIR
