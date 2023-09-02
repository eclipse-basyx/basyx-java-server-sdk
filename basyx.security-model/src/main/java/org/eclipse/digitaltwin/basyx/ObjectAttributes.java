package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import java.util.List;

public interface ObjectAttributes {
    // should at least have 1 element
    List<Reference> getObjectAttribute();
}
