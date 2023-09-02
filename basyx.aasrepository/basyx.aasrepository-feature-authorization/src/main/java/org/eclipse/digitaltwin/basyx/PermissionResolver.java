package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;

public interface PermissionResolver {
    public boolean hasPermission(AasRepository aasRepository, String aasId, Action action);

    public boolean hasSubmodelPermission(AasRepository aasRepository, String aasId, String submodelId, Action action);
}
