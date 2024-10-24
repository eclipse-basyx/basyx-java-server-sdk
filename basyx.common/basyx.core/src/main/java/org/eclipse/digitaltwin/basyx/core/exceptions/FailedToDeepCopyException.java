package org.eclipse.digitaltwin.basyx.core.exceptions;

public class FailedToDeepCopyException extends RuntimeException {

    public FailedToDeepCopyException(String objId, Throwable e) {
        super(getMessage(objId), e);
    }

    private static String getMessage(String objId) {
        return "Failed to deep copy object with id " + objId;
    }
}
