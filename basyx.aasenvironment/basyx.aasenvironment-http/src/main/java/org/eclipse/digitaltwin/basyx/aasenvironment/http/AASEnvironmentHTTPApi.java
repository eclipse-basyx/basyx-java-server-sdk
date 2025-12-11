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

package org.eclipse.digitaltwin.basyx.aasenvironment.http;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-05-08T12:36:05.278579031Z[GMT]")
@Validated
public interface AASEnvironmentHTTPApi {

	@Operation(summary = "Returns an appropriate serialization based on the specified format (see SerializationFormat)", description = "", tags = { "Serialization API" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Requested serialization based on SerializationFormat", content = @Content(mediaType = "application/asset-administration-shell-package+xml", schema = @Schema(implementation = Resource.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/serialization", produces = { "application/asset-administration-shell-package+xml", "application/json", "application/xml" }, method = RequestMethod.GET)
	ResponseEntity<Resource> generateSerializationByIds(
			@Parameter(in = ParameterIn.QUERY, description = "The Asset Administration Shells' unique ids (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "aasIds", required = false) List<String> aasIds,
			@Parameter(in = ParameterIn.QUERY, description = "The Submodels' unique ids (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "submodelIds", required = false) List<String> submodelIds,
			@Parameter(in = ParameterIn.QUERY, description = "Include Concept Descriptions?", schema = @Schema(defaultValue = "true")) @Valid @RequestParam(value = "includeConceptDescriptions", required = false, defaultValue = "true") Boolean includeConceptDescriptions);


	@Operation(summary = "Upload an environment file (XML, JSON, AASX)", description = "Uploads an environment file for processing.", tags = { "Environment API" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Environment successfully processed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request, invalid file format or structure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error, processing failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))) })
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	ResponseEntity<Boolean> uploadEnvironment(
			@Parameter(description = "An environment file (XML, JSON, AASX)") @Valid @RequestParam("file") MultipartFile envFile,
			@Parameter(description = "Flag to indicate if already existing Ids should be ignored when reuploading an environment (default: false)", schema = @Schema(defaultValue = "false"))
			@RequestParam(value = "ignore-duplicates", required = false, defaultValue = "false") boolean ignoreDuplicates
	) throws IOException, InvalidFormatException, DeserializationException, ZipBombException;
}
