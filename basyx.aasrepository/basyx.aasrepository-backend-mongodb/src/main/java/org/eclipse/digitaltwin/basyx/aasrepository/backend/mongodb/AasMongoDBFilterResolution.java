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

package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilter;
import org.eclipse.digitaltwin.basyx.core.FilterResolution;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * This class implements the {@link FilterResolution} interface to provide a
 * MongoDB-based filter resolution for {@link AssetAdministrationShell}.
 * 
 * <p>
 * It constructs a MongoDB {@link AggregationOperation} with filtering
 * conditions based on the provided {@link AasFilter}. This approach is designed
 * for scenarios where filtering should be performed within MongoDB, allowing
 * efficient querying directly on the database layer.
 *
 * <p>
 * The filtering criteria supported by this class include:
 * <ul>
 * <li>AssetKind - allows filtering by specific asset kinds (e.g., INSTANCE,
 * TYPE, NOT_APPLICABLE)</li>
 * <li>AssetType - filters by the type of asset as a string</li>
 * <li>Ids - matches a list of asset administration shell IDs using MongoDB's
 * {@code $in} operator</li>
 * <li>IdShort</li>
 * <li>SpecificAssetIds - matches specific asset identifier name-value pairs
 * using {@code $elemMatch}</li>
 * </ul>
 *
 * <p>
 * The criteria construction ensures that the generated MongoDB query is as flat
 * as possible, minimizing unnecessary nesting for performance efficiency. In
 * addition, the class performs type checks for ID fields, ensuring
 * compatibility with MongoDB's expected formats.
 *
 * @see FilterResolution
 * @see AasFilter
 * @see AggregationOperation
 * @see AssetAdministrationShell
 * 
 * @author danish
 */
public class AasMongoDBFilterResolution implements FilterResolution<AggregationOperation> {

	public static final String ASSET_TYPE = "assetInformation.assetType";
	public static final String ASSET_KIND = "assetInformation.assetKind";
	public static final String IDENTIFIER = "_id";
	public static final String ID_SHORT = "idShort";
	public static final String SPECIFIC_ASSET_ID_ARRAY = "assetInformation.specificAssetIds";
	public static final String SPECIFIC_ASSET_ID_NAME = "name";
	public static final String SPECIFIC_ASSET_ID_VALUE = "value";

	@Override
	public AggregationOperation applyFilter(Filter filter) {

		Criteria criteria = new Criteria();

		if (filter == null)
			return new MatchOperation(criteria);

		if (!(filter instanceof AasFilter))
			throw new RuntimeException("The provided filter is not of type AasFilter");

		AasFilter aasFilter = (AasFilter) filter;

		AssetKind kind = aasFilter.getAssetKind();
		if (kind == AssetKind.NOT_APPLICABLE)
			criteria.and(ASSET_KIND).exists(false);
		else if (kind != null)
			criteria.and(ASSET_KIND).is(kind.name());

		String assetType = aasFilter.getAssetType();
		if (assetType != null)
			criteria.and(ASSET_TYPE).is(assetType);

		List<String> aasIds = aasFilter.getIds();
		if (aasIds != null && !aasIds.isEmpty())
			criteria.and(IDENTIFIER).in(aasIds);

		String idShort = aasFilter.getIdShort();
		if (idShort != null && !idShort.isBlank())
			criteria.and(ID_SHORT).is(idShort);

		List<SpecificAssetId> specificAssetIds = aasFilter.getSpecificAssetIds();
		if (specificAssetIds != null && !specificAssetIds.isEmpty()) {
			Criteria[] specificCriteriaArray = specificAssetIds.stream()
					.map(specId -> Criteria.where(SPECIFIC_ASSET_ID_ARRAY).elemMatch(Criteria.where(SPECIFIC_ASSET_ID_NAME).is(specId.getName()).and(SPECIFIC_ASSET_ID_VALUE).is(specId.getValue()))).toArray(Criteria[]::new);
			criteria.andOperator(specificCriteriaArray);
		}

		return new MatchOperation(criteria);
	}
}
