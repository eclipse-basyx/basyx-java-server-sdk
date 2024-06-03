# BaSyx Setup
This is your BaSyx setup. To run the BaSyx containers, you need to have Docker installed on your device.

## How to run the BaSyx containers
1. Extract the zip file on your device.
2. Open a terminal and navigate to the extracted folder.
3. Run the following command to start the BaSyx containers:
```
docker-compose up -d
```

## Access the BaSyx containers
- AAS Environment: [http://localhost:8081](http://localhost:8081)
- AAS Registry: [http://localhost:8082](http://localhost:8082)
- Submodel Registry: [http://localhost:8083](http://localhost:8083)

## Include your own Asset Administration Shells
To include your own Asset Administration Shells, you can either put them in the `aas` folder or upload them via the AAS Web UI.