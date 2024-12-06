/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilter;
import org.eclipse.digitaltwin.basyx.core.FilterResolution;
import org.eclipse.digitaltwin.basyx.core.Filter;

/**
 * This class provides an in-memory implementation of the
 * {@link FilterResolution} interface for filtering
 * {@link AssetAdministrationShell} based on specified criteria.
 * 
 * <p>
 * It uses a {@link Predicate} to apply the filter conditions directly on
 * instances of {@link AssetAdministrationShell}. This approach is suitable for
 * scenarios where filtering needs to be performed in memory, for example, on
 * collections already loaded from a data source.
 *
 * <p>
 * The filter criteria are derived from an {@link AasFilter} object, which
 * contains various filtering attributes, including:
 * <ul>
 * <li>AssetKind - the type of asset (e.g., INSTANCE, TYPE, or
 * NOT_APPLICABLE)</li>
 * <li>AssetType - the specific type of asset</li>
 * <li>Ids - a list of asset administration shell IDs to filter by</li>
 * <li>IdShort</li>
 * <li>SpecificAssetIds - a list of specific asset identifiers (name and value
 * pairs)</li>
 * </ul>
 *
 * <p>
 * Each attribute of the filter is matched to the corresponding properties of
 * {@link AssetAdministrationShell} objects, and the filtering logic ensures
 * that only the objects satisfying all specified criteria are returned.
 *
 *
 * @see FilterResolution
 * @see AasFilter
 * @see AssetAdministrationShell
 * 
 * @author danish
 */
public class AasInMemoryFilterResolution implements FilterResolution<Predicate<AssetAdministrationShell>> {

	@Override
	public Predicate<AssetAdministrationShell> applyFilter(Filter filter) {
		return aas -> {

			if (filter == null)
				return true;

			if (!(filter instanceof AasFilter))
				throw new RuntimeException("The provided filter is not of type AasFilter");

			AasFilter aasFilter = (AasFilter) filter;

			AssetKind filterKind = aasFilter.getAssetKind();
			String filterAssetType = aasFilter.getAssetType();
			List<String> filterAasIds = aasFilter.getIds();
			String filterIdShort = aasFilter.getIdShort();
			List<SpecificAssetId> filterSpecificAssetIds = aasFilter.getSpecificAssetIds();

			AssetInformation targetAssetInformation = aas.getAssetInformation();

			AssetKind targetKind = null;
			String targetAssetType = null;
			List<SpecificAssetId> targetSpecificAssetIds = null;

			if (targetAssetInformation != null) {
				targetKind = targetAssetInformation.getAssetKind();
				targetAssetType = targetAssetInformation.getAssetType();
				targetSpecificAssetIds = targetAssetInformation.getSpecificAssetIds();
			}

			String targetAasId = aas.getId();
			String targetIdShort = aas.getIdShort();

			return matchesId(filterAasIds, targetAasId) && matchesIdShort(filterIdShort, targetIdShort) && matchesAssetInfo(filterKind, targetKind, filterAssetType, targetAssetType)
					&& matchesSpecificAssetId(filterSpecificAssetIds, targetSpecificAssetIds);

		};
	}

	private boolean matchesId(List<String> filterAasIds, String targetAasId) {

		if (filterAasIds == null || filterAasIds.isEmpty())
			return true;

		return filterAasIds.contains(targetAasId);
	}

	private boolean matchesIdShort(String filterIdShort, String targetIdShort) {

		if (filterIdShort == null || filterIdShort.isBlank())
			return true;

		if (targetIdShort == null)
			return false;

		return filterIdShort.equals(targetIdShort);
	}

	private boolean matchesSpecificAssetId(List<SpecificAssetId> filterSpecificAssetIds, List<SpecificAssetId> targetSpecificAssetIds) {

		if (filterSpecificAssetIds == null || filterSpecificAssetIds.isEmpty())
			return true;

		if (targetSpecificAssetIds == null)
			return false;

		return filterSpecificAssetIds.stream().allMatch(filterAsset -> targetSpecificAssetIds.stream().anyMatch(targetAsset -> targetAsset.getName().equals(filterAsset.getName()) && targetAsset.getValue().equals(filterAsset.getValue())));
	}

	private boolean matchesAssetInfo(AssetKind filterAssetKind, AssetKind targetAssetKind, String filterAssetType, String targetAssetType) {

		if (filterAssetKind == null)
			return true;

		if (filterAssetKind == AssetKind.INSTANCE)
			return targetAssetKind == AssetKind.INSTANCE;
		else if (filterAssetKind == AssetKind.NOT_APPLICABLE)
			return targetAssetKind == null;
		else if (targetAssetKind != AssetKind.TYPE)
			return false;
		else {
			if (filterAssetType == null)
				return true;

			return filterAssetType.equals(targetAssetType);
		}
	}

}
