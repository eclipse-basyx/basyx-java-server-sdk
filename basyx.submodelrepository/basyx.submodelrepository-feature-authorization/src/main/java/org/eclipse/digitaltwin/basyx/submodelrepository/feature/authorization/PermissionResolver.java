package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

public interface PermissionResolver<SubmodelFilterType, SubmodelElementFilterType> {
    public FilterInfo<SubmodelFilterType> getGetAllSubmodelsFilterInfo();

    public void getSubmodel(String submodelId);

    public void updateSubmodel(String submodelId, Submodel submodel);

    public void createSubmodel(Submodel submodel);

    public void deleteSubmodel(Submodel submodel);

    public FilterInfo<SubmodelElementFilterType> getGetSubmodelElementsFilterInfo(Submodel submodel);

    public void getSubmodelElement(String submodelId, String smeIdShort);

    public void getSubmodelElementValue(String submodelId, String smeIdShort);

    public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value);

    public void createSubmodelElement(String submodelId, SubmodelElement smElement);

    public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement);

    public void deleteSubmodelElement(String submodelId, String idShortPath);

    public void getSubmodelByIdValueOnly(String submodelId);

    public void getSubmodelByIdMetadata(String submodelId);
}
