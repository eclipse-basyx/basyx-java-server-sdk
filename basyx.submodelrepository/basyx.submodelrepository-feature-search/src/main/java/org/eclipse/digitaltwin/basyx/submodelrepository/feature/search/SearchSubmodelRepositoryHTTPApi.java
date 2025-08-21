/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@Validated
public interface SearchSubmodelRepositoryHTTPApi {
	@Operation(
			summary = "Returns all Submodels that conform to the input query",
			tags = { "Submodel Repository API" },
			operationId = "querySubmodels"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Requested Submodels",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = QueryResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
			@ApiResponse(responseCode = "500", description = "Internal Server Error",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
			@ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
	})
	@RequestMapping(
			value = "/query/submodels",
			produces = { "application/json" },
			consumes = { "application/json" },
			method = RequestMethod.POST
	)
	ResponseEntity<QueryResponse> querySubmodels(
			@Parameter(
					description = "Query object",
					required = true,
					schema = @Schema(implementation = String.class)
			)
			@Valid @RequestBody AASQuery query,

			@Parameter(
					in = ParameterIn.QUERY,
					description = "Maximum number of results to be returned"
			)
			@RequestParam(value = "limit", required = false) Integer limit,

			@Parameter(
					in = ParameterIn.QUERY,
					description = "Cursor for pagination"
			)
			@RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor
	);


}
