package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.digitaltwin.basyx.authorization.rbac.ITargetInfo;

import java.util.HashMap;
import java.util.Map;

public class ConceptDescriptionTargetInfo implements ITargetInfo {
    private String conceptDescriptionId;

    public String getConceptDescriptionId() {
        return conceptDescriptionId;
    }

    @JsonCreator
    public ConceptDescriptionTargetInfo(final @JsonProperty("aasId") String conceptDescriptionId) {
        this.conceptDescriptionId = conceptDescriptionId;
    }

    @Override
    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("conceptDescriptionId", conceptDescriptionId);
        return map;
    }
}
