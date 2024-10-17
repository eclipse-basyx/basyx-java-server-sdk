package org.eclipse.digitaltwin.basyx.gateway.core;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.BaSyxComponentNotHealthyException;

/**
 * Gateway Interface
 *
 * @author fried
 */
public interface Gateway {

    public void createAAS(AssetAdministrationShell aas, String aasRepository, String aasRegistry) throws BaSyxComponentNotHealthyException;
}
