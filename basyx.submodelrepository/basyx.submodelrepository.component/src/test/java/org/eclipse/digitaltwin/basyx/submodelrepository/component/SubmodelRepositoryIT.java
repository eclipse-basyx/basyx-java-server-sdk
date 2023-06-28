package org.eclipse.digitaltwin.basyx.submodelrepository.component;

import org.eclipse.digitaltwin.basyx.submodelrepository.tck.SubmodelRepositorySubmodelTestDefinedURL;

public class SubmodelRepositoryIT extends SubmodelRepositorySubmodelTestDefinedURL {
	@Override
	protected String getURL() {
		return "http://localhost:8081/submodels";
	}
}
