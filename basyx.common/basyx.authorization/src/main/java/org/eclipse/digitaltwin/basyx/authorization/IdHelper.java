package org.eclipse.digitaltwin.basyx.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

import java.util.stream.Collectors;

public class IdHelper {
    public static String getSubmodelSemanticIdString(Reference reference) {
        if (reference == null) {
            return null;
        }
        return reference.getKeys().stream().map(Key::getValue).collect(Collectors.joining(";"));
    }
}
