package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.Delegate;

import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.*;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class DiscoveryEnhancedShellDescriptors implements ShellDescriptorsApiDelegate {

    @Autowired
    MongoDBCrudAasDiscovery mongoDBCrudAasDiscovery;

    private final ShellDescriptorsApiDelegate originalDelegate;


    @Autowired
    public DiscoveryEnhancedShellDescriptors(
            ObjectProvider<ShellDescriptorsApiDelegate> delegateProvider) {
        this.originalDelegate = delegateProvider.stream()
                .filter(delegate -> !(delegate instanceof DiscoveryEnhancedShellDescriptors))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No original ShellDescriptorsApiDelegate found"));
    }


    @Override
    public ResponseEntity<GetAssetAdministrationShellDescriptorsResult> getAllAssetAdministrationShellDescriptors(
            Integer limit, String cursor, AssetKind assetKind, String assetType) {
        return originalDelegate.getAllAssetAdministrationShellDescriptors(limit, cursor, assetKind, assetType);
    }

    @Override
    public ResponseEntity<Void> deleteAllShellDescriptors() {
        ResponseEntity<GetAssetAdministrationShellDescriptorsResult> allShells = getAllAssetAdministrationShellDescriptors(null, null, null, null);
        List<AssetAdministrationShellDescriptor> allShellIdentifiers = allShells.getBody().getResult();
        allShellIdentifiers.parallelStream().forEach(assetAdministrationShellDescriptor ->
           mongoDBCrudAasDiscovery.deleteAllAssetLinksById(Base64.getEncoder().encodeToString(assetAdministrationShellDescriptor.getId().getBytes())));
        return originalDelegate.deleteAllShellDescriptors();
    }

    @Override
    public ResponseEntity<Void> deleteAssetAdministrationShellDescriptorById(String aasIdentifier) {
        return originalDelegate.deleteAssetAdministrationShellDescriptorById(aasIdentifier);
    }

    @Override
    public ResponseEntity<Void> deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
        return originalDelegate.deleteSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier);
    }

    @Override
    public ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptorsThroughSuperpath(String aasIdentifier, Integer limit, String cursor) {
        return originalDelegate.getAllSubmodelDescriptorsThroughSuperpath(aasIdentifier, limit, cursor);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorById(String aasIdentifier) {
        return originalDelegate.getAssetAdministrationShellDescriptorById(aasIdentifier);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
        return originalDelegate.getSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier);
    }

    @Override
    public ResponseEntity<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        String encodedId = Base64.getEncoder().encodeToString(assetAdministrationShellDescriptor.getId().getBytes());
        @Valid List<org.eclipse.digitaltwin.basyx.aasregistry.model.@Valid SpecificAssetId> ids = assetAdministrationShellDescriptor.getSpecificAssetIds();
        List<SpecificAssetId> specificAssetIds = ids.stream()
                .map(rId -> {
                    SpecificAssetId assetId = new DefaultSpecificAssetId();
                    assetId.setName(rId.getName());
                    assetId.setValue(rId.getValue());
                    return assetId;
                }).collect(Collectors.toList());

        mongoDBCrudAasDiscovery.createAllAssetLinksById(encodedId, specificAssetIds);
        return originalDelegate.postAssetAdministrationShellDescriptor(assetAdministrationShellDescriptor);
    }

    @Override
    public ResponseEntity<SubmodelDescriptor> postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor submodelDescriptor) {
        return originalDelegate.postSubmodelDescriptorThroughSuperpath(aasIdentifier, submodelDescriptor);
    }

    @Override
    public ResponseEntity<Void> putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        return originalDelegate.putAssetAdministrationShellDescriptorById(aasIdentifier, assetAdministrationShellDescriptor);
    }

    @Override
    public ResponseEntity<Void> putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor submodelDescriptor) {
        return originalDelegate.putSubmodelDescriptorByIdThroughSuperpath(aasIdentifier, submodelIdentifier, submodelDescriptor);
    }
}
