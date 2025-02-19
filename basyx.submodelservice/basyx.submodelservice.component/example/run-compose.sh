#!/bin/bash
# Automatically export all variables loaded below
set -a
# Source the .env file
source .env
# Disable automatic exporting of variables
set +a

source provide-image.sh

docker compose up