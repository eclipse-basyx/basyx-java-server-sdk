package org.eclipse.digitaltwin.basyx.aasrepository.backend.mongodb;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilter;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;

public class TestAasMongoDBFilterResolution {

	private AasMongoDBFilterResolution filterResolution;

	@Before
	public void setUp() {
		filterResolution = new AasMongoDBFilterResolution();
	}

	@Test(expected = RuntimeException.class)
	public void testInvalidFilterType() {
		Filter invalidFilter = new Filter() {
			@Override
			public List<String> getIds() {
				return null;
			}
		};

		filterResolution.applyFilter(invalidFilter);
	}

	@Test
	public void testAllValuesMatch() {
		AasFilter filter = prepareFilter();

		AggregationOperation operation = filterResolution.applyFilter(filter);
		assertTrue(operation instanceof MatchOperation);

		Document queryDocument = ((MatchOperation) operation).toDocument(Aggregation.DEFAULT_CONTEXT);
		Document matchDocument = (Document) queryDocument.get("$match");

		assertEquals("INSTANCE", matchDocument.get("assetInformation.assetKind"));
		assertEquals("asset-type", matchDocument.get("assetInformation.assetType"));
		assertEquals("matching-id-short", matchDocument.get("idShort"));

		Document idCondition = (Document) matchDocument.get("_id");
		assertNotNull(idCondition);
		assertEquals(Collections.singletonList("matching-id"), idCondition.get("$in"));

		@SuppressWarnings("unchecked")
		List<Document> andConditions = (List<Document>) matchDocument.get("$and");
		assertNotNull(andConditions);
		assertEquals(1, andConditions.size());

		Document specificAssetIdsCondition = andConditions.get(0);
		Document elemMatch = (Document) ((Document) specificAssetIdsCondition.get("assetInformation.specificAssetIds")).get("$elemMatch");
		assertNotNull(elemMatch);

		assertEquals("specific-id-name", elemMatch.get("name"));
		assertEquals("specific-id-value", elemMatch.get("value"));
	}

	@Test
	public void testOneValueChanged() {
		AasFilter filter = prepareFilter();
		filter.setAssetType("non-matching-type");

		AggregationOperation operation = filterResolution.applyFilter(filter);
		assertTrue(operation instanceof MatchOperation);

		Document queryDocument = ((MatchOperation) operation).toDocument(Aggregation.DEFAULT_CONTEXT);
		Document matchDocument = (Document) queryDocument.get("$match");

		assertEquals("INSTANCE", matchDocument.get("assetInformation.assetKind"));
		assertEquals("non-matching-type", matchDocument.get("assetInformation.assetType"));
		assertEquals("matching-id-short", matchDocument.get("idShort"));

		Document idCondition = (Document) matchDocument.get("_id");
		assertNotNull(idCondition);
		assertEquals(Collections.singletonList("matching-id"), idCondition.get("$in"));

		@SuppressWarnings("unchecked")
		List<Document> andConditions = (List<Document>) matchDocument.get("$and");
		assertNotNull(andConditions);
		assertEquals(1, andConditions.size());

		Document specificAssetIdsCondition = andConditions.get(0);
		Document elemMatch = (Document) ((Document) specificAssetIdsCondition.get("assetInformation.specificAssetIds")).get("$elemMatch");
		assertNotNull(elemMatch);

		assertEquals("specific-id-name", elemMatch.get("name"));
		assertEquals("specific-id-value", elemMatch.get("value"));
	}

	private AasFilter prepareFilter() {
		AasFilter filter = new AasFilter();
		filter.setIds(Arrays.asList("matching-id"));
		filter.setIdShort("matching-id-short");
		filter.setAssetKind(AssetKind.INSTANCE);
		filter.setAssetType("asset-type");

		SpecificAssetId filterSpecificId = new DefaultSpecificAssetId();
		filterSpecificId.setName("specific-id-name");
		filterSpecificId.setValue("specific-id-value");
		filter.setSpecificAssetIds(Collections.singletonList(filterSpecificId));

		return filter;
	}
}
