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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.pagination.GetSubmodelElementsResult;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-07-16T14:12:09.075410867Z[GMT]")
@Validated
public interface SubmodelServiceHTTPApi {

	@Operation(summary = "Deletes a submodel element at a specified path within the submodel elements hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel element deleted successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}", produces = { "application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath);

	@Operation(summary = "Returns all submodel elements including their hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of found submodel elements", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GetSubmodelElementsResult.class)))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<PagedResult> getAllSubmodelElements(
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent);

	@Operation(summary = "Returns the Submodel", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested Submodel", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submodel.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<Submodel> getSubmodel(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent);

	@Operation(summary = "Returns a specific submodel element from the Submodel at a specified path", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested submodel element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetSubmodelElementsResult.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelElement> getSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent);

	@Operation(summary = "Returns a specific submodel element from the Submodel at a specified path in the ValueOnly representation", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested submodel element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelElementValue.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/$value", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelElementValue> getSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent);

	@Operation(summary = "Returns the metadata attributes of a specific Submodel", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested Submodel in the metadata representation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submodel.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/$metadata", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<Submodel> getSubmodelMetadata(@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
			"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level);

	@Operation(summary = "Returns the Submodel in the ValueOnly representation", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ValueOnly representation of the Submodel", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelValueOnly.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/$value", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelValueOnly> getSubmodelValueOnly(
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level,
			@Parameter(in = ParameterIn.QUERY, description = "Determines to which extent the resource is being serialized", schema = @Schema(allowableValues = { "withBlobValue",
					"withoutBlobValue" }, defaultValue = "withoutBlobValue")) @Valid @RequestParam(value = "extent", required = false, defaultValue = "withoutBlobValue") String extent);

	@Operation(summary = "Updates the value of an existing SubmodelElement", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/$value", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PATCH)
	ResponseEntity<Void> patchSubmodelElementByPathValueOnly(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The SubmodelElement in its ValueOnly representation", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElementValue body,
			@Min(1) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array", schema = @Schema(allowableValues = {
					"1" }, minimum = "1")) @Valid @RequestParam(value = "limit", required = false) Integer limit,
			@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", schema = @Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"core" }, defaultValue = "core")) @Valid @RequestParam(value = "level", required = false, defaultValue = "core") String level);

	@Operation(summary = "Creates a new submodel element", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Submodel element created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelElement.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<SubmodelElement> postSubmodelElement(@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = { "deep",
					"core" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level);

	@Operation(summary = "Creates a new submodel element at a specified path within submodel elements hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Submodel element created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelElement.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<SubmodelElement> postSubmodelElementByPath(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body);

	@Operation(summary = "Synchronously invokes an Operation at a specified path", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Operation result object", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OperationResult.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "405", description = "Method not allowed - Invoke only valid for Operation submodel element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/invoke", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<OperationResult> invokeOperation(
			@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Operation request object", required = true, schema = @Schema()) @Valid @RequestBody OperationRequest body);

	@Operation(summary = "Updates an existing submodel element at a specified path within submodel elements hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel element updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	ResponseEntity<Void> putSubmodelElementByPath(@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "Requested submodel element", required = true, schema = @Schema()) @Valid @RequestBody SubmodelElement body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level);

	@Operation(summary = "Updates the values of the Submodel", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/$value", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PATCH)
	ResponseEntity<Void> patchSubmodelValueOnly(@Parameter(in = ParameterIn.DEFAULT, description = "Submodel object in its ValueOnly representation", required = false, schema = @Schema()) @Valid @RequestBody List<SubmodelElement> body,
			@Parameter(in = ParameterIn.QUERY, description = "Determines the structural depth of the respective resource content", schema = @Schema(allowableValues = {
					"deep" }, defaultValue = "deep")) @Valid @RequestParam(value = "level", required = false, defaultValue = "deep") String level);

	@Operation(summary = "Deletes file content of an existing submodel element at a specified path within submodel elements hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Submodel element updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/attachment", produces = { "application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<Void> deleteFileByPath(@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath);

	@Operation(summary = "Downloads file content from a specific submodel element from the Submodel at a specified path", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Requested file", content = @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = Resource.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "405", description = "Method not allowed - Download only valid for File submodel element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/attachment", produces = { "application/octet-stream", "application/json" }, method = RequestMethod.GET)
	ResponseEntity<Resource> getFileByPath(@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath);

	@Operation(summary = "Uploads file content to an existing submodel element at a specified path within submodel elements hierarchy", description = "", tags = { "Submodel API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel element updated successfully"),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "405", description = "Method not allowed - Upload only valid for File submodel element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodel-elements/{idShortPath}/attachment", produces = { "application/json" }, consumes = { "multipart/form-data" }, method = RequestMethod.PUT)
	ResponseEntity<Void> putFileByPath(@Parameter(in = ParameterIn.PATH, description = "IdShort path to the submodel element (dot-separated)", required = true, schema = @Schema()) @PathVariable("idShortPath") String idShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @RequestParam(value = "fileName", required = true) String fileName,
			@Parameter(description = "file detail") @Valid @RequestPart("file") MultipartFile file);
}
