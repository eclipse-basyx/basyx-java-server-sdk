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

package org.eclipse.digitaltwin.basyx.submodelservice.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-07-20T09:38:58.667119080Z[GMT]")
@RestController
public class SubmodelServiceHTTPApiController implements SubmodelServiceHTTPApi {

	private static final Logger log = LoggerFactory.getLogger(SubmodelServiceHTTPApiController.class);

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	private SubmodelService service;

	private final SubmodelServiceFactory serviceFactory;

	@org.springframework.beans.factory.annotation.Autowired
	public SubmodelServiceHTTPApiController(ObjectMapper objectMapper, HttpServletRequest request, SubmodelService service, SubmodelServiceFactory serviceFactory) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.service = service;
		this.serviceFactory = serviceFactory;
	}

	public ResponseEntity<Void> deleteSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath) {
		service.deleteSubmodelElement(idShortPath.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<Collection<SubmodelElement>> getAllSubmodelElements(
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		Collection<SubmodelElement> result = service.getSubmodelElements();
		return new ResponseEntity<Collection<SubmodelElement>>(result, HttpStatus.OK);
	}

	public ResponseEntity<List<Reference>> getAllSubmodelElementsReference(
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {
		Collection<SubmodelElement> submodelElements = service.getSubmodelElements();
		ArrayList<Reference> result = new ArrayList<>();
		submodelElements.stream()
				.forEach(sme -> result.add(sme.getSemanticID()));

		return new ResponseEntity<List<Reference>>(result, HttpStatus.OK);
	}

	public ResponseEntity<Submodel> getSubmodel(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		Submodel submodel = service.getSubmodel();

		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelElement> getSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelElement submodelElement = service.getSubmodelElement(idShortPath.getIdentifier());

		return new ResponseEntity<SubmodelElement>(submodelElement, HttpStatus.OK);
	}

	public ResponseEntity<Reference> getSubmodelElementByPathReference(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {

		SubmodelElement submodelElement = service.getSubmodelElement(idShortPath.getIdentifier());

		return new ResponseEntity<Reference>(submodelElement.getSemanticID(), HttpStatus.OK);
	}

	public ResponseEntity<SubmodelElementValue> getSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelElementValue submodelElementValue = service.getSubmodelElementValue(idShortPath.getIdentifier());

		return new ResponseEntity<SubmodelElementValue>(submodelElementValue, HttpStatus.OK);
	}

	public ResponseEntity<Submodel> getSubmodelMetadata(@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
			"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {

		Submodel submodel = service.getSubmodel();
		submodel.setSubmodelElements(null);

		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	public ResponseEntity<Reference> getSubmodelReference(@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
			"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {
		String accept = request.getHeader("Accept");

		Reference result = service.getSubmodel()
				.getSemanticID();

		return new ResponseEntity<Reference>(result, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelValueOnly> getSubmodelValueOnly(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelValueOnly result = new SubmodelValueOnly(service.getSubmodelElements());

		return new ResponseEntity<SubmodelValueOnly>(result, HttpStatus.OK);
	}

	public ResponseEntity<Void> patchSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "SubmodelElement object", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {
		service.deleteSubmodelElement(idShortPath.getIdentifier());
		service.createSubmodelElement(body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<Void> patchSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The SubmodelElement in its ValueOnly representation", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElementValue body,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {
		service.setSubmodelElementValue(idShortPath.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<SubmodelElement> postSubmodelElement(@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {
		service.createSubmodelElement(body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}

	public ResponseEntity<SubmodelElement> postSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body) {
		service.createSubmodelElement(idShortPath.getIdentifier(), body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}

	public ResponseEntity<Void> putSubmodel(@Parameter(in = ParameterIn.DEFAULT, description = "Submodel object", required = true, schema = @Schema()) @Valid @RequestBody Submodel body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {

		service = serviceFactory.create(body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<Void> putSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") Base64UrlEncodedIdentifier idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {
		service.deleteSubmodelElement(idShortPath.getIdentifier());
		service.createSubmodelElement(idShortPath.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
