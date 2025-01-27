#!/bin/bash

# Define the URL and body data to request the OAuth2 token
TOKEN_URL="http://keycloak:8080/realms/BaSyx/protocol/openid-connect/token"
BODY="client_id=workstation-1&client_secret=nY0mjyECF60DGzNmQUjL81XurSl8etom&grant_type=client_credentials&scope=openid"

# Fetch the OAuth2 token
ACCESS_TOKEN=$(curl -X POST $TOKEN_URL -d $BODY -H "Content-Type: application/x-www-form-urlencoded" | jq -r .access_token)

# Check if the access token was retrieved
if [ -z "$ACCESS_TOKEN" ]; then
    echo "Failed to obtain access token. Exiting."
    exit 1
fi

# Export the token as an environment variable
export ACCESS_TOKEN

echo "Sending POST request to the service..."
curl -X POST http://security-submodel:8081/submodels \
    -d @/initial-submodel.json \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $ACCESS_TOKEN"

echo "Security Submodel initialized successfully!"
