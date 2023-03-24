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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
public class SubmodelRepositoryApiHTTPController implements SubmodelRepositoryHTTPApi {
	private SubmodelRepository repository;

	@Autowired
	public SubmodelRepositoryApiHTTPController(SubmodelRepository repository) {
		this.repository = repository;
	}

	@Override
	public ResponseEntity<List<Submodel>> getAllSubmodels(
			@Parameter(in = ParameterIn.QUERY, description = "The value of the semantic id reference (BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "semanticId", required = false) String semanticId,
			@Parameter(in = ParameterIn.QUERY, description = "The Submodel’s idShort", schema = @Schema()) @Valid @RequestParam(value = "idShort", required = false) String idShort) {
		return new ResponseEntity<List<Submodel>>(new ArrayList<>(repository.getAllSubmodels()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getSubmodelSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		if (isNormalContentRequest(content)) {
			return new ResponseEntity<Submodel>(repository.getSubmodel(submodelIdentifier.getIdentifier()),
					HttpStatus.OK);
		} else if (isValueContentRequest(content)) {
			return new ResponseEntity<SubmodelValueOnly>(new SubmodelValueOnly(
					repository.getSubmodelElements(submodelIdentifier.getIdentifier())), HttpStatus.OK);
		}
		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}
	
	@Override
	public ResponseEntity<Void> putSubmodelSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Submodel object", required = true, schema = @Schema()) @Valid @RequestBody Submodel body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		repository.updateSubmodel(submodelIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Submodel> postSubmodel(@Parameter(in = ParameterIn.DEFAULT, description = "Submodel object", required = true, schema = @Schema()) @Valid @RequestBody Submodel body) {
		repository.createSubmodel(body);
		return new ResponseEntity<Submodel>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelById(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier) {
		repository.deleteSubmodel(submodelIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<List<SubmodelElement>> getAllSubmodelElementsSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		Collection<SubmodelElement> submodelElements = repository.getSubmodelElements(submodelIdentifier.getIdentifier());
		return new ResponseEntity<List<SubmodelElement>>(new ArrayList<>(submodelElements), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Object> getSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {

		if (isNormalContentRequest(content)) {
			return handleSubmodelElementValueNormalGetRequest(submodelIdentifier.getIdentifier(), idShortPath);
		} else if (isValueContentRequest(content)) {
			return handleSubmodelElementValueGetRequest(submodelIdentifier.getIdentifier(), idShortPath);
		}

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<Void> putSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElementValue body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		if (isValueContentRequest(content)) {
			return handleSubmodelElementValueSetRequest(submodelIdentifier, idShortPath, body);
		}

		return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" })) @Valid @RequestParam(value = "extent", required = false) String extent) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), body);

		return new ResponseEntity<SubmodelElement>(HttpStatus.OK);
	}

	@Override
	@Operation(summary = "Deletes a submodel element at a specified path within the submodel elements hierarchy", description = "", tags = { "Asset Administration Shell Repository" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel element deleted successfully") })
	@RequestMapping(value = "/submodels/{submodelIdentifier}/submodel/submodel-elements/{idShortPath}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath) {
		repository.deleteSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> handleSubmodelElementValueSetRequest(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, SubmodelElementValue body) {
		repository.setSubmodelElementValue(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Object> handleSubmodelElementValueGetRequest(String submodelIdentifier, String idShortPath) {
		Object value = repository.getSubmodelElementValue(submodelIdentifier, idShortPath);
		return new ResponseEntity<Object>(value, HttpStatus.OK);
	}

	private ResponseEntity<Object> handleSubmodelElementValueNormalGetRequest(String submodelIdentifier, String idShortPath) {
		SubmodelElement submodelElement = repository.getSubmodelElement(submodelIdentifier, idShortPath);
		return new ResponseEntity<Object>(submodelElement, HttpStatus.OK);
	}

	private boolean isValueContentRequest(String content) {
		return content.equals("value");
	}

	private boolean isNormalContentRequest(String content) {
		return content.equals("normal");
	}
}
