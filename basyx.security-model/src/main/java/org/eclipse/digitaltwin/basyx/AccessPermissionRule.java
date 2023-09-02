package org.eclipse.digitaltwin.basyx;

import java.util.List;

public interface AccessPermissionRule {
    SubjectAttributes getTargetSubjectAttributes();
    List<PermissionsPerObject> getPermissionsPerObject();
    Formula getConstraint();
}
