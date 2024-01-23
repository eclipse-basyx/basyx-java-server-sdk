package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;

import java.util.ArrayList;
import java.util.List;

public class AasDiscoveryUtils {

	public static List<AssetLink> deriveAssetLinksFromShell(AssetAdministrationShell shell) {
		List<SpecificAssetId> specificAssetIds = shell.getAssetInformation().getSpecificAssetIds();

		return deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
	}

	public static List<AssetLink> deriveAssetLinksFromSpecificAssetIds(List<SpecificAssetId> specificAssetIds) {
		List<AssetLink> assetLinks = new ArrayList<>();
		specificAssetIds.forEach(assetId -> assetLinks.add(new AssetLink(assetId.getName(), assetId.getValue())));
		return assetLinks;
	}

	public static AssetAdministrationShell deriveShellFromIdAndSpecificAssetIds(String shellId, List<SpecificAssetId> specificAssetIds) {
		DefaultAssetInformation assetInformation = new DefaultAssetInformation.Builder().specificAssetIds(specificAssetIds).build();

		return new DefaultAssetAdministrationShell.Builder().id(shellId).assetInformation(assetInformation).build();
	}
}
