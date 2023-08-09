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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetID;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.http.pagination.GetAssetAdministrationShellsResult;
import org.eclipse.digitaltwin.basyx.aasrepository.http.pagination.GetReferencesResult;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
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
public class AasRepositoryApiHTTPController implements AasRepositoryHTTPApi {
	private final AasRepository aasRepository;

	@Autowired
	public AasRepositoryApiHTTPController(AasRepository aasRepository) {
		this.aasRepository = aasRepository;
	}

	@Override
	public ResponseEntity<Void> deleteAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		aasRepository.deleteAas(aasIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<AssetAdministrationShell> getAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
		return new ResponseEntity<AssetAdministrationShell>(aasRepository.getAas(aasIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<AssetAdministrationShell> postAssetAdministrationShell(
			@Parameter(in = ParameterIn.DEFAULT, description = "Asset Administration Shell object", required = true, schema = @Schema()) @Valid @RequestBody AssetAdministrationShell body) {
		aasRepository.createAas(body);
		return new ResponseEntity<AssetAdministrationShell>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> putAssetAdministrationShellById(
			@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Asset Administration Shell object", required = true, schema = @Schema()) @Valid @RequestBody AssetAdministrationShell body) {
		aasRepository.updateAas(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelReferenceByIdAasRepository(Base64UrlEncodedIdentifier aasIdentifier, Base64UrlEncodedIdentifier submodelIdentifier) {
		aasRepository.removeSubmodelReference(aasIdentifier.getIdentifier(), submodelIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<PagedResult> getAllAssetAdministrationShells(@Valid List<SpecificAssetID> assetIds, @Valid String idShort, @Min(1) @Valid Integer limit, @Valid String cursor) {
		if (limit == null)
			limit = 100;
		if (cursor == null)
			cursor = "";

		PaginationInfo paginationInfo = new PaginationInfo(limit, cursor);
		CursorResult<List<AssetAdministrationShell>> paginatedAAS = aasRepository.getAllAas(paginationInfo);

		GetAssetAdministrationShellsResult result = new GetAssetAdministrationShellsResult();
		result.setResult(paginatedAAS.getResult());
		result.setPagingMetadata(new PagedResultPagingMetadata().cursor(paginatedAAS.getCursor()));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<PagedResult> getAllSubmodelReferencesAasRepository(Base64UrlEncodedIdentifier aasIdentifier, @Min(1) @Valid Integer limit, @Valid String cursor) {
		if (limit == null)
			limit = 100;
		if (cursor == null)
			cursor = "";

		PaginationInfo paginationInfo = new PaginationInfo(limit, cursor);
		CursorResult<List<Reference>> submodelReferences = aasRepository.getSubmodelReferences(aasIdentifier.getIdentifier(), paginationInfo);

		GetReferencesResult result = new GetReferencesResult();
		result.setResult(submodelReferences.getResult());
		result.setPagingMetadata(new PagedResultPagingMetadata().cursor(submodelReferences.getCursor()));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Reference> postSubmodelReferenceAasRepository(Base64UrlEncodedIdentifier aasIdentifier, @Valid Reference body) {
		aasRepository.addSubmodelReference(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Reference>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<AssetInformation> getAssetInformationAasRepository(Base64UrlEncodedIdentifier aasIdentifier) {
		return new ResponseEntity<AssetInformation>(aasRepository.getAssetInformation(aasIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putAssetInformationAasRepository(Base64UrlEncodedIdentifier aasIdentifier, @Valid AssetInformation body) {
		aasRepository.setAssetInformation(aasIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
