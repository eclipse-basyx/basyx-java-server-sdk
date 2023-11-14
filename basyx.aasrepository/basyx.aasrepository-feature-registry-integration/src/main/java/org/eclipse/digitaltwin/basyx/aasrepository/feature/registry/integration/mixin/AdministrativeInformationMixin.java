package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AdministrativeInformationMixin {
	
	@JsonProperty("templateId")
	public String getTemplateID();
	
	
//	public String getTemplateId();
}
