package org.eclipse.digitaltwin.basyx.serialization;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.FailedToDeepCopyException;

public final class SubmodelDeepCopyUtil {

    private SubmodelDeepCopyUtil() {
    }

    /**
     * Deep copy a submodel.
     * 
     * @param submodel
     *            The submodel that should be copied.
     * @return A deep copy of the submodel.
     * 
     * @throws FailedToDeepCopyException
     *             If the submodel can not be copied.
     */
    public static Submodel deepCopy(Submodel submodel) {
        try {
            String submodelAsJSON = new JsonSerializer().write(submodel);

            Submodel submodelDeepCopy = new JsonDeserializer().read(submodelAsJSON, Submodel.class);

            submodelDeepCopy.setSubmodelElements(null);

            return submodelDeepCopy;

        } catch (DeserializationException | SerializationException e) {
            throw new FailedToDeepCopyException(submodel.getId(), e);
        }
    }

}
