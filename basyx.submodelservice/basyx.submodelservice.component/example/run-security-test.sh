#!/bin/bash

# =============================================
# BaSyx Submodel Authorization Test Script
# ---------------------------------------------
# This script fetches an access token from Keycloak
# and performs authorized/unauthorized operations
# against a Submodel REST API.
# =============================================

# --- Keycloak credentials ---
TOKEN_URL="http://localhost/realms/BaSyx/protocol/openid-connect/token"
CLIENT_ID="workstation-1"
CLIENT_SECRET="nY0mjyECF60DGzNmQUjL81XurSl8etom"

# --- Fetch Access Token ---
echo "Requesting access token from Keycloak..."

ACCESS_TOKEN=$(curl -s -X POST "$TOKEN_URL" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

# --- Check if token was retrieved ---
if [ -z "$ACCESS_TOKEN" ]; then
  echo "Failed to retrieve access token."
  return 1
fi

echo "Access token received."

# ---------------------------------------------
# GET /submodel (should succeed)
# ---------------------------------------------
echo ""
echo "================================="
echo "Calling 'GET /submodel' (should succeed)"

curl -s -w "\n->  HTTP Status Code: %{http_code}\n" \
  -X GET "http://localhost:8123/submodel" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Accept: application/json"

# ---------------------------------------------
# POST /BasicOperations.AddOperation/invoke (should succeed)
# ---------------------------------------------
echo ""
echo "================================="
echo "Calling 'AddOperation' (should succeed)"

curl -s -w "\n->  HTTP Status Code: %{http_code}\n" \
  -X POST "http://localhost:8123/submodel/submodel-elements/BasicOperations.AddOperation/invoke" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "inputArguments": [
      { "value": { "modelType": "Property", "value": "5" }},
      { "value": { "modelType": "Property", "value": "4" }}
    ]
  }'

# ---------------------------------------------
# POST /SquareOperation/invoke (should fail!)
# ---------------------------------------------
echo ""
echo "================================="
echo "Calling 'SquareOperation' (should fail!)"

curl -s -w "\n-> HTTP Status Code: %{http_code}\n" \
  -X POST "http://localhost:8123/submodel/submodel-elements/SquareOperation/invoke" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "inputArguments": [
      { "value": { "modelType": "Property", "value": "5" }}
    ]
  }'