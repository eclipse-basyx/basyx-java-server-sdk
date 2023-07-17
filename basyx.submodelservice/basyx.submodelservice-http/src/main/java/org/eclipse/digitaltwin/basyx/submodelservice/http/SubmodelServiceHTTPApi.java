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

import javax.validation.Valid;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.http.model.Result;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-07-16T14:12:09.075410867Z[GMT]")
@Validated
public interface SubmodelServiceHTTPApi {

	@Operation(summary = "Deletes a specific Submodel-Element from the Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "204", description = "Submodel-Element deleted successfully"),

			@ApiResponse(responseCode = "404", description = "Submodel-Element not found") })
	@RequestMapping(value = "/submodel/submodelElements/{seIdShortPath}", produces = { "application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<Result> deleteSubmodelElementByIdShort(@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath);

	@Operation(summary = "Retrieves the entire Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Submodel.class))),

			@ApiResponse(responseCode = "404", description = "Submodel not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<Submodel> getSubmodel();

	@Operation(summary = "Retrieves a specific Submodel-Element from the Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Returns the requested Submodel-Element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelElement.class))),

			@ApiResponse(responseCode = "404", description = "Submodel Element not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodelElements/{seIdShortPath}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelElement> getSubmodelElementByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath);

	@Operation(summary = "Retrieves the value of a specific Submodel-Element from the Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Returns the value of a specific Submodel-Element", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),

			@ApiResponse(responseCode = "404", description = "Submodel / Submodel-Element not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodelElements/{seIdShortPath}/value", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelElementValue> getSubmodelElementValueByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath);

	@Operation(summary = "Retrieves all Submodel-Elements from the Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Returns a list of found Submodel-Elements", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubmodelElement.class)))),

			@ApiResponse(responseCode = "404", description = "Submodel not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodelElements", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<SubmodelElement>> getSubmodelElements();

	@Operation(summary = "Retrieves the minimized version of a Submodel, i.e. only the values of SubmodelElements are serialized and returned", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),

			@ApiResponse(responseCode = "404", description = "Submodel not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/values", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<SubmodelValueOnly> getSubmodelValues();

	@Operation(summary = "Creates or updates a Submodel-Element at the Submodel", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Submodel-Element created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelElement.class))),

			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
	@RequestMapping(value = "/submodel/submodelElements/{seIdShortPath}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	ResponseEntity<SubmodelElement> putSubmodelElement(@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The Submodel-Element object", schema = @Schema()) @Valid @RequestBody SubmodelElement body);

	@Operation(summary = "Updates the Submodel-Element's value", description = "", tags = { "Submodel" })
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Submodel-Element's value changed successfully"),

			@ApiResponse(responseCode = "404", description = "Submodel-Element not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),

			@ApiResponse(responseCode = "405", description = "Method not allowed") })
	@RequestMapping(value = "/submodel/submodelElements/{seIdShortPath}/value", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	ResponseEntity<SubmodelElementValue> putSubmodelElementValueByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The new value", schema = @Schema()) @Valid @RequestBody SubmodelElementValue body);

}
