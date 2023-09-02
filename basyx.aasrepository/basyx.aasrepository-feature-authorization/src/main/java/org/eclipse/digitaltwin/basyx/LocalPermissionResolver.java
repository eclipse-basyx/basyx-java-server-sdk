package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.springframework.stereotype.Component;

@Component
public class LocalPermissionResolver implements PermissionResolver {
    private final String SECURITY_SUBMODEL_ID = "SECURITY";
    @Override
    public boolean hasPermission(AasRepository aasRepository, String aasId, Action action) {
        return true;
    }

    @Override
    public boolean hasSubmodelPermission(AasRepository aasRepository, String aasId, String submodelId, Action action) {
        return true;
    }
}
