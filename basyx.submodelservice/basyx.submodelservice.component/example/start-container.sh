#!/bin/bash

project_was_built() {
  if find ../target -type f -name "basyx.submodelservice.component*-exec.jar" | grep -q "^"; then
    return 0  
  else
    return 1  
  fi
}

build_maven() {
  echo building maven artifacts ...
  mvn -f ../../../pom.xml clean install -DskipTests
}

run_square() {
  echo "Please enter an integer value:"
  read -r int_value
  response=$(curl http://localhost:8111/submodel/submodel-elements/SquareOperation/invoke \
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
  response=$(curl http://localhost:8111/submodel/submodel-elements/BasicOperations.AddOperation/invoke \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -d "{ \"inputArguments\" : [{ \"value\" : { \"modelType\" : \"Property\", \"value\" : \"${int_value1}\" }},{ \"value\" : { \"modelType\" : \"Property\", \"value\" : \"${int_value2}\" }}]}" \
     -s | jq '.outputArguments[0].value.value')
  echo result: $response
}

run_hello() {
  response=$(curl http://localhost:8111/submodel/submodel-elements/BasicOperations.HelloOperation/invoke \
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

if ! project_was_built; then
  read -p "The maven artifact does not exist. Do you want to build all maven artifacts now? [Y/n]: " build_maven
  build_maven=${build_maven:-Y}
  if [[ "$build_maven" =~ ^[Yy]$ ]]; then
    build_maven;
  else
    echo abort
    return;
  fi
fi

docker-compose up --build --force-recreate --detach --wait
echo service started: http://localhost:8111/submodel

run_tests;

echo "Shutting down containers"
docker-compose down
