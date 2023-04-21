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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory class to create filtered ConceptDescriptions response based on
 * parameters
 * 
 * @author danish
 *
 */
public class FilteredConceptDescriptionResponseFactory {

	private static final Logger logger = LoggerFactory.getLogger(FilteredConceptDescriptionResponseFactory.class);

	private ConceptDescriptionRepository repository;
	private String idShort;
	private String isCaseOf;
	private String dataSpecificationRef;
	private final ObjectMapper objectMapper;

	public FilteredConceptDescriptionResponseFactory(ConceptDescriptionRepository conceptDescriptionRepository,
			Base64UrlEncodedIdentifier idShort, Base64UrlEncodedIdentifier isCaseOf,
			Base64UrlEncodedIdentifier dataSpecificationRef, ObjectMapper objectMapper) {
		super();
		this.repository = conceptDescriptionRepository;
		this.idShort = getDecodedIdentifier(idShort);
		this.isCaseOf = getDecodedIdentifier(isCaseOf);
		this.dataSpecificationRef = getDecodedIdentifier(dataSpecificationRef);
		this.objectMapper = objectMapper;
	}

	public ResponseEntity<List<ConceptDescription>> create() {
		if (!hasPermittedParameters(idShort, isCaseOf, dataSpecificationRef)) {
			logger.error("Only one parameter should be set per request");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (idShortParamSet(idShort))
			return new ResponseEntity<>(new ArrayList<>(repository.getAllConceptDescriptionsByIdShort(idShort)),
					HttpStatus.OK);

		if (isCaseOfParamSet(isCaseOf)) {
			Reference reference = getReference(isCaseOf);

			if (reference == null)
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

			return new ResponseEntity<>(new ArrayList<>(repository.getAllConceptDescriptionsByIsCaseOf(reference)),
					HttpStatus.OK);
		}

		if (dataSpecificationRefSet(dataSpecificationRef)) {
			Reference reference = getReference(dataSpecificationRef);

			if (reference == null)
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

			return new ResponseEntity<>(
					new ArrayList<>(repository.getAllConceptDescriptionsByDataSpecificationReference(reference)),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(new ArrayList<>(repository.getAllConceptDescriptions()), HttpStatus.OK);
	}

	private boolean hasPermittedParameters(String idShort, String isCaseOf, String dataSpecificationRef) {

		return !allParametersSet(idShort, isCaseOf, dataSpecificationRef)
				&& (allParametersEmpty(idShort, isCaseOf, dataSpecificationRef)
				|| (idShortParamSet(idShort) ^ isCaseOfParamSet(isCaseOf)
						^ dataSpecificationRefSet(dataSpecificationRef)));
	}

	private boolean allParametersEmpty(String idShort, String isCaseOf, String dataSpecificationRef) {
		return !idShortParamSet(idShort) && !isCaseOfParamSet(isCaseOf)
				&& !dataSpecificationRefSet(dataSpecificationRef);
	}

	private boolean allParametersSet(String idShort, String isCaseOf, String dataSpecificationRef) {
		return idShortParamSet(idShort) && isCaseOfParamSet(isCaseOf) && dataSpecificationRefSet(dataSpecificationRef);
	}

	private boolean dataSpecificationRefSet(String dataSpecificationRef) {
		return dataSpecificationRef != null && !dataSpecificationRef.isBlank();
	}

	private boolean isCaseOfParamSet(String isCaseOf) {
		return isCaseOf != null && !isCaseOf.isBlank();
	}

	private boolean idShortParamSet(String idShort) {
		return idShort != null && !idShort.isBlank();
	}

	private Reference getReference(String serializedReference) {
		Reference reference = null;

		try {
			reference = objectMapper.readValue(serializedReference, DefaultReference.class);
		} catch (JsonProcessingException e) {
			logger.error("Couldn't serialize response for content type application/json", e);
		}

		return reference;
	}

	private String getDecodedIdentifier(Base64UrlEncodedIdentifier identifier) {
		return identifier == null ? null : identifier.getIdentifier();
	}

}
