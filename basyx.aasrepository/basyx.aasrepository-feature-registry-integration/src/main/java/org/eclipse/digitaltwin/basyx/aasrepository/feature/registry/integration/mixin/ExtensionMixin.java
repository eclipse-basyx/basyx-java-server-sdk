package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ExtensionMixin {
	
	@JsonProperty("semanticId")
	public Reference getSemanticID();
	
	
//	public String getTemplateId();
}
