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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.common.backend.InstanceScopedThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.common.backend.ThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Thread-safe wrapper for the {@link ConceptDescriptionRepository}
 * 
 * @author mateusmolina
 */
public class ThreadSafeConceptDescriptionRepository implements ConceptDescriptionRepository {

    private final InstanceScopedThreadSafeAccess access = new InstanceScopedThreadSafeAccess();
    private final ConceptDescriptionRepository decoratedRepository;

    public ThreadSafeConceptDescriptionRepository(ConceptDescriptionRepository decoratedRepository) {
        this.decoratedRepository = decoratedRepository;
    }

    @Override
    public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
        return decoratedRepository.getAllConceptDescriptions(pInfo);
    }

    @Override
    public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
        return decoratedRepository.getAllConceptDescriptionsByIdShort(idShort, pInfo);
    }

    @Override
    public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo) {
        return decoratedRepository.getAllConceptDescriptionsByIsCaseOf(isCaseOf, pInfo);
    }

    @Override
    public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo) {
        return decoratedRepository.getAllConceptDescriptionsByDataSpecificationReference(dataSpecificationReference, pInfo);
    }

    @Override
    public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedRepository.getConceptDescription(conceptDescriptionId), conceptDescriptionId);
    }

    @Override
    public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.updateConceptDescription(conceptDescriptionId, conceptDescription), conceptDescriptionId);
    }

    @Override
    public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
        decoratedRepository.createConceptDescription(conceptDescription);
    }

    @Override
    public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
        access.write(() -> decoratedRepository.deleteConceptDescription(conceptDescriptionId), conceptDescriptionId);
    }

    @Override
    public String getName() {
        return decoratedRepository.getName();
    }

}
