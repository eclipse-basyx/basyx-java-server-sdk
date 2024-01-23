package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

public class AasDiscoveryDocument {
	private String shellIdentifier;
	private Set<AssetLink> assetLinks;
	private List<SpecificAssetId> specificAssetIds;

	public AasDiscoveryDocument() {
	}

	public AasDiscoveryDocument(String shellIdentifier, Set<AssetLink> assetLinks, List<SpecificAssetId> specificAssetIds) {
		this.shellIdentifier = shellIdentifier;
		this.assetLinks = assetLinks;
		this.specificAssetIds = specificAssetIds;
	}

	public String getShellIdentifier() {
		return shellIdentifier;
	}

	public Set<AssetLink> getAssetLinks() {
		return assetLinks;
	}

	public List<SpecificAssetId> getSpecificAssetIds() {
		return specificAssetIds;
	}
}
