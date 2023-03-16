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

package org.eclipse.digitaltwin.basyx.aasrepository.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
public class AasRepositoryApiHTTPController implements AasRepositoryHTTPApi {

	private static final Logger log = LoggerFactory.getLogger(AasRepositoryApiHTTPController.class);

	private final ObjectMapper objectMapper;

	private final AasRepository aasRepository;

	@Autowired
	public AasRepositoryApiHTTPController(ObjectMapper objectMapper, AasRepository aasRepository) {
		this.objectMapper = objectMapper;
		this.aasRepository = aasRepository;
	}

	@Override
	public ResponseEntity<Void> deleteAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		aasRepository.deleteAas(aasIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelReferenceById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier) {
		aasRepository.removeSubmodelReference(aasIdentifier.getIdentifier(), submodelIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Reference>> getAllSubmodelReferences(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		List<Reference> submodelReferences = aasRepository.getSubmodelReferences(aasIdentifier.getIdentifier());
		return new ResponseEntity<List<Reference>>(submodelReferences, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<AssetAdministrationShell> getAssetAdministrationShell(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the request or response kind of the resource", schema = @Schema(allowableValues = { "normal", "trimmed", "value", "reference",
					"path" }, defaultValue = "normal")) @Valid @RequestParam(value = "content", required = false, defaultValue = "normal") String content) {
		try {
			return new ResponseEntity<AssetAdministrationShell>(objectMapper.readValue("\"\"", AssetAdministrationShell.class), HttpStatus.NOT_IMPLEMENTED);
		} catch (IOException e) {
			log.error("Couldn't serialize response for content type application/json", e);
			return new ResponseEntity<AssetAdministrationShell>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<AssetAdministrationShell> getAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		return new ResponseEntity<AssetAdministrationShell>(aasRepository.getAas(aasIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<AssetAdministrationShell> postAssetAdministrationShell(
			@Parameter(in = ParameterIn.DEFAULT, description = "Asset Administration Shell object", required = true, schema = @Schema()) @Valid @RequestBody AssetAdministrationShell body) {
		aasRepository.createAas(body);
		return new ResponseEntity<AssetAdministrationShell>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Reference> postSubmodelReference(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Reference to the Submodel", required = true, schema = @Schema()) @Valid @RequestBody Reference body) {
		aasRepository.addSubmodelReference(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Reference>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> putAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Asset Administration Shell object", required = true, schema = @Schema()) @Valid @RequestBody AssetAdministrationShell body) {
		aasRepository.updateAas(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<List<AssetAdministrationShell>> getAllAssetAdministrationShells(
			@Parameter(in = ParameterIn.QUERY, description = "The Asset Administration Shell’s IdShort", schema = @Schema()) @Valid @RequestParam(value = "idShort", required = false) String idShort) {
		return new ResponseEntity<List<AssetAdministrationShell>>(new ArrayList<>(aasRepository.getAllAas()), HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Void> postAssetInformationByAasId(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Asset Information object", required = true, schema = @Schema()) @Valid @RequestBody AssetInformation body) {
		aasRepository.setAssetInformation(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<AssetInformation> getAssetInformationByAasId(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		return new ResponseEntity<AssetInformation>(aasRepository.getAssetInformation(aasIdentifier.getIdentifier()), HttpStatus.OK);
	}

}
