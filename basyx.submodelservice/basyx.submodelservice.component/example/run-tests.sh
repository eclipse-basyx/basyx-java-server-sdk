#!/bin/bash
set -a
# Source the .env file
source .env
# Disable automatic exporting of variables
set +a

run_square() {
  echo "Please enter an integer value:"
  read -r int_value
  response=$(curl http://localhost:$PORT/submodel/submodel-elements/SquareOperation/invoke \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d "{ \"inputArguments\" : [{ \"value\" : { \"modelType\" : \"Property\", \"value\" : \"${int_value}\" }}]}" \
     -s | jq '.outputArguments[0].value.value')
  echo result: $response
}

run_add() {
  echo "Please enter the first integer value:"
  read -r int_value1
  echo "Please enter the second integer value:"
  read -r int_value2
  response=$(curl http://localhost:$PORT/submodel/submodel-elements/BasicOperations.AddOperation/invoke \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d "{ \"inputArguments\" : [{ \"value\" : { \"modelType\" : \"Property\", \"value\" : \"${int_value1}\" }},{ \"value\" : { \"modelType\" : \"Property\", \"value\" : \"${int_value2}\" }}]}" \
     -s | jq '.outputArguments[0].value.value')
  echo result: $response
}

run_hello() {
  response=$(curl http://localhost:$PORT/submodel/submodel-elements/BasicOperations.HelloOperation/invoke \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d "{ }" \
     -s | jq '.outputArguments[0].value.value')
  echo result: $response
}

run_tests() {
  echo We are now running tests with curl
  
  echo The following operations can be invoked:
  echo "'s': square operation"
  echo "'h': hello world operation"
  echo "'a': add operation"
  echo "'e': exit"
  while true; do
    # Nutzer nach Eingabe fragen
    echo "Please enter 's', 'h', 'a' or 'e' to exit:"
    read -r user_input

    # Prüfen, welche Eingabe gemacht wurde
    if [[ "$user_input" == "s" ]]; then
      run_square
    elif [[ "$user_input" == "h" ]]; then
      run_hello
    elif [[ "$user_input" == "a" ]]; then
      run_add
    elif [[ "$user_input" == "e" ]]; then
      echo Exit...
      break 
    else
      echo "Input not valid. Please enter 's', 'h', 'a' or 'e'."
    fi
  done
}

pull_submodel() {
    URL="http://localhost:$PORT/submodel"

    while true; do
        response=$(curl -s -w "\n%{http_code}" "$URL")
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" -eq 200 ]; then
            cat submodel.json
            break
        else
            echo "HTTP-Code: $http_code. Try to connect again after 2 seconds..."
            echo "Check that the server is started!"
            sleep 2
        fi
    done
}

echo "##### Current Submodel #############"
pull_submodel

echo "##### Test #########################"
echo ""
run_tests