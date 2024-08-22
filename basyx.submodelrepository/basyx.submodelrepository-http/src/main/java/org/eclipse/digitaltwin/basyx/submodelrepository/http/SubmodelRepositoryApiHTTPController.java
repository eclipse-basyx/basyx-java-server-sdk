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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationResult;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifierSize;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.eclipse.digitaltwin.basyx.pagination.GetSubmodelElementsResult;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination.GetSubmodelsResult;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
public class SubmodelRepositoryApiHTTPController implements SubmodelRepositoryHTTPApi {

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
	public ResponseEntity<PagedResult> getAllSubmodels(@Base64UrlEncodedIdentifierSize(min = 1, max = 3072) @Valid Base64UrlEncodedIdentifier semanticId, @Valid String idShort, @Min(1) @Valid Integer limit,
			@Valid Base64UrlEncodedCursor cursor, @Valid String level, @Valid String extent) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		PaginationInfo pInfo = new PaginationInfo(limit, decodedCursor);

		CursorResult<List<Submodel>> cursorResult = repository.getAllSubmodels(pInfo);

		GetSubmodelsResult paginatedSubmodel = new GetSubmodelsResult();

		String encodedCursor = getEncodedCursorFromCursorResult(cursorResult);

		paginatedSubmodel.result(new ArrayList<>(cursorResult.getResult()));
		paginatedSubmodel.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<PagedResult>(paginatedSubmodel, HttpStatus.OK);
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
	public ResponseEntity<PagedResult> getAllSubmodelElements(Base64UrlEncodedIdentifier submodelIdentifier, @Min(1) @Valid Integer limit, @Valid Base64UrlEncodedCursor cursor, @Valid String level, @Valid String extent) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		PaginationInfo pInfo = new PaginationInfo(limit, decodedCursor);
		CursorResult<List<SubmodelElement>> cursorResult = repository.getSubmodelElements(submodelIdentifier.getIdentifier(), pInfo);

		GetSubmodelElementsResult paginatedSubmodelElement = new GetSubmodelElementsResult();
		String encodedCursor = getEncodedCursorFromCursorResult(cursorResult);

		paginatedSubmodelElement.result(new ArrayList<>(cursorResult.getResult()));
		paginatedSubmodelElement.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<PagedResult>(paginatedSubmodelElement, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElement> getSubmodelElementByPathSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid String level, @Valid String extent) {
		return handleSubmodelElementValueNormalGetRequest(submodelIdentifier.getIdentifier(), idShortPath);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementByPathSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid SubmodelElement body, @Valid String level, @Valid String extent) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, @Valid SubmodelElement body) {
		repository.createSubmodelElement(submodelIdentifier.getIdentifier(), body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> putSubmodelElementByPathSubmodelRepo(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier,
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {
		repository.updateSubmodelElement(submodelIdentifier.getIdentifier(), idShortPath, body);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
		return new ResponseEntity<SubmodelValueOnly>(repository.getSubmodelByIdValueOnly(submodelIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Submodel> getSubmodelByIdMetadata(Base64UrlEncodedIdentifier submodelIdentifier, @Valid String level) {
		return new ResponseEntity<Submodel>(repository.getSubmodelByIdMetadata(submodelIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> getFileByPath(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath) {
		Resource resource = new FileSystemResource(repository.getFileByPathSubmodel(submodelIdentifier.getIdentifier(), idShortPath));

		return new ResponseEntity<Resource>(resource, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putFileByPath(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, String fileName, @Valid MultipartFile file) {
		InputStream fileInputstream = null;
		try {
			fileInputstream = file.getInputStream();
			repository.setFileValue(submodelIdentifier.getIdentifier(), idShortPath, fileName, fileInputstream);
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (ElementDoesNotExistException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (ElementNotAFileException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED);
		} catch (IOException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Void> deleteFileByPath(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath) {
		try {
			repository.deleteFileValue(submodelIdentifier.getIdentifier(), idShortPath);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (FileDoesNotExistException | ElementDoesNotExistException e) {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (ElementNotAFileException e) {
			return new ResponseEntity<Void>(HttpStatus.PRECONDITION_FAILED);
		}
	}

	@Override
	public ResponseEntity<Void> patchSubmodelByIdValueOnly(Base64UrlEncodedIdentifier submodelIdentifier, @Valid List<SubmodelElement> body, @Valid String level) {
		repository.patchSubmodelElements(submodelIdentifier.getIdentifier(), body);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private ResponseEntity<Void> handleSubmodelElementValueSetRequest(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, SubmodelElementValue body) {
		repository.setSubmodelElementValue(submodelIdentifier.getIdentifier(), idShortPath, body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	private ResponseEntity<SubmodelElementValue> handleSubmodelElementValueGetRequest(String submodelIdentifier, String idShortPath) {
		SubmodelElementValue value = repository.getSubmodelElementValue(submodelIdentifier, idShortPath);
		return new ResponseEntity<SubmodelElementValue>(value, HttpStatus.OK);
	}

	private ResponseEntity<SubmodelElement> handleSubmodelElementValueNormalGetRequest(String submodelIdentifier, String idShortPath) {
		SubmodelElement submodelElement = repository.getSubmodelElement(submodelIdentifier, idShortPath);
		return new ResponseEntity<SubmodelElement>(submodelElement, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<OperationResult> invokeOperationSubmodelRepo(Base64UrlEncodedIdentifier submodelIdentifier, String idShortPath, @Valid OperationRequest body, @Valid Boolean async) {
		List<OperationVariable> inVars = new ArrayList<>();
		inVars.addAll(body.getInputArguments());
		inVars.addAll(body.getInoutputArguments());

		List<OperationVariable> result = Arrays.asList(repository.invokeOperation(submodelIdentifier.getIdentifier(), idShortPath, inVars.toArray(new OperationVariable[0])));

		List<OperationVariable> outVars = new ArrayList<>(result);
		List<OperationVariable> inoutputVars = new ArrayList<>();

		if (!body.getInoutputArguments().isEmpty()) {
			List<String> inoutputVarsIdShorts = body.getInoutputArguments().stream().map(OperationVariable::getValue).map(SubmodelElement::getIdShort).toList();

			inoutputVars = result.stream().filter(opVar -> inoutputVarsIdShorts.contains(opVar.getValue().getIdShort())).toList();

			outVars.removeAll(inoutputVars);
		}

		return ResponseEntity.ok(createOperationResult(outVars, inoutputVars));
	}

	private OperationResult createOperationResult(List<OperationVariable> outputVars, List<OperationVariable> inoutputVars) {
		return new DefaultOperationResult.Builder().outputArguments(outputVars).inoutputArguments(inoutputVars).build();
	}

	private String getEncodedCursorFromCursorResult(CursorResult<?> cursorResult) {
		if (cursorResult == null || cursorResult.getCursor() == null) {
			return null;
		}

		return Base64UrlEncodedCursor.encodeCursor(cursorResult.getCursor());
	}

	private void closeInputStream(InputStream fileInputstream) {
		if (fileInputstream == null) {
			return;
		}

		try {
			fileInputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
