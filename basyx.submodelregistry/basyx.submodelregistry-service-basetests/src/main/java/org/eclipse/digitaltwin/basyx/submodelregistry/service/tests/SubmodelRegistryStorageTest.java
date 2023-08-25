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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelAlreadyExistsException;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.errors.SubmodelNotFoundException;
import org.junit.Test;

public abstract class SubmodelRegistryStorageTest extends ExtensionsTest {

	@Test
	public void whenRegisterSubmodelDescriptorNullModel_thenNullPointer() {
		assertNullPointerThrown(() -> storage.insertSubmodelDescriptor(null));
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasAlreadyPresent_thenElementIsOverridden() throws IOException {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = new SubmodelDescriptor(SM_ID_2, List.of()).addDescriptionItem(new LangStringTextType("de-DE", "Overridden"));
		storage.replaceSubmodelDescriptor(SM_ID_2, toAdd);
		List<SubmodelDescriptor> newState = getAllSubmodels();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}
	
	public void whenReplaceSubmodelDescriptorAdnNotPresent_thenExceptionIsTrown() {
		assertThrows(SubmodelNotFoundException.class, () -> storage.replaceSubmodelDescriptor(UNKNOWN_SM_ID, new SubmodelDescriptor(UNKNOWN_SM_ID, List.of())));
		
	}

	@Test
	public void whenRegisterSubmodelDescriptorAndWasNotAlreadyPresent_thenElementIsAdded() throws IOException {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor toAdd = new SubmodelDescriptor(SM_ID_3, List.of());
		storage.insertSubmodelDescriptor(toAdd);
		List<SubmodelDescriptor> newState = getAllSubmodels();
		
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorNullSubmodelId_thenNullPointer() {
		assertNullPointerThrown(() -> storage.removeSubmodelDescriptor(null));
	}

	@Test
	public void whenGetAllSubmodelDescriptors_thenAll() throws IOException {
		Collection<SubmodelDescriptor> found = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(found).containsExactlyInAnyOrderElementsOf(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsOverTwoPages_thenReturnPageStepByStep() throws IOException {
		CursorResult<List<SubmodelDescriptor>> firstResult = getAllSubmodelsWithPagination(2, null);
		List<SubmodelDescriptor> expected = testResourcesLoader.loadRepositoryDefinition(SubmodelDescriptor.class);

		CursorResult<List<SubmodelDescriptor>> secondResult = getAllSubmodelsWithPagination(2, firstResult.getCursor());

		assertThat(firstResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(0, 2));
		assertThat(secondResult.getResult()).containsExactlyInAnyOrderElementsOf(expected.subList(2, 4));

		if (secondResult.getCursor() != null) { // implementation specific
			CursorResult<List<SubmodelDescriptor>> thirdResult = getAllSubmodelsWithPagination(2,
					secondResult.getCursor());
			assertThat(thirdResult.getResult()).isEmpty();
		}
		verifyNoEventSent();
	}

	@Test
	public void whenGetAllSubmodelDescriptorsAndEmptyRepo_thenEmptyList() {
		Collection<SubmodelDescriptor> found = getAllSubmodels();
		assertThat(found).isEmpty();
		verifyNoEventSent();
	}

	@Test
	public void whenGetSubmodelDescritorByIdAndIdIsNull_thenNullPointer() {
		assertNullPointerThrown(() -> storage.getSubmodelDescriptor(null));
	}

	@Test
	public void whenGetSubmodelByIdAndUnknown_thenThrowNotFound() {
		assertThrows(SubmodelNotFoundException.class, () -> storage.getSubmodelDescriptor(UNKNOWN_SM_ID));
		verifyNoEventSent();
	}

	@Test
	public void whenGetSubmodelDescritorByIdAndAvailable_thenGotResult() throws IOException {
		SubmodelDescriptor result = storage.getSubmodelDescriptor(SM_ID_2);
		SubmodelDescriptor expected = testResourcesLoader.load(SubmodelDescriptor.class);
		assertThat(result).isEqualTo(expected);
		verifyNoEventSent();
	}

	@Test
	public void whenRegisterAssetAdministrationShellDescriptorNullArg_thenNullPointer() {
		assertNullPointerThrown(() -> storage.replaceSubmodelDescriptor(null, null));
	}

	@Test
	public void whenRegisterSubmodelDescriptor_thenStored() throws IOException {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		assertThat(initialState).isNotEqualTo(expected);
		SubmodelDescriptor testResource = new SubmodelDescriptor("new", List.of());
		storage.insertSubmodelDescriptor(testResource);
		List<SubmodelDescriptor> newState = getAllSubmodels();
		assertThat(newState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorByIdAndNullId_thenThrowNotFoundAndNoChanges() {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		assertThrows(NullPointerException.class, () -> storage.removeSubmodelDescriptor(null));
		List<SubmodelDescriptor> currentState = getAllSubmodels();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorById_thenReturnTrueAndEntryRemoved() throws IOException {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		storage.removeSubmodelDescriptor(SM_ID_2);
		List<SubmodelDescriptor> currentState = getAllSubmodels();
		assertThat(currentState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenUnregisterSubmodelDescriptorByIdAndIdUnknon_thenReturnFalsAndNoChanges() {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		assertThrows(SubmodelNotFoundException.class, () -> storage.removeSubmodelDescriptor(UNKNOWN_SM_ID));
		List<SubmodelDescriptor> currentState = getAllSubmodels();
		assertThat(currentState).asList().containsExactlyInAnyOrderElementsOf(initialState);
		verifyNoEventSent();
	}

	@Test
	public void whenRegistrationUpdateForNewId_AvailableUnderNewIdAndTwoEventsFired() throws IOException {
		List<SubmodelDescriptor> initialState = getAllSubmodels();
		SubmodelDescriptor descr = initialState.stream().filter(a -> a.getId().equals(SM_ID_2)).findAny().orElseThrow();
		
		SubmodelDescriptor copy = new SubmodelDescriptor(SM_ID_3, new LinkedList<>(descr.getEndpoints()));
		copy.idShort(descr.getIdShort());
		
		storage.replaceSubmodelDescriptor(SM_ID_2, copy);
		List<SubmodelDescriptor> currentState = getAllSubmodels();
		List<SubmodelDescriptor> expected = testResourcesLoader.loadList(SubmodelDescriptor.class);
		
		assertThat(currentState).asList().isNotEqualTo(initialState).containsExactlyInAnyOrderElementsOf(expected);
		verifyEventsSent();
	}

	@Test
	public void whenTryToReplaceUnknownSubmodel_thenThrowException() {
		SubmodelDescriptor descr = new SubmodelDescriptor(SM_ID_5, List.of());
		assertThrows(SubmodelNotFoundException.class, () -> storage.replaceSubmodelDescriptor(UNKNOWN_SM_ID, descr));
	}

	@Test
	public void whenInsertSubmodelAndSubmodelAlreadyAvailable_thenThrowException() {
		assertThrows(SubmodelAlreadyExistsException.class, () -> storage.insertSubmodelDescriptor(new SubmodelDescriptor("sm1", List.of())));
	}
}