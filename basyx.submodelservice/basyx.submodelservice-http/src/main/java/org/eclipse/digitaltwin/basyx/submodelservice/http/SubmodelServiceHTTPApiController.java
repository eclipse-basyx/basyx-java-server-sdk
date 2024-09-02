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
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.eclipse.digitaltwin.basyx.pagination.GetSubmodelElementsResult;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
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

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-07-20T09:38:58.667119080Z[GMT]")
@RestController
public class SubmodelServiceHTTPApiController implements SubmodelServiceHTTPApi {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private SubmodelService service;

	@Autowired
	public SubmodelServiceHTTPApiController(SubmodelService service) {
		this.service = service;
	}

	@Override
	public ResponseEntity<Void> deleteSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath) {
		service.deleteSubmodelElement(idShortPath);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<PagedResult> getAllSubmodelElements(
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		PaginationInfo pInfo = new PaginationInfo(limit, decodedCursor);
		CursorResult<List<SubmodelElement>> submodelElements = service.getSubmodelElements(pInfo);

		GetSubmodelElementsResult paginatedSubmodelElement = new GetSubmodelElementsResult();

		String encodedCursor = getEncodedCursorFromCursorResult(submodelElements);

		paginatedSubmodelElement.setResult(submodelElements.getResult());
		paginatedSubmodelElement.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<PagedResult>(paginatedSubmodelElement, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Submodel> getSubmodel(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		Submodel submodel = service.getSubmodel();

		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElement> getSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelElement submodelElement = service.getSubmodelElement(idShortPath);

		return new ResponseEntity<SubmodelElement>(submodelElement, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelElementValue> getSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelElementValue submodelElementValue = service.getSubmodelElementValue(idShortPath);

		return new ResponseEntity<SubmodelElementValue>(submodelElementValue, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Submodel> getSubmodelMetadata(@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
			"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {

		Submodel submodel = service.getSubmodel();
		submodel.setSubmodelElements(null);

		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SubmodelValueOnly> getSubmodelValueOnly(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent) {

		SubmodelValueOnly result = new SubmodelValueOnly(service.getSubmodelElements(NO_LIMIT_PAGINATION_INFO).getResult());

		return new ResponseEntity<SubmodelValueOnly>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> patchSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The SubmodelElement in its ValueOnly representation", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElementValue body,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level) {
		service.setSubmodelElementValue(idShortPath, body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElement(@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {
		service.createSubmodelElement(body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<SubmodelElement> postSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body) {
		service.createSubmodelElement(idShortPath, body);
		return new ResponseEntity<SubmodelElement>(HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<Void> putSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {
		
		service.updateSubmodelElement(idShortPath, body);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<Void> patchSubmodelValueOnly(@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody List<SubmodelElement> body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level) {

		service.patchSubmodelElements(body);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<OperationResult> invokeOperation(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Operation request object", required = true, schema = @Schema()) @Valid @RequestBody OperationRequest body) {
		List<OperationVariable> inVars = new ArrayList<>();
		inVars.addAll(body.getInputArguments());
		inVars.addAll(body.getInoutputArguments());

		List<OperationVariable> result = Arrays.asList(service.invokeOperation(idShortPath, inVars.toArray(new OperationVariable[0])));

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
		return new DefaultOperationResult.Builder()
				.outputArguments(outputVars).inoutputArguments(inoutputVars)
				.build();
	}

	private String getEncodedCursorFromCursorResult(CursorResult<?> cursorResult) {
		if (cursorResult == null || cursorResult.getCursor() == null) {
			return null;
		}

		return Base64UrlEncodedCursor.encodeCursor(cursorResult.getCursor());
	}

	@Override
	public ResponseEntity<Resource> getFileByPath(String idShortPath) {
		Resource resource = new FileSystemResource(service.getFileByPath(idShortPath));

		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> putFileByPath(String idShortPath, String fileName, @Valid MultipartFile file) {
		InputStream fileInputstream = null;
		try {
			fileInputstream = file.getInputStream();
			service.setFileValue(idShortPath, fileName, fileInputstream);
			closeInputStream(fileInputstream);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ElementDoesNotExistException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (ElementNotAFileException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
		} catch (IOException e) {
			closeInputStream(fileInputstream);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Void> deleteFileByPath(String idShortPath) {
		try {
			service.deleteFileValue(idShortPath);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FileDoesNotExistException | ElementDoesNotExistException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (ElementNotAFileException e) {
			return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
		}
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
