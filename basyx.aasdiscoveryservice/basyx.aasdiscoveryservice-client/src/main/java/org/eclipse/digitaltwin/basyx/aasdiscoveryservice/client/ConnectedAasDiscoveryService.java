package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.client.internal.AasDiscoveryServiceApi;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.List;

public class ConnectedAasDiscoveryService implements AasDiscoveryService {

	private final AasDiscoveryServiceApi aasDiscoveryServiceApi;

	public ConnectedAasDiscoveryService(AasDiscoveryServiceApi aasDiscoveryServiceApi) {
		this.aasDiscoveryServiceApi = aasDiscoveryServiceApi;
	}

	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		List<String> assetsId = convertToString(assetIds);
		Integer limit = pInfo.getLimit();
		String cursor = pInfo.getCursor();
		return aasDiscoveryServiceApi.getAllAssetAdministrationShellIdsByAssetLink(assetsId, limit, cursor);
	}

	private static List<String> convertToString(List<AssetLink> assetIds) {
		return assetIds.stream().map(AssetLink::toString).toList();
	}


	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		return aasDiscoveryServiceApi.getAllAssetLinksById(shellIdentifier);
	}

	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier, List<SpecificAssetId> assetIds) {
		return aasDiscoveryServiceApi.postAllAssetLinksById(shellIdentifier,assetIds);
	}

	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		aasDiscoveryServiceApi.deleteAllAssetLinksById(shellIdentifier);

	}
}