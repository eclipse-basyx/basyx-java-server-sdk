/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.aasregistry.paths.AasRegistryPaths;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorSearchResponse;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Sorting;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortingPath;
import org.junit.Test;
import org.mockito.Mockito;

public abstract class ExtensionsTest extends BaseInterfaceTest {

	@Test
	public void whenSearchWithSorting_thenSorted() {
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting(List.of(SortingPath.ID))));
		String[] ids = response.getHits().stream().map(AssetAdministrationShellDescriptor::getId).toArray(String[]::new);
		assertThat(ids[0]).isEqualTo(IDENTIFICATION_1);
		assertThat(ids[1]).isEqualTo(IDENTIFICATION_2);

		response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting(List.of(SortingPath.ID)).direction(SortDirection.DESC)));
		ids = response.getHits().stream().map(AssetAdministrationShellDescriptor::getId).toArray(String[]::new);
		assertThat(ids[0]).isEqualTo(IDENTIFICATION_2);
		assertThat(ids[1]).isEqualTo(IDENTIFICATION_1);
	}

	@Test
	public void whenSearchOutsideSubmodel_thenGetUnshrinkedDescriptor() {
		AssetAdministrationShellDescriptor expected = storage.getAasDescriptor(IDENTIFICATION_1);
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.id(), IDENTIFICATION_1)));
		assertThat(response.getTotal()).isEqualTo(1);
		assertThat(response.getHits().iterator().next()).isEqualTo(expected);
	}

	@Test
	public void whenSearchWithPagination_thenReturnStepwise() throws IOException {
		List<AssetAdministrationShellDescriptor> expectedFirstPage = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class, "0");
		List<AssetAdministrationShellDescriptor> expectedSecondPage = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class, "1");
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().description().text(), ".*[R|r]obot.*").queryType(QueryTypeEnum.REGEX))
				.sortBy(new Sorting(List.of(SortingPath.ID)).direction(SortDirection.DESC)).page(new Page(0, 2));
		ShellDescriptorSearchResponse response1 = storage.searchAasDescriptors(request);
		request.setPage(new Page(1, 2));
		ShellDescriptorSearchResponse response2 = storage.searchAasDescriptors(request);
		assertThat(response1.getTotal()).isEqualTo(3);
		assertThat(response2.getTotal()).isEqualTo(3);
		assertThat(response1.getHits().size()).isEqualTo(2);
		assertThat(response2.getHits().size()).isEqualTo(1);
		assertThat(response1.getHits()).isEqualTo(expectedFirstPage);
		assertThat(response2.getHits()).isEqualTo(expectedSecondPage);
	}

	@Test
	public void whenSearchWithSortingAndNullValueAdSearchPath_thenNotSorted() {
		Collection<AssetAdministrationShellDescriptor> initial = getAllAasDescriptors();
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting(List.of(SortingPath.ADMINISTRATION_VERSION))));
		assertThat(response.getHits()).isEqualTo(initial);
	}

	@Test
	public void whenSearchWithTwoSortingPaths_thenSorted() throws IOException {
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting(List.of(SortingPath.ADMINISTRATION_VERSION, SortingPath.ADMINISTRATION_REVISION))));
		assertThat(response.getHits()).isEqualTo(expected);
	}

	@Test
	public void whenSearchWithSortingButNoSortPath_thenNotSorted() {
		Collection<AssetAdministrationShellDescriptor> initial = getAllAasDescriptors();
		ShellDescriptorSearchResponse response = storage.searchAasDescriptors(new ShellDescriptorSearchRequest().sortBy(new Sorting(List.of())));
		assertThat(response.getHits()).isEqualTo(initial);
	}

	@Test
	public void whenDeleteAllShellDescritors_thenEventsAreSendAndDescriptorsRemoved() {
		List<AssetAdministrationShellDescriptor> oldState = getAllAasDescriptors();
		assertThat(oldState).isNotEmpty();
		Set<String> aasIdsOfRemovedDescriptors = storage.clear();
		// listener is invoked for each removal
		Mockito.verify(getEventSink(), Mockito.times(aasIdsOfRemovedDescriptors.size())).consumeEvent(Mockito.any(RegistryEvent.class));
		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).isEmpty();
	}

	@Test
	public void whenMatchSearchBySubModel_thenReturnDescriptorList() throws IOException {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().id(), IDENTIFICATION_2_1).queryType(QueryTypeEnum.MATCH));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(request);
		AssetAdministrationShellDescriptor descriptor = testResourcesLoader.load(AssetAdministrationShellDescriptor.class);

		assertThat(result.getTotal()).isEqualTo(1);
		assertThat(result.getHits().get(0)).isEqualTo(descriptor);
	}

	@Test
	public void whenMatchSearchBySubModelAndNotFound_thenReturnEmptyList() {
		ShellDescriptorSearchRequest query = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().id(), UNKNOWN).queryType(QueryTypeEnum.MATCH));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(query);
		assertThat(result.getTotal()).isZero();
		assertThat(result.getHits().size()).isZero();
	}

	@Test
	public void whenRegexSearchBySubModel_thenReturnDescriptorList() throws IOException {
		ShellDescriptorSearchRequest request = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().idShort(), ".*_24").queryType(QueryTypeEnum.REGEX));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(request);
		AssetAdministrationShellDescriptor descriptor = testResourcesLoader.load(AssetAdministrationShellDescriptor.class);
		assertThat(result.getTotal()).isEqualTo(1);
		assertThat(result.getHits().get(0)).isEqualTo(descriptor);
	}

	@Test
	public void whenRegexSearchBySubModelAndNotFound_thenReturnEmptyList() {
		ShellDescriptorSearchRequest query = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(AasRegistryPaths.submodelDescriptors().idShort(), ".*_333_.*").queryType(QueryTypeEnum.REGEX));
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(query);
		assertThat(result.getTotal()).isZero();
		assertThat(result.getHits().size()).isZero();
	}

	@Test
	public void whenSearchForSubmodelEndpointsProtocolInformationEndpointProtocolVersion_whenSubmodelsFound() throws IOException {
		whenSearchForSubmodelEndpointsProtocolInformationEndpointProtocolVersion_whenSubmodelsFound(QueryTypeEnum.MATCH, "2_2_2");
	}

	@Test
	public void whenRegexSearchForSubmodelEndpointsProtocolInformationEndpointProtocolVersion_whenSubmodelsFound() throws IOException {
		whenSearchForSubmodelEndpointsProtocolInformationEndpointProtocolVersion_whenSubmodelsFound(QueryTypeEnum.REGEX, "^[4|3]_0_1$");
	}

	private void whenSearchForSubmodelEndpointsProtocolInformationEndpointProtocolVersion_whenSubmodelsFound(QueryTypeEnum type, String value) throws IOException {
		String path = AasRegistryPaths.submodelDescriptors().endpoints().protocolInformation().endpointProtocolVersion();
		ShellDescriptorSearchRequest query = new ShellDescriptorSearchRequest().query(new ShellDescriptorQuery(path, value).queryType(type));
		List<AssetAdministrationShellDescriptor> expected = this.testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		ShellDescriptorSearchResponse result = storage.searchAasDescriptors(query);
		assertThat(result.getHits()).asList().isEqualTo(expected);
		assertThat(result.getTotal()).isEqualTo(2);
	}

}