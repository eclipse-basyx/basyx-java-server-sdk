package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

import java.util.List;

public interface AccessControl {
    List<AccessPermissionRule> getAccessPermissionRules();
    Reference getSelectableSubjectAttributes();
    Reference defaultSubjectAttributes();
    Reference getSelectablePermissions();
    Reference getDefaultPermissions();
    Reference getSelectableEnvironmentAttributes();
    Reference getDefaultEnvironmentAttributes();
}
