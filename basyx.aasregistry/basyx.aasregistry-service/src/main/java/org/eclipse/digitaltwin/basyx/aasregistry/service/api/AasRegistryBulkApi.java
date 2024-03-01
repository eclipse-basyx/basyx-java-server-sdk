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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

/**
 * Interface for the AAS Registry Bulk API
 * 
 * @author mateusmolina
 */
public interface AasRegistryBulkApi {

    @PostMapping("/bulk/shell-descriptors")
    public ResponseEntity<String> postBulkShellDescriptors(
            @Parameter(name = "AssetAdministrationShellDescriptor", description = "List of Asset Administration Shell Descriptor objects", required = true) @Valid @RequestBody List<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptors);

    @PutMapping("/bulk/shell-descriptors")
    public ResponseEntity<String> putBulkShellDescriptors(
            @Parameter(name = "AssetAdministrationShellDescriptor", description = "List of Asset Administration Shell Descriptor objects", required = true) @Valid @RequestBody List<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptors);

    @DeleteMapping("/bulk/shell-descriptors")
    public ResponseEntity<String> deleteBulkShellDescriptors(
            @Parameter(name = "aasIdentifier", description = "List of Asset Administration Shell Descriptor unique identifiers (UTF8-BASE64-URL-encoded)", required = true, in = ParameterIn.PATH) @Valid @RequestBody List<String> aasIdentifiersBase64);

    @GetMapping("/bulk/status/{handleId}")
    public ResponseEntity<Void> getBulkStatus(@Parameter(in = ParameterIn.PATH, description = "The handleId for the transaction", required = true, schema = @Schema()) @PathVariable("handleId") long handleId);

    @GetMapping("/bulk/result/{handleId}")
    public ResponseEntity<BulkOperationResult> getBulkResult(@Parameter(in = ParameterIn.PATH, description = "The handleId for the transaction", required = true, schema = @Schema()) @PathVariable("handleId") long handleId);
}
