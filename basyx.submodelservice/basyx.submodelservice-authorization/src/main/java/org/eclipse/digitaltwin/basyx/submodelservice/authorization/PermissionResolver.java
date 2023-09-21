package org.eclipse.digitaltwin.basyx.submodelservice.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

public interface PermissionResolver<SubmodelElementFilterType> {
    public void deleteSubmodelElement(Submodel submodel, String idShortPath);

    public FilterInfo<SubmodelElementFilterType> getGetSubmodelElementsFilterInfo(Submodel submodel);

    public void getSubmodelElement(Submodel submodel, String idShortPath);

    public void getSubmodelElementValue(Submodel submodel, String idShortPath);

    public FilterInfo<SubmodelElementFilterType> getSubmodelValueOnlyFilterInfo(Submodel submodel);

    public void setSubmodelElementValue(Submodel submodel, String idShortPath, SubmodelElementValue body);

    public void createSubmodelElement(Submodel submodel, SubmodelElement body);

    public void createSubmodelElement(Submodel submodel, String idShortPath, SubmodelElement body);

    public void getSubmodelMetaData(Submodel submodel);

    public void getSubmodel();
}
