/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.filter;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Filters ConceptDescriptions based on parameters
 * 
 * @author danish
 *
 */
public class ConceptDescriptionRepositoryFilter {

	private static final int MAX_NUM_OF_EXPECTED_PARAMETERS = 1;
	private ConceptDescriptionRepository repository;

	public ConceptDescriptionRepositoryFilter(ConceptDescriptionRepository conceptDescriptionRepository) {
		this.repository = conceptDescriptionRepository;
	}

	/**
	 * Filters ConceptDescriptions from repository
	 * 
	 * 
	 * @return a filtered collection of ConceptDescriptions
	 */
	public CursorResult<List<ConceptDescription>> filter(String idShort, Reference isCaseOf, Reference dataSpecificationRef, PaginationInfo pInfo) {
		if (!hasPermittedNumberOfParameters(idShort, isCaseOf, dataSpecificationRef)) {
			throw new IllegalArgumentException("ConceptDescriptionFilter was called with the wrong number of arguments");
		}

		if (idShort != null)
			return repository.getAllConceptDescriptionsByIdShort(idShort, pInfo);

		if (isCaseOf != null)
			return repository.getAllConceptDescriptionsByIsCaseOf(isCaseOf, pInfo);

		if (dataSpecificationRef != null)
			return repository.getAllConceptDescriptionsByDataSpecificationReference(dataSpecificationRef, pInfo);

		return repository.getAllConceptDescriptions(pInfo);
	}

	private boolean hasPermittedNumberOfParameters(String idShort, Reference isCaseOf, Reference dataSpecificationRef) {
		int setParameters = 0;

		if (isIdShortParamSet(idShort))
			setParameters++;

		if (isIsCaseOfParamSet(isCaseOf))
			setParameters++;

		if (isDataSpecificationRefSet(dataSpecificationRef))
			setParameters++;

		return isNumberOfParametersValid(setParameters);
	}

	private boolean isNumberOfParametersValid(int setParameters) {
		return setParameters <= MAX_NUM_OF_EXPECTED_PARAMETERS;
	}

	private boolean isDataSpecificationRefSet(Reference dataSpecificationRef) {
		return dataSpecificationRef != null;
	}

	private boolean isIsCaseOfParamSet(Reference isCaseOf) {
		return isCaseOf != null;
	}

	private boolean isIdShortParamSet(String idShort) {
		return idShort != null && !idShort.isBlank();
	}

}
