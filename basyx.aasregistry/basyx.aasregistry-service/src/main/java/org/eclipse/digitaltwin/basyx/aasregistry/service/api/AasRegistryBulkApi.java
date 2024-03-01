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

package org.eclipse.digitaltwin.basyx.aasregistry.service.api;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.BulkOperationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * Interface for the AAS Registry Bulk API
 * 
 * Based on specifcation:
 * {@link https://app.swaggerhub.com/apis/Plattform_i40/AssetAdministrationShellRegistryServiceSpecification/V3.1.0_SSP-003}
 * 
 * @author mateusmolina
 */
public interface AasRegistryBulkApi {

        @Operation(summary = "Create multiple AAS Descriptors", description = "Registers multiple Asset Administration Shells in a bulk operation.")
        @ApiResponses({ @ApiResponse(responseCode = "202", description = "Accepted for processing, but the processing has not been completed."), @ApiResponse(responseCode = "400", description = "Bad request. Invalid input provided."),
                        @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication required."), @ApiResponse(responseCode = "500", description = "Internal server error.") })
        @PostMapping("/bulk/shell-descriptors")
        ResponseEntity<String> postBulkShellDescriptors(
                        @Parameter(name = "AssetAdministrationShellDescriptor", description = "List of Asset Administration Shell Descriptor objects", required = true) @Valid @RequestBody List<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptors);

        @Operation(summary = "Update multiple AAS Descriptors", description = "Updates multiple Asset Administration Shell Descriptors in a bulk operation.")
        @ApiResponses({ @ApiResponse(responseCode = "202", description = "Accepted for processing, but the processing has not been completed."), @ApiResponse(responseCode = "400", description = "Bad request. Invalid input provided."),
                        @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication required."), @ApiResponse(responseCode = "500", description = "Internal server error.") })
        @PutMapping("/bulk/shell-descriptors")
        ResponseEntity<String> putBulkShellDescriptors(
                        @Parameter(name = "AssetAdministrationShellDescriptor", description = "List of Asset Administration Shell Descriptor objects", required = true) @Valid @RequestBody List<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptors);

        @Operation(summary = "Delete multiple AAS Descriptors", description = "Deletes multiple Asset Administration Shell Descriptors using their unique identifiers.")
        @ApiResponses({ @ApiResponse(responseCode = "202", description = "Accepted for processing, but the processing has not been completed."), @ApiResponse(responseCode = "400", description = "Bad request. Invalid input provided."),
                        @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication required."), @ApiResponse(responseCode = "404", description = "Not found. One or more identifiers do not match existing descriptors."),
                        @ApiResponse(responseCode = "500", description = "Internal server error.") })
        @DeleteMapping("/bulk/shell-descriptors")
        ResponseEntity<String> deleteBulkShellDescriptors(
                        @Parameter(name = "aasIdentifier", description = "List of Asset Administration Shell Descriptor unique identifiers (UTF8-BASE64-URL-encoded)", required = true, in = ParameterIn.PATH) @Valid @RequestBody List<String> aasIdentifiersBase64);

        @Operation(summary = "Get Bulk Operation Status", description = "Retrieves the status of a bulk operation using the handleId.")
        @ApiResponses({ @ApiResponse(responseCode = "204", description = " The bulk request itself was correct and all elements have been\n" + //
                        "            processed. The server may remove the result resource after  it was\n" + //
                        "            requested once (by any client) or after a certain time period."), @ApiResponse(responseCode = "400", description = "There was an error in the processing of the request. Either the bulk\n" + //
                                        "            request itself was not correct, or at least of it's part requests. \n" + //
                                        "            The whole transaction has been rolled back."),
                        @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication required."), @ApiResponse(responseCode = "404", description = "Not found. The handleId does not exist."),
                        @ApiResponse(responseCode = "500", description = "Internal server error.") })
        @GetMapping("/bulk/status/{handleId}")
        ResponseEntity<Void> getBulkOperationStatus(@Parameter(in = ParameterIn.PATH, description = "The handleId for the transaction", required = true, schema = @Schema()) @PathVariable("handleId") long handleId);

        @Operation(summary = "Get Bulk Operation Result", description = "Retrieves the result of a bulk operation using the handleId.")
        @ApiResponses({ @ApiResponse(responseCode = "200", description = "Bulk operation result retrieved successfully."), @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication required."),
                        @ApiResponse(responseCode = "404", description = "Not found. The handleId does not exist."), @ApiResponse(responseCode = "500", description = "Internal server error.") })
        @GetMapping("/bulk/result/{handleId}")
        ResponseEntity<BulkOperationResult> getBulkOperationResult(@Parameter(in = ParameterIn.PATH, description = "The handleId for the transaction", required = true, schema = @Schema()) @PathVariable("handleId") long handleId);
}