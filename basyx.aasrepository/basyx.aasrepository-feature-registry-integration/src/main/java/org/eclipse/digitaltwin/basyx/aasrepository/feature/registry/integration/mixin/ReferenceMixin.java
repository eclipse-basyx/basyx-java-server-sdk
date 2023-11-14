package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.mixin;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ReferenceMixin {
	
	@JsonProperty("referredSemanticId")
	public Reference getReferredSemanticID();
	
	
//	public String getTemplateId();
}
