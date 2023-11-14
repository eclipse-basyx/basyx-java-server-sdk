package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AdministrativeInformationMixin {
	
	@JsonProperty("templateID")
	public String getTemplateId();
}
