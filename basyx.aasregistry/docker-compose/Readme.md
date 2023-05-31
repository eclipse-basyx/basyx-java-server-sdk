# Docker Compose

The folder content is an example on how to include the aas-registry docker images in a docker compose setup.

Look at the docker-compose file to see how additional application properties can be applied.

Start your docker daemon and 'cd' into this folder from your shell. 

Run *build-images.sh* to create the referenced docker images with a specific docker-name-prefix *aasregistry-test* for testing and call *docker-compose-up.sh* to start the docker stack and *docker-compose-down-sh* to tear it down again.

After invoking the startup script, follow the links, shown in the console, to the aas-registry Swagger-UI to test the backend. 

The aas and submodel ids are base64 url encoded and transmitted as bytes. Run 'java GenerateByteUrlEncodedBase64Id.java MyId' (java 11 is required) to create a byte array of your id (here 'MyId') and use the output as path variable at these byte array path positions in Swagger-UI.