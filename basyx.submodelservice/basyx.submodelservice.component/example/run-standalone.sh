#!/bin/bash
# Automatically export all variables loaded below
set -a
# Source the .env file
source .env
# Disable automatic exporting of variables
set +a

source provide-image.sh

echo Building image eclipsebasyx/submodel-example:test ...

docker buildx use default
docker buildx build -f Dockerfile.standalone-example --build-arg REVISION=$REVISION -t eclipsebasyx/submodel-example:test . --load

docker run -p $PORT:8081 eclipsebasyx/submodel-example:test 
