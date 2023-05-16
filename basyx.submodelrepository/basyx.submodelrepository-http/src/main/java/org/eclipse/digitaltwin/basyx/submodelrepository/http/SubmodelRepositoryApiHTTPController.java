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

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination.PaginatedSubmodel;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination.PaginatedSubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
public class SubmodelRepositoryApiHTTPController implements SubmodelRepositoryHTTPApi {
	private static final Integer DEFAULT_LIMIT = 25;
	private SubmodelRepository repository;

	@Autowired
	public SubmodelRepositoryApiHTTPController(SubmodelRepository repository) {
		this.repository = repository;
	}

	@Override
	public ResponseEntity<Submodel> postSubmodel(@Parameter(in = ParameterIn.DEFAULT, description = "Submodel object", required = true, schema = @Schema()) @Valid @RequestBody Submodel body) {
		repository.createSubmodel(body);
		return new ResponseEntity<Submodel>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelById(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier) {
		repository.deleteSubmodel(submodelIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath) {
		repository.deleteSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}


	@Override
	public ResponseEntity<List<Submodel>> getAllSubmodels(@Size(min = 1, max = 3072) @Valid Base64UrlEncodedIdentifier semanticId, @Valid String idShort, @Min(1) @Valid Integer limit, @Valid String cursor, @Valid String level,
			@Valid String extent) {
		PaginatedSubmodel paginatedSubmodel;

		if (limit == null) {
			paginatedSubmodel = new PaginatedSubmodel(new ArrayList<>(repository.getAllSubmodels()),
					DEFAULT_LIMIT, cursor);
			return new ResponseEntity<List<Submodel>>(paginatedSubmodel.getPaginatedSubmodels(), HttpStatus.OK);
		}

		paginatedSubmodel = new PaginatedSubmodel(new ArrayList<>(repository.getAllSubmodels()), limit, cursor);

		return new ResponseEntity<List<Submodel>>(new ArrayList<>(repository.getAllSubmodels()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Submodel> getSubmodelById(Base64UrlEncodedIdentifier submodelIdentifier, @Valid String level, @Valid String extent) {
		return new ResponseEntity<Submodel>(repository.getSubmodel(submodelIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putSubmodelById(Base64UrlEncodedIdentifier submodelIdentifier, @Valid Submodel body, @Valid String level) {
		repository.updateSubmodel(submodelIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<List<SubmodelElement>> getAllSubmodelElements(Base64UrlEncodedIdentifier submodelIdentifier, @Min(1) @Valid Integer limit, @Valid String cursor, @Valid String level, @Valid String extent) {
		Collection<SubmodelElement> submodelElements = repository.getSubmodelElements(submodelIdentifier.getIdentifier());
		
		PaginatedSubmodelElement paginatedSubmodelElement;

		if (limit == null) {
			paginatedSubmodelElement = new PaginatedSubmodelElement(new ArrayList<>(submodelElements), DEFAULT_LIMIT,
					cursor);
			return new ResponseEntity<List<SubmodelElement>>(paginatedSubmodelElement.getPaginatedSubmodelElements(),
					HttpStatus.OK);
		}

		paginatedSubmodelElement = new PaginatedSubmodelElement(new ArrayList<>(submodelElements), limit, cursor);

		return new ResponseEntity<List<SubmodelElement>>(new ArrayList<>(submodelElements), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElement> getSubmodelElementByPathSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid String level, @Valid String extent) {
			return handleSubmodelElementValueNormalGetRequest(submodelIdentifier.getIdentifier(), idShortPath);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementByPathSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid SubmodelElement body, @Valid String level, @Valid String extent) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, @Valid SubmodelElement body) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElementValue> getSubmodelElementByPathValueOnlySubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid String level, @Valid String extent) {
		return handleSubmodelElementValueGetRequest(submodelIdentifier.getIdentifier(), idShortPath);
	}

	@Override
	public ResponseEntity<Void> patchSubmodelElementByPathValueOnlySubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid SubmodelElementValue body, @Valid String level) {
		return handleSubmodelElementValueSetRequest(submodelIdentifier, idShortPath, body);
	}

	@Override
	public ResponseEntity<SubmodelValueOnly> getSubmodelByIdValueOnly(Base64UrlEncodedIdentifier submodelIdentifier, @Valid String level, @Valid String extent) {
		return new ResponseEntity<SubmodelValueOnly>(new SubmodelValueOnly(repository.getSubmodelElements(submodelIdentifier.getIdentifier())), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Submodel> getSubmodelByIdMetadata(Base64UrlEncodedIdentifier submodelIdentifier, @Valid String level) {
		Submodel submodel = repository.getSubmodel(submodelIdentifier.getIdentifier());
		submodel.setSubmodelElements(null);
		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	private ResponseEntity<Void> handleSubmodelElementValueSetRequest(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, SubmodelElementValue body) {
		repository.setSubmodelElementValue(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<SubmodelElementValue> handleSubmodelElementValueGetRequest(String submodelIdentifier, String idShortPath) {
		SubmodelElementValue value = repository.getSubmodelElementValue(submodelIdentifier, idShortPath);
		return new ResponseEntity<SubmodelElementValue>(value, HttpStatus.OK);
	}

	private ResponseEntity<SubmodelElement> handleSubmodelElementValueNormalGetRequest(String submodelIdentifier, String idShortPath) {
		SubmodelElement submodelElement = repository.getSubmodelElement(submodelIdentifier, idShortPath);
		return new ResponseEntity<SubmodelElement>(submodelElement, HttpStatus.OK);
	}
}
