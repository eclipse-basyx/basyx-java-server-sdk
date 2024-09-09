#!/bin/bash

# Check if the correct number of arguments are provided
if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <image_name> <version> <container_name>"
  exit 1
fi

# Assign input arguments to variables
IMAGE_NAME=$1
VERSION=$2
CONTAINER_NAME=$3

# Run the Docker container
docker run -d --name $CONTAINER_NAME $IMAGE_NAME:$VERSION

# Initialize variables
max_checks=24  # 2 minutes total (120 seconds / 5 seconds per check)
sleep_interval=5  # Interval in seconds between checks
check_count=0

# Loop to check health status
while [ $check_count -lt $max_checks ]; do
  if [ "$(docker inspect --format='{{.State.Health.Status}}' $CONTAINER_NAME)" == "healthy" ]; then
    echo "$CONTAINER_NAME started successfully and is healthy."
    break
  else
    echo "Waiting for $CONTAINER_NAME to become healthy..."
    check_count=$((check_count + 1))
    sleep $sleep_interval
  fi
done

# If the container is still not healthy after the loop
if [ $check_count -eq $max_checks ]; then
  echo "$CONTAINER_NAME failed to start or is unhealthy after 2 minutes."
  docker logs $CONTAINER_NAME
  exit 1
fi

# Stop and remove the container after testing
docker stop $CONTAINER_NAME
docker rm $CONTAINER_NAME
