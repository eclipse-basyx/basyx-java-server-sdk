package org.eclipse.digitaltwin.basyx.aasrepository.component;

import org.eclipse.digitaltwin.basyx.aasrepository.tck.AasRepositoryTestDefinedURL;

public class AASRepositoryIT extends AasRepositoryTestDefinedURL {
	@Override
	protected String getURL() {
		return "http://localhost:8081/shells";
	}
}
