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

package org.eclipse.digitaltwin.basyx.aasxfileserver.http;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.springframework.core.io.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-06-26T06:44:00.548939417Z[GMT]")
@Validated
public interface AASXFileServerHttpApi {

    @Operation(summary = "Deletes a specific AASX package from the server", description = "", tags={ "AASX File Server API" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted successfully"),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/packages/{packageId}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteAASXByPackageId(@Parameter(in = ParameterIn.PATH, description = "The package Id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId
);


    @Operation(summary = "Returns a specific AASX package from the server", description = "", tags={ "AASX File Server API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Requested AASX package", content = @Content(mediaType = "application/asset-administration-shell-package", schema = @Schema(implementation = Resource.class))),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/packages/{packageId}",
        produces = { "application/asset-administration-shell-package", "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Resource> getAASXByPackageId(@Parameter(in = ParameterIn.PATH, description = "The package Id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId
);


    @Operation(summary = "Returns a list of available AASX packages at the server", description = "", tags={ "AASX File Server API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Requested package list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/packages",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<PagedResult> getAllAASXPackageIds(@Parameter(in = ParameterIn.QUERY, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)" ,schema=@Schema()) @Valid @RequestParam(value = "aasId", required = false) Base64UrlEncodedIdentifier aasId
, @Min(1)@Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array" ,schema=@Schema(allowableValues={ "1" }, minimum="1"
)) @Valid @RequestParam(value = "limit", required = false) Integer limit
, @Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue" ,schema=@Schema()) @Valid @RequestParam(value = "cursor", required = false) Base64UrlEncodedCursor cursor
);


    @Operation(summary = "Stores the AASX package at the server", description = "", tags={ "AASX File Server API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "AASX package stored successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PackageDescription.class))),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })



    @RequestMapping(value = "/packages",
        produces = { "application/json" },
        consumes = { "multipart/form-data" },
        method = RequestMethod.POST)
    ResponseEntity<PackageDescription> postAASXPackage(@Parameter(in = ParameterIn.DEFAULT, description = "", required=true,schema=@Schema()) @RequestParam(value="aasIds", required=true) List<Base64UrlEncodedIdentifier> aasIds
, @Parameter(description = "") @Valid @RequestPart(value="file", required=true) MultipartFile file
, @Parameter(in = ParameterIn.DEFAULT, description = "", required=true,schema=@Schema()) @RequestParam(value="fileName", required=true)  Base64UrlEncodedIdentifier fileName
    );


    @Operation(summary = "Updates the AASX package at the server", description = "", tags={ "AASX File Server API" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "AASX package updated successfully"),
        
        @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "401", description = "Unauthorized, e.g. the server refused the authorization attempt.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
        
        @ApiResponse(responseCode = "200", description = "Default error handling for unmentioned status codes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))) })
    @RequestMapping(value = "/packages/{packageId}",
        produces = { "application/json" }, 
        consumes = { "multipart/form-data" }, 
        method = RequestMethod.PUT)
    ResponseEntity<Void> putAASXByPackageId(@Parameter(in = ParameterIn.PATH, description = "The package Id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("packageId") Base64UrlEncodedIdentifier packageId
, @Parameter(in = ParameterIn.DEFAULT, description = "", required=true,schema=@Schema()) @RequestParam(value="aasIds", required=true)  List<Base64UrlEncodedIdentifier> aasIds
, @Parameter(description = "") @Valid @RequestPart(value="file", required=true) MultipartFile file
, @Parameter(in = ParameterIn.DEFAULT, description = "", required=true,schema=@Schema()) @RequestParam(value="fileName", required=true)  Base64UrlEncodedIdentifier fileName
);

}

