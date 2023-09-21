package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;

import java.util.stream.Collectors;

public class IdHelper {
    public static String getSubmodelDescriptorSemanticIdString(Reference reference) {
        if (reference == null) {
            return null;
        }
        return reference.getKeys().stream().map(Key::getValue).collect(Collectors.joining(";"));
    }
}
