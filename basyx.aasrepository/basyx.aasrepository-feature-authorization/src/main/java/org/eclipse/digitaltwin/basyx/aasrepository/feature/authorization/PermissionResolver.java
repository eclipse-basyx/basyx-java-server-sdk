package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

public interface PermissionResolver<AssetAdministrationShellFilterType, SubmodelReferenceFilterType> {
    FilterInfo<AssetAdministrationShellFilterType> getGetAllAasFilterInfo();

    public void getAas(String aasId);

    public void createAas(AssetAdministrationShell aas);

    public void updateAas(String aasId, AssetAdministrationShell aas);

    public void deleteAas(String aasId);

    public FilterInfo<SubmodelReferenceFilterType> getGetSubmodelReferencesFilterInfo(String aasId);

    public void addSubmodelReference(String aasId, Reference submodelReference);

    public void removeSubmodelReference(String aasId, String submodelId);

    public void setAssetInformation(String aasId, AssetInformation aasInfo);

    public void getAssetInformation(String aasId);
}
