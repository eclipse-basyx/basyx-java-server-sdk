package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

public interface Permission {
    Reference getPermission();
    PermissionKind getKindOfPermission();
}
