package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiClient;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {

	@Test
	void emptyCollection_excludedFromJson() {
		ApiClient apiClient = new ApiClient();
		ObjectMapper objectMapper = apiClient.getObjectMapper();
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor();
		descr.id("descriptor_with_empty_list");
		descr.description(Collections.emptyList());

		JsonNode jsonNode = objectMapper.valueToTree(descr);
		JsonNode description = jsonNode.get("description");

		assertThat(description).isNull();
	}

}
