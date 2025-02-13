package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AasDiscoveryDocumentBackend extends CrudRepository<AasDiscoveryDocument, String> {
    
}
