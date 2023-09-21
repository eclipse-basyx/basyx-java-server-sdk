package org.eclipse.digitaltwin.basyx.aasregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

import javax.validation.Valid;

public interface PermissionResolver<AssetAdministrationShellDescriptorFilterType, SubmodelDescriptorFilterType> {

    public void deleteAssetAdministrationShellDescriptorById(String aasIdentifier);

    public void deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier);

    public FilterInfo<AssetAdministrationShellDescriptorFilterType> getGetAllAssetAdministrationShellDescriptorsFilterInfo();

    public FilterInfo<SubmodelDescriptorFilterType> getGetAllSubmodelDescriptorsThroughSuperpathFilterInfo();

    public void getAssetAdministrationShellDescriptorById(String aasIdentifier);

    public void getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier);

    public void postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor body);

    public void putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor body);

    public void postAssetAdministrationShellDescriptor(@Valid AssetAdministrationShellDescriptor body);

    public void putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor descriptor);

    public FilterInfo<AssetAdministrationShellDescriptorFilterType> getDeleteAllShellDescriptorsFilterInfo();
}
