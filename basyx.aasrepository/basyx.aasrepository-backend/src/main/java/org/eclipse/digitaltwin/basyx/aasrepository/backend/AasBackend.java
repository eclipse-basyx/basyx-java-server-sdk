package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.springframework.data.repository.CrudRepository;

public interface AasBackend extends CrudRepository<AssetAdministrationShell, String> {

}
