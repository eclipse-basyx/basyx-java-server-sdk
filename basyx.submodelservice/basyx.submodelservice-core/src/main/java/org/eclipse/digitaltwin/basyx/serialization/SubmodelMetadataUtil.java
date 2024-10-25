package org.eclipse.digitaltwin.basyx.serialization;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.FailedToDeepCopyException;

public final class SubmodelMetadataUtil {

    private SubmodelMetadataUtil() {
    }

    /**
     * Returns a new submodel that only contains the metadata of the given submodel.
     *
     * @param submodel
     * @return A new submodel with the metadata of the given submodel.
     */
    public static Submodel extractMetadata(Submodel submodel) {
        Submodel submodelDeepCopy = deepCopy(submodel);

        submodelDeepCopy.setSubmodelElements(null);

        return submodelDeepCopy;
    }

    /**
     * Deep copy a submodel.
     * 
     * @param submodel
     * @return A deep copy of the submodel.
     * 
     * @throws FailedToDeepCopyException
     * 
     */
    public static Submodel deepCopy(Submodel submodel) {
        try {
            String submodelAsJSON = new JsonSerializer().write(submodel);

            return new JsonDeserializer().read(submodelAsJSON, Submodel.class);

        } catch (DeserializationException | SerializationException e) {
            throw new FailedToDeepCopyException(submodel.getId(), e);
        }
    }
}
