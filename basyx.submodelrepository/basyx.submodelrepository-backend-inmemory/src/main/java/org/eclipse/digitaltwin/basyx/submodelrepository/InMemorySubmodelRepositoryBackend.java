package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SubmodelRepositoryBackend;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
public class InMemorySubmodelRepositoryBackend extends InMemoryCrudRepository<Submodel> implements SubmodelRepositoryBackend {

    public InMemorySubmodelRepositoryBackend() {
        super(Submodel::getId);
    }
    
}
