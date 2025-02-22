package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
@Component
public class InMemoryAasDiscoveryDocumentBackend extends InMemoryCrudRepository<AasDiscoveryDocument> implements AasDiscoveryDocumentBackend {

	public InMemoryAasDiscoveryDocumentBackend() {
		super(AasDiscoveryDocument::getShellIdentifier);
	}

}