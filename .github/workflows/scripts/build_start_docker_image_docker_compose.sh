#!/bin/bash

# Check if the correct number of arguments are provided
if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <path_to_docker_compose_file> <container_name>"
  exit 1
fi

# Run the Docker container
docker compose -f $1 up -d

#Check every 5 seconds if the compose is healthy
max_checks=24  # 2 minutes total (120 seconds / 5 seconds per check)
sleep_interval=5  # Interval in seconds between checks
check_count=0

# Loop to check health status
while [ $check_count -lt $max_checks ]; do
  if [ "$(docker inspect --format='{{.State.Health.Status}}' $2)" == "healthy" ]; then
    echo "$2 started successfully and is healthy."
    break
  else
    echo "Waiting for $2 to become healthy..."
    check_count=$((check_count + 1))
    sleep $sleep_interval
  fi
done

# If the container is still not healthy after the loop
if [ $check_count -eq $max_checks ]; then
  echo "$2 failed to start or is unhealthy after 2 minutes."
  docker logs $2
  exit 1
fi
# Stop and remove all container
docker compose -f $1 down

