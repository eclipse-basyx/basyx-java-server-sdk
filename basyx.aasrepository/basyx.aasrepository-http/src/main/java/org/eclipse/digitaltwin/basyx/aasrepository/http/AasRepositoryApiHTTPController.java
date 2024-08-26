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

package org.eclipse.digitaltwin.basyx.aasrepository.http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.http.pagination.GetAssetAdministrationShellsResult;
import org.eclipse.digitaltwin.basyx.aasrepository.http.pagination.GetReferencesResult;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
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
	public ResponseEntity<PagedResult> getAllAssetAdministrationShells(@Valid List<SpecificAssetId> assetIds, @Valid String idShort, @Min(0) @Valid Integer limit, @Valid Base64UrlEncodedCursor cursor) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		PaginationInfo paginationInfo = new PaginationInfo(limit, decodedCursor);
		CursorResult<List<AssetAdministrationShell>> paginatedAAS = aasRepository.getAllAas(paginationInfo);

		GetAssetAdministrationShellsResult result = new GetAssetAdministrationShellsResult();

		String encodedCursor = getEncodedCursorFromCursorResult(paginatedAAS);

		result.setResult(paginatedAAS.getResult());
		result.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<PagedResult> getAllSubmodelReferencesAasRepository(Base64UrlEncodedIdentifier aasIdentifier, @Min(0) @Valid Integer limit, @Valid Base64UrlEncodedCursor cursor) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		PaginationInfo paginationInfo = new PaginationInfo(limit, decodedCursor);
		CursorResult<List<Reference>> submodelReferences = aasRepository.getSubmodelReferences(aasIdentifier.getIdentifier(), paginationInfo);

		GetReferencesResult result = new GetReferencesResult();

		String encodedCursor = getEncodedCursorFromCursorResult(submodelReferences);

		result.setResult(submodelReferences.getResult());
		result.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Reference> postSubmodelReferenceAasRepository(Base64UrlEncodedIdentifier aasIdentifier, @Valid Reference body) {
		try {
			aasRepository.addSubmodelReference(aasIdentifier.getIdentifier(), body);
		}catch(CollidingSubmodelReferenceException e){
			return new ResponseEntity<Reference>(HttpStatus.CONFLICT);
		}
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

	private String getEncodedCursorFromCursorResult(CursorResult<?> cursorResult) {
		if (cursorResult == null || cursorResult.getCursor() == null) {
			return null;
		}

		return Base64UrlEncodedCursor.encodeCursor(cursorResult.getCursor());
	}

	@Override
	public ResponseEntity<Void> deleteThumbnailAasRepository(Base64UrlEncodedIdentifier aasIdentifier) {
		aasRepository.deleteThumbnail(aasIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> getThumbnailAasRepository(Base64UrlEncodedIdentifier aasIdentifier) {
        Resource resource = new FileSystemResource(aasRepository.getThumbnail(aasIdentifier.getIdentifier()));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

	@Override
	public ResponseEntity<Void> putThumbnailAasRepository(Base64UrlEncodedIdentifier aasIdentifier, String fileName, @Valid MultipartFile file) {
		InputStream fileInputstream = null;
		try {
			fileInputstream = file.getInputStream();
			aasRepository.setThumbnail(aasIdentifier.getIdentifier(), fileName, file.getContentType(), fileInputstream);
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (IOException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void closeInputStream(InputStream fileInputstream) {
		if (fileInputstream == null)
			return;

		try {
			fileInputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
