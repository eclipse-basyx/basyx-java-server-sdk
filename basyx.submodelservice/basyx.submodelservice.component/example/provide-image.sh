#!/bin/bash
# Automatically export all variables loaded below
set -a
# Source the .env file
source .env
# Disable automatic exporting of variables
set +a

IMAGE=eclipsebasyx/submodel-service:$REVISION

image_available() {
  if docker image inspect "$IMAGE" > /dev/null 2>&1; then
      echo "Image $IMAGE exists locally."
  else 
    echo "Image $IMAGE not found locally."
    if $PULL -eq "true"; then 
      echo "Attempting to pull..."
      docker pull "$IMAGE"
      if [ $? -ne 0 ]; then
        echo "Failed to pull image $IMAGE"
        return 1
      else
        echo "Successfully pulled image $IMAGE."
      fi
    fi
  fi 
}

# building the image could be relevant if the 
build_maven() {
  echo building maven artifacts ...
  mvn -f ../../../pom.xml clean install -DskipTests
  mvn -f ../pom.xml install -Ddocker.namespace=eclipsebasyx -Drevision=$REVISION  -DskipTests
}

if ! image_available; then
  read -p "The image does not exist. Do you want to build all maven artifacts and the image now? [Y/n]: " build_maven
  build_maven=${build_maven:-Y}
  if [[ "$build_maven" =~ ^[Yy]$ ]]; then
    build_maven; 
  else
    echo abort
    return;
  fi
fi