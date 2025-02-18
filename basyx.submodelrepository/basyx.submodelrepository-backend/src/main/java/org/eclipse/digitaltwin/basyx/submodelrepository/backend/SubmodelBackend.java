package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelFileOperations;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmodelBackend extends CrudRepository<Submodel, String>, SubmodelOperations, SubmodelFileOperations {
    
}
