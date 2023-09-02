package org.eclipse.digitaltwin.basyx;

public enum PermissionKind {
    // Allow the permission given to the subject.
    ALLOW,
    // Explicitly deny the permission given to the subject.
    DENY,
    // The permission is not applicable to the subject.
    NOT_APPLICABLE,
    // It is undefinde whether the permission is allowed, not applicable or denied to the subject.
    UNDEFINED;

    private PermissionKind() {
    }
}
