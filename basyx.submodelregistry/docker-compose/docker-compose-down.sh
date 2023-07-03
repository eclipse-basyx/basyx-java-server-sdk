#!/bin/bash

OLD_WORK_DIR=$(pwd)
trap 'cd $OLD_WORK_DIR' EXIT

cd $(dirname "${BASH_SOURCE[0]}")

docker-compose down

cd $OLD_WORK_DIR