package org.eclipse.digitaltwin.basyx.authorization;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.authorization.abac.AllRule;
import org.eclipse.digitaltwin.basyx.authorization.abac.AllRulesWrapper;
import org.eclipse.digitaltwin.basyx.authorization.abac.LogicalComponent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.eclipse.digitaltwin.basyx.authorization.abac.LogicalComponentDeserializer;

public class TestDeserialization {

	public static void main(String[] args) throws Exception {
		String json = """
				       [
	{
		"AllRules": [
			{
				"ATTRIBUTES": [
					{
						"CLAIM": "realm_access.roles"
					}
				],
				"RIGHTS": [
					"READ"
				],
				"ACCESS": "ALLOW",
				"OBJECTS": [
					{
						"IDENTIFIABLE": "aas"
					}
				],
				"FORMULA": {
					"type": "logicalExpression",
					"$and": [
						{
							"type": "simpleExpression",
							"$eq": [
								"$aas.idShort",
								"AAS_123"
							]
						},
						{
							"type": "simpleExpression",
							"$eq": [
								"realm_access.roles",
								"admin"
							]
						}
					]
				}
			}
		]
	},
	{
		"AllRules": [
			{
				"ATTRIBUTES": [
					{
						"CLAIM": "realm_access.roles"
					}
				],
				"RIGHTS": [
					"CREATE"
				],
				"ACCESS": "ALLOW",
				"OBJECTS": [
					{
						"IDENTIFIABLE": "aas"
					}
				],
				"FORMULA": {
					"type": "logicalExpression",
					"$or": [
						{
							"type": "simpleExpression",
							"$eq": [
								"$aas.idShort",
								"Dummy"
							]
						},
						{
							"type": "simpleExpression",
							"$eq": [
								"$aas.id",
								"AAS_123"
							]
						},
						{
							"type": "simpleExpression",
							"$eq": [
								"realm_access.roles",
								"admin"
							]
						}
					]
				}
			}
		]
	}
]

				       """;

		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(LogicalComponent.class, new LogicalComponentDeserializer());
		objectMapper.registerModule(module);

		 // Deserialize into List<AllRulesWrapper>
        List<AllRulesWrapper> wrapperList = objectMapper.readValue(
            json,
            objectMapper.getTypeFactory().constructCollectionType(List.class, AllRulesWrapper.class)
        );

        // Flatten the "AllRules" into a single list
        List<AllRule> flattenedRules = wrapperList.stream()
                                                  .flatMap(wrapper -> wrapper.getAllRules().stream())
                                                  .collect(Collectors.toList());

		// Print flattened list
		System.out.println("Flattened Rules: " + flattenedRules);
	}
}
