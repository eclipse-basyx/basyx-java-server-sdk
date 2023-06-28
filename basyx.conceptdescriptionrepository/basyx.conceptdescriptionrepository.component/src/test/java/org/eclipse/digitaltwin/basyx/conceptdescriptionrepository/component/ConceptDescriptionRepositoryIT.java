package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.component;

import org.eclipse.digitaltwin.basyx.aasrepository.tck.ConceptDescriptionRepositoryTestDefinedURL;

public class ConceptDescriptionRepositoryIT extends ConceptDescriptionRepositoryTestDefinedURL {
	@Override
	protected String getURL() {
		return "http://localhost:8081/concept-descriptions";
	}
}
