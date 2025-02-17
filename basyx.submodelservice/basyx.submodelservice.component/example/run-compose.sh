#!/bin/bash
# Automatically export all variables loaded below
set -a
# Source the .env file
source .env
# Disable automatic exporting of variables
set +a


source provide-image.sh

docker compose up --build --force-recreate --detach --wait
echo service started: http://localhost:$PORT/submodel

run_tests; 

echo "Shutting down containers"
docker-compose down
