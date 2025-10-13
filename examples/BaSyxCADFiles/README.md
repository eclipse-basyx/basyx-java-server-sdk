# BaSyx Example for integrating CAD Files

This example provides a setup for integrating CAD files into the BaSyx middleware.
It includes a preconfigured AAS with a submodel containing CAD files as part of File Submodel Element.

The BaSyx AAS Web UI is able to display CAD files in the 3D viewer. The following formats are supported for now:

- glTF
- OBJ
- STL

> [!NOTE]
> Other formats like STEP files can still be uploaded and downloaded, but won't be displayed in the 3D viewer.

## How to run the BaSyx + CAD File Example

1. Open a terminal in this folder
2. Run the following command to start the BaSyx containers:

```bash
docker-compose up -d
```

> To run the example containers, you need to have Docker installed on your device.

## View the working Example

To see the working example, open the [BaSyx AAS Web UI](http://localhost:3000) and navigate to a File Submodel Element with a CAD file. You should see a 3D viewer displaying the CAD file under the `Visualization` tab on the right side of the UI.
