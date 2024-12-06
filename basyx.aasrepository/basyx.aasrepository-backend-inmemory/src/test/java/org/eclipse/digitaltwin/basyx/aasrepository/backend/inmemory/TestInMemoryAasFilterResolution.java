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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilter;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link AasInMemoryFilterResolution}
 * 
 * @author danish
 */
public class TestInMemoryAasFilterResolution {

	private AasInMemoryFilterResolution filterResolution;
	private AssetAdministrationShell aas;
	private AssetInformation assetInformation;

	@Before
	public void setUp() {
		filterResolution = new AasInMemoryFilterResolution();

		assetInformation = new DefaultAssetInformation();
		aas = new DefaultAssetAdministrationShell();
		aas.setAssetInformation(assetInformation);
	}

	@Test
	public void nullFilter() {
		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(null);
		assertTrue(predicate.test(aas));
	}

	@Test(expected = RuntimeException.class)
	public void invalidFilterType() {
		Filter invalidFilter = new Filter() {

			@Override
			public List<String> getIds() {
				return null;
			}
		};

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(invalidFilter);
		predicate.test(aas);
	}

	@Test
	public void filterWithMatchingId() {
		AasFilter filter = new AasFilter();
		filter.setIds(Arrays.asList("matching-id"));
		aas.setId("matching-id");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void filterWithNonMatchingId() {
		AasFilter filter = new AasFilter();
		filter.setIds(Arrays.asList("non-matching-id"));
		aas.setId("some-other-id");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertFalse(predicate.test(aas));
	}

	@Test
	public void filterWithMatchingIdShort() {
		AasFilter filter = new AasFilter();
		filter.setIdShort("matching-id-short");
		aas.setIdShort("matching-id-short");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void filterWithNonMatchingIdShort() {
		AasFilter filter = new AasFilter();
		filter.setIdShort("filter-id-short");
		aas.setIdShort("other-id-short");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertFalse(predicate.test(aas));
	}

	@Test
	public void filterWithMatchingAssetKindAndType() {
		AasFilter filter = new AasFilter();
		filter.setAssetKind(AssetKind.INSTANCE);
		filter.setAssetType("asset-type");

		assetInformation.setAssetKind(AssetKind.INSTANCE);
		assetInformation.setAssetType("asset-type");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void filterWithNonMatchingAssetKindAndType() {
		AasFilter filter = new AasFilter();
		filter.setAssetKind(AssetKind.INSTANCE);
		filter.setAssetType("filter-type");

		assetInformation.setAssetKind(AssetKind.TYPE);
		assetInformation.setAssetType("other-type");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertFalse(predicate.test(aas));
	}

	@Test
	public void filterWithMatchingSpecificAssetIds() {
		SpecificAssetId filterSpecificId = new DefaultSpecificAssetId();
		filterSpecificId.setName("filter-name");
		filterSpecificId.setValue("filter-value");

		SpecificAssetId targetSpecificId = new DefaultSpecificAssetId();
		targetSpecificId.setName("filter-name");
		targetSpecificId.setValue("filter-value");

		AasFilter filter = new AasFilter();
		filter.setSpecificAssetIds(Collections.singletonList(filterSpecificId));
		assetInformation.setSpecificAssetIds(Collections.singletonList(targetSpecificId));

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void filterWithNonMatchingSpecificAssetIds() {
		SpecificAssetId filterSpecificId = new DefaultSpecificAssetId();
		filterSpecificId.setName("filter-name");
		filterSpecificId.setValue("filter-value");

		SpecificAssetId targetSpecificId = new DefaultSpecificAssetId();
		targetSpecificId.setName("different-name");
		targetSpecificId.setValue("different-value");

		AasFilter filter = new AasFilter();
		filter.setSpecificAssetIds(Collections.singletonList(filterSpecificId));
		assetInformation.setSpecificAssetIds(Collections.singletonList(targetSpecificId));

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertFalse(predicate.test(aas));
	}

	@Test
	public void filterWithNullSpecificAssetIds() {
		AasFilter filter = new AasFilter();
		filter.setSpecificAssetIds(null);

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void filterWithEmptySpecificAssetIds() {
		AasFilter filter = new AasFilter();
		filter.setSpecificAssetIds(Collections.emptyList());

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void allValuesMatch() {
		AasFilter filter = new AasFilter();
		filter.setIds(Arrays.asList("matching-id1", "matching-id2"));
		filter.setIdShort("matching-id-short");
		filter.setAssetKind(AssetKind.INSTANCE);
		filter.setAssetType("asset-type");

		SpecificAssetId filterSpecificId = new DefaultSpecificAssetId();
		filterSpecificId.setName("specific-id-name");
		filterSpecificId.setValue("specific-id-value");
		filter.setSpecificAssetIds(Collections.singletonList(filterSpecificId));

		aas.setId("matching-id2");
		aas.setIdShort("matching-id-short");

		assetInformation.setAssetKind(AssetKind.INSTANCE);
		assetInformation.setAssetType("asset-type");

		SpecificAssetId targetSpecificId = new DefaultSpecificAssetId();
		targetSpecificId.setName("specific-id-name");
		targetSpecificId.setValue("specific-id-value");
		assetInformation.setSpecificAssetIds(Collections.singletonList(targetSpecificId));

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertTrue(predicate.test(aas));
	}

	@Test
	public void oneValueChanged() {
		AasFilter filter = new AasFilter();
		filter.setIds(Arrays.asList("matching-id"));
		filter.setIdShort("matching-id-short");
		filter.setAssetKind(AssetKind.INSTANCE);
		filter.setAssetType("asset-type");

		SpecificAssetId filterSpecificId = new DefaultSpecificAssetId();
		filterSpecificId.setName("specific-id-name");
		filterSpecificId.setValue("specific-id-value");
		filter.setSpecificAssetIds(Collections.singletonList(filterSpecificId));

		aas.setId("matching-id");
		aas.setIdShort("matching-id-short");

		assetInformation.setAssetKind(AssetKind.INSTANCE);
		assetInformation.setAssetType("asset-type");

		SpecificAssetId targetSpecificId = new DefaultSpecificAssetId();
		targetSpecificId.setName("specific-id-name");
		targetSpecificId.setValue("specific-id-value");
		assetInformation.setSpecificAssetIds(Collections.singletonList(targetSpecificId));

		filter.setIdShort("non-matching-id-short");

		Predicate<AssetAdministrationShell> predicate = filterResolution.applyFilter(filter);
		assertFalse(predicate.test(aas));
	}

}
