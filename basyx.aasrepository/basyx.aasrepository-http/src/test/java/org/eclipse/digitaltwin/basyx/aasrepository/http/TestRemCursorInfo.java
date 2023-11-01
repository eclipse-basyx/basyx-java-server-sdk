package org.eclipse.digitaltwin.basyx.aasrepository.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRemCursorInfo {
//    public static void main(String[] args) throws Exception {
//        // Input JSON as a string
//        String inputJson = "{\"paging_metadata\":{\"cursor\":\"Y3VzdG9tSWRlbnRpZmllcg\"},\"result\":[{\"modelType\":\"AssetAdministrationShell\",\"assetInformation\":{\"assetKind\":\"Instance\",\"globalAssetId\":\"globalAssetId\"},\"id\":\"customIdentifier\",\"idShort\":\"ExampleMotor\"}]}";
//
//        // Create ObjectMapper
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Parse the JSON into a JsonNode
//        JsonNode rootNode = mapper.readTree(inputJson);
//
//        // Create a new ObjectNode to represent the modified JSON
//        ObjectNode modifiedNode = JsonNodeFactory.instance.objectNode();
//        
//        // Add the "result" field back to the modified JSON
//        modifiedNode.set("result", rootNode.get("result"));
//
//        // Convert the modified ObjectNode back to a JSON string
//        String modifiedJson = mapper.writeValueAsString(modifiedNode);
//
//        System.out.println(modifiedJson);
//    }
    
    public static void main(String[] args) throws Exception {
        // Input JSON as a string
        String inputJson = "{\"paging_metadata\":{\"cursor\":\"Y3VzdG9tSWRlbnRpZmllcg\"},\"result\":[{\"modelType\":\"AssetAdministrationShell\",\"assetInformation\":{\"assetKind\":\"Instance\",\"globalAssetId\":\"globalAssetId\"},\"id\":\"customIdentifier\",\"idShort\":\"ExampleMotor\"}]}";

        // Create ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        // Parse the JSON into a JsonNode
        JsonNode rootNode = mapper.readTree(inputJson);

        // If paging_metadata exists, remove the "cursor" field
        if (rootNode.has("paging_metadata")) {
            ObjectNode pagingMetadata = (ObjectNode) rootNode.get("paging_metadata");
            pagingMetadata.remove("cursor");
        }

        // Convert the modified JsonNode back to a JSON string
        String modifiedJson = mapper.writeValueAsString(rootNode);

        System.out.println(modifiedJson);
    }
}
