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
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.AasDescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.DescriptorNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.errors.SubmodelNotFoundException;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorCopies;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.junit.Test;

public abstract class AasRegistryStorageTest extends ExtensionsTest {

	@Test
	public void whenRegisterSubmodelDescriptorNullAasId_thenNullPointer() {
		assertNullPointerThrown(() -> storage.insertSubmodel(null, null));
	}

	@Test
	public void whenRegisterSubmodelDescriptorNullModel_thenNullPointer() {
		assertNullPointerThrown(() -> storage.insertSubmodel(IDENTIFICATION_1, null));
	}

	@Test
	public void whenRegisterSubmodelDescriptorUnknownId_thenThrowNotFound() {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		SubmodelDescriptor ignored = new SubmodelDescriptor("ignored", List.of());
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.insertSubmodel(UNKNOWN, ignored));
		List<AssetAdministrationShellDescriptor> currentState = getAllAasDescriptors();
		assertThat(currentState).isEqualTo(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasAlreadyPresent_thenElementIsOverridden() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = new SubmodelDescriptor(IDENTIFICATION_2_2, List.of()).addDescriptionItem(new LangStringTextType("de-DE", "Overridden"));
		storage.replaceSubmodel(IDENTIFICATION_2, toAdd.getId(), toAdd);
		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasNotAlreadyPresent_thenElementIsAdded() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = new SubmodelDescriptor(IDENTIFICATION_2_3, List.of());

		// RegistryTestObjects.addDefaultEndpoint(toAdd);
		storage.insertSubmodel(IDENTIFICATION_2, toAdd);
		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorNullAdminShell_thenNullPointer() {
		assertNullPointerThrown(() -> storage.removeSubmodel(null, IDENTIFICATION_2_1));
	}

	@Test
	public void whenUnregisterSubmodelDescriptorNullSubmodelId_thenNullPointer() {
		assertNullPointerThrown(() -> storage.removeSubmodel(IDENTIFICATION_2, null));
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndWasPresent_thenElementIsRemoved() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_2);

		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();

		storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);

		assertThat(storage.getAasDescriptor(IDENTIFICATION_2).getSubmodelDescriptors()).isNullOrEmpty();

		assertThrows(SubmodelNotFoundException.class, () -> storage.removeSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1));
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndShellWasNotPresent_thenThrowNotFound() {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();

		assertThrows(AasDescriptorNotFoundException.class, () -> storage.removeSubmodel(UNKNOWN, UNKNOWN_1));

		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorAndSubmodelWasNotPresent_thenReturnFalse() {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		assertThrows(SubmodelNotFoundException.class, () -> storage.removeSubmodel(IDENTIFICATION_2, _2_UNKNOWN));
		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptors_thenAll() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByType_thenOnlyType() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.TYPE, null);
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByInstance_thenOnlyInstance() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.INSTANCE, null);
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}
	
	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByInstanceAndType_thenOnlyMatching() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.INSTANCE, "A");
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}
	
	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByTypeAndType_thenOnlyMatching() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.TYPE, "A");
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}
	
	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredNoKindButType_thenOnlyMatching() throws IOException, SerializationException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(null, "A");
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		
		System.out.println(new JsonSerializer().write(found));
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}
	
	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredNotApplicableAndType_thenOnlyMatching() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.NOTAPPLICABLE, "A");
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByNotApplicable_thenOnlyNotApplicable() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.NOTAPPLICABLE, null);
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsFilteredByTypeName_thenOnlyMatching() throws IOException {
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptorsFiltered(AssetKind.TYPE, "A");
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsOverTwoPages_thenReturnPageStepByStep() throws IOException {
		CursorResult<List<AssetAdministrationShellDescriptor>> firstResult = getAllAasDescriptorsWithPagination(2, null);
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);

		CursorResult<List<AssetAdministrationShellDescriptor>> secondResult = getAllAasDescriptorsWithPagination(2, firstResult.getCursor());

		assertThat(firstResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(0, 2));
		assertThat(secondResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(2, 4));

		if (secondResult.getCursor() != null) { // implementation specific
			CursorResult<List<AssetAdministrationShellDescriptor>> thirdResult = getAllAasDescriptorsWithPagination(2, secondResult.getCursor());
			assertThat(thirdResult.getResult()).isEmpty();
		}

		verifyNoEventSent();
	}

	@Test
	public void whenGetAllAssetAdministrationShellDescriptorsAndEmptyRepo_thenEmptyList() {
		clearBaseStorage();
		Collection<AssetAdministrationShellDescriptor> found = getAllAasDescriptors();
		assertThat(found).isEmpty();
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsAndNotSet_thenEmptyList() {
		List<SubmodelDescriptor> submodels = getAllSubmodels(IDENTIFICATION_1);
		assertThat(submodels).isEmpty();
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsAndNotPresent_throwNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> getAllSubmodels(UNKNOWN));
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelDescriptors_thenGot2Elements() throws IOException {
		List<SubmodelDescriptor> found = getAllSubmodels(IDENTIFICATION_2);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getAasDescriptor(null));
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndUnknown_thenThrowNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.getAasDescriptor(UNKNOWN));
		verifyNoEventSent();
	}

	@Test
	public void whenGetAssetAdminstrationShellDescritorByIdAndAvailable_thenGotResult() throws IOException {
		AssetAdministrationShellDescriptor result = storage.getAasDescriptor(IDENTIFICATION_1);
		AssetAdministrationShellDescriptor expected = testResourcesLoader.load(AssetAdministrationShellDescriptor.class);
		assertThat(result).isEqualTo(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenRegisterAssetAdministrationShellDescriptorNullArg_thenNullPointer() {
		assertNullPointerThrown(() -> storage.replaceAasDescriptor(null, null));
	}

	@Test
	public void whenRegisterAssetAdministrationShellDescriptor_thenStored() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		AssetAdministrationShellDescriptor testResource = new AssetAdministrationShellDescriptor(IDENTIFICATION_NEW);
		SubmodelDescriptor subModel = new SubmodelDescriptor(IDENTIFICATION_NEW_1, List.of());
		testResource.setSubmodelDescriptors(Collections.singletonList(subModel));
		storage.insertAasDescriptor(testResource);
		List<AssetAdministrationShellDescriptor> newState = getAllAasDescriptors();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorByIdAndNullId_thenThrowNotFoundAndNoChanges() {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		assertThrows(NullPointerException.class, () -> storage.removeAasDescriptor(null));
		List<AssetAdministrationShellDescriptor> currentState = getAllAasDescriptors();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorById_thenReturnTrueAndEntryRemoved() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		storage.removeAasDescriptor(IDENTIFICATION_2);
		List<AssetAdministrationShellDescriptor> currentState = getAllAasDescriptors();
		assertThat(currentState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterAssetAdministrationShellDescriptorByIdAndIdUnknon_thenReturnFalsAndNoChanges() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.removeAasDescriptor(UNKNOWN));

		List<AssetAdministrationShellDescriptor> currentState = getAllAasDescriptors();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenRegistrationUpdateForNewId_AvailableUnderNewIdAndTwoEventsFired() throws IOException {
		List<AssetAdministrationShellDescriptor> initialState = getAllAasDescriptors();

		AssetAdministrationShellDescriptor descr = initialState.stream().filter(a -> a.getId().equals(IDENTIFICATION_2)).findAny().get();
		descr = DescriptorCopies.deepClone(descr);
		descr.setId(IDENTIFICATION_3);
		storage.replaceAasDescriptor(IDENTIFICATION_2, descr);

		List<AssetAdministrationShellDescriptor> currentState = getAllAasDescriptors();
		List<AssetAdministrationShellDescriptor> expected = testResourcesLoader.loadList(AssetAdministrationShellDescriptor.class);
		assertThat(currentState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);

		verifyEventsSent();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndBothArgsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getSubmodel(null, null));
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getSubmodel(IDENTIFICATION_2, null));
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndDescriptorIdIsNotAvailable_thenThrowNotFound() {
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.getSubmodel(UNKNOWN, UNKNOWN_1));
		verifyNoEventSent();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsNotAvailable_thenThrowNotFound() {
		assertThrows(SubmodelNotFoundException.class, () -> storage.getSubmodel(IDENTIFICATION_2, _2_UNKNOWN));
		verifyNoEventSent();
	}

	@Test
	public void whenGetSubmodelDescriptorByIdAndSubmodelIdIsAvailable_thenGotResult() throws IOException {
		SubmodelDescriptor result = storage.getSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1);
		SubmodelDescriptor expected = testResourcesLoader.load(SubmodelDescriptor.class);
		assertThat(result).isEqualTo(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelsOverTwoPages_thenReturnPageStepByStep() throws IOException {
		CursorResult<List<SubmodelDescriptor>> firstResult = getAllSubmodelsWithPagination(IDENTIFICATION_2, 2, null);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		CursorResult<List<SubmodelDescriptor>> secondResult = getAllSubmodelsWithPagination(IDENTIFICATION_2, 2, firstResult.getCursor());
		assertThat(firstResult.getCursor()).isNotNull();
		assertThat(firstResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(0, 2));
		assertThat(secondResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(2, 4));

		if (secondResult.getCursor() != null) {
			CursorResult<List<SubmodelDescriptor>> thirdResult = getAllSubmodelsWithPagination(IDENTIFICATION_2, 2, secondResult.getCursor());
			assertThat(thirdResult.getResult()).isEmpty();
		}
		verifyNoEventSent();
	}

	@Test
	public void whenTryToReplaceUnknownDescriptor_thenThrowException() {
		AssetAdministrationShellDescriptor descr = new AssetAdministrationShellDescriptor(IDENTIFICATION_1);
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.replaceAasDescriptor(UNKNOWN, descr));
	}

	@Test
	public void whenInsertSubmodelAndAlreadyExists_thenThrowException() {
		SubmodelDescriptor descr = new SubmodelDescriptor(IDENTIFICATION_2_1, List.of());
		assertThrows(SubmodelAlreadyExistsException.class, () -> storage.insertSubmodel(IDENTIFICATION_2, descr));
	}

	@Test
	public void whenInsertSubmodelAndAasDescriptorNotFound_thenThrowException() {
		SubmodelDescriptor descr = new SubmodelDescriptor(IDENTIFICATION_2_1, List.of());
		assertThrows(AasDescriptorNotFoundException.class, () -> storage.insertSubmodel(UNKNOWN, descr));
	}

	@Test
	public void whenReplaceSubmodelAndNotAvailable_thenThrowException() {
		SubmodelDescriptor descr = new SubmodelDescriptor(UNKNOWN, List.of());
		assertThrows(SubmodelNotFoundException.class, () -> storage.replaceSubmodel(IDENTIFICATION_1, UNKNOWN, descr));
	}

	@Test
	public void whenReplaceSubmodelAndDescriptorNotAvailable_thenThrowException() {
		SubmodelDescriptor descr = new SubmodelDescriptor(UNKNOWN, List.of());
		assertThrows(DescriptorNotFoundException.class, () -> storage.replaceSubmodel(UNKNOWN, UNKNOWN, descr));
	}

	@Test
	public void whenInsertAasDescriptorAndDescriptorAlreadyAvailable_thenThrowException() {
		assertThrows(AasDescriptorAlreadyExistsException.class, () -> storage.insertAasDescriptor(new AssetAdministrationShellDescriptor(IDENTIFICATION_1)));
	}

	@Test
	public void whenReplaceSubmodelAndWithDifferentId_thenEventIsSent() throws IOException {
		SubmodelDescriptor descr = new SubmodelDescriptor(IDENTIFICATION_NEW, List.of());
		storage.replaceSubmodel(IDENTIFICATION_2, IDENTIFICATION_2_1, descr);
		verifyEventsSent();
	}

	@Test
	public void whenReplaceSubmodelButNotFound_thenThrowNotFound() {
		SubmodelDescriptor sm = new SubmodelDescriptor(IDENTIFICATION_NEW, List.of());
		assertThrows(SubmodelNotFoundException.class, () -> storage.replaceSubmodel(IDENTIFICATION_1, IDENTIFICATION_NEW, sm));
		verifyNoEventSent();
	}

}