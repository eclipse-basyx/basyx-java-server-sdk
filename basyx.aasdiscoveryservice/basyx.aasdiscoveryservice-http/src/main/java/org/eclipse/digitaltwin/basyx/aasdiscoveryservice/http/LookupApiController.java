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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.pagination.InlineResponse200;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-10-10T10:16:17.046754509Z[GMT]")
@RestController
public class LookupApiController implements LookupApi {

    private final AasDiscoveryService aasDiscoveryService;
	private final ObjectMapper objectMapper;

    @Autowired
    public LookupApiController(AasDiscoveryService aasDiscoveryService, ObjectMapper objectMapper) {
    	this.aasDiscoveryService = aasDiscoveryService;
		this.objectMapper = objectMapper;
    }

    public ResponseEntity<Void> deleteAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
    	aasDiscoveryService.deleteAllAssetLinksById(aasIdentifier.getIdentifier());

		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<PagedResult> getAllAssetAdministrationShellIdsByAssetLink(@Parameter(in = ParameterIn.QUERY, description = "A list of specific Asset identifiers. Each Asset identifier is a base64-url-encoded [SpecificAssetId](https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.0.1#/components/schemas/SpecificAssetId)" ,schema=@Schema()) @Valid @RequestParam(value = "assetIds", required = false) List<Base64UrlEncodedIdentifier> assetIds,@Min(1)@Parameter(in = ParameterIn.QUERY, description = "The maximum number of elements in the response array" ,schema=@Schema(allowableValues={ "1" }, minimum="1"
)) @Valid @RequestParam(value = "limit", required = false) Integer limit,@Parameter(in = ParameterIn.QUERY, description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue" ,schema=@Schema()) @Valid @RequestParam(value = "cursor", required = false) String cursor) {

    	if (limit == null)
			limit = 100;

		if (cursor == null)
			cursor = "";

		PaginationInfo pInfo = new PaginationInfo(limit, cursor);

		List<AssetLink> decodedAssetLinks;
		try {
			decodedAssetLinks = getDecodedAssetLinks(assetIds);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		CursorResult<List<String>> filteredResult = aasDiscoveryService.getAllAssetAdministrationShellIdsByAssetLink(pInfo, decodedAssetLinks);

		InlineResponse200 paginatedAasIds = new InlineResponse200();
		paginatedAasIds.setResult(filteredResult.getResult());
		paginatedAasIds.setPagingMetadata(new PagedResultPagingMetadata().cursor(filteredResult.getCursor()));

		return new ResponseEntity<PagedResult>(paginatedAasIds, HttpStatus.OK);
    }

    public ResponseEntity<List<SpecificAssetId>> getAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier) {
        List<SpecificAssetId> assetLinks = aasDiscoveryService.getAllAssetLinksById(aasIdentifier.getIdentifier());
        
        return new ResponseEntity<>(assetLinks, HttpStatus.OK);
    }

    public ResponseEntity<List<SpecificAssetId>> postAllAssetLinksById(@Parameter(in = ParameterIn.PATH, description = "The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded)", required=true, schema=@Schema()) @PathVariable("aasIdentifier") Base64UrlEncodedIdentifier aasIdentifier,@Parameter(in = ParameterIn.DEFAULT, description = "A list of specific Asset identifiers", required=true, schema=@Schema()) @Valid @RequestBody List<SpecificAssetId> body) {
        List<SpecificAssetId> assetIDs = aasDiscoveryService.createAllAssetLinksById(aasIdentifier.getIdentifier(), body);
        
        return new ResponseEntity<List<SpecificAssetId>>(assetIDs, HttpStatus.CREATED);
    }

	private List<AssetLink> getDecodedAssetLinks(List<Base64UrlEncodedIdentifier> assetIds) throws JsonProcessingException {
		List<AssetLink> result = new ArrayList<>();

		if (assetIds == null)
			return result;

		for (Base64UrlEncodedIdentifier base64UrlEncodedIdentifier : assetIds) {

			var decodedString = base64UrlEncodedIdentifier.getIdentifier();
				result.add(objectMapper.readValue(decodedString, AssetLink.class));

		}
		
		return result;
	}

}
