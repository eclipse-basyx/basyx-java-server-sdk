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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-05-08T12:36:05.278579031Z[GMT]")
@RestController
public class SerializationApiController implements AASEnvironmentHTTPApi {

	private static final String ACCEPT_JSON = "application/json";
	private static final String ACCEPT_XML = "application/xml";
	private static final String ACCEPT_AASX = "application/asset-administration-shell-package+xml";

	private final HttpServletRequest request;

	private final AasEnvironmentSerialization aasEnvironment;

	@Autowired
	public SerializationApiController(HttpServletRequest request, AasEnvironmentSerialization aasEnvironment) {
		this.request = request;
		this.aasEnvironment = aasEnvironment;
	}

	@Override
	public ResponseEntity<Resource> generateSerializationByIds(
			@Parameter(in = ParameterIn.QUERY, description = "The Asset Administration Shells' unique ids (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "aasIds", required = false) List<String> aasIds,
			@Parameter(in = ParameterIn.QUERY, description = "The Submodels' unique ids (UTF8-BASE64-URL-encoded)", schema = @Schema()) @Valid @RequestParam(value = "submodelIds", required = false) List<String> submodelIds,
			@Parameter(in = ParameterIn.QUERY, description = "Include Concept Descriptions?", schema = @Schema(defaultValue = "true")) @Valid @RequestParam(value = "includeConceptDescriptions", required = false, defaultValue = "true") Boolean includeConceptDescriptions) {
		String accept = request.getHeader("Accept");

		if (!areParametersValid(accept, aasIds, submodelIds)) {
			return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
		}

		try {
			if (accept.equals(ACCEPT_AASX)) {
				byte[] serialization = aasEnvironment.createAASXAASEnvironmentSerialization(getOriginalIds(aasIds), getOriginalIds(submodelIds), includeConceptDescriptions);
				return new ResponseEntity<Resource>(new ByteArrayResource(serialization), HttpStatus.OK);
			}

			if (accept.equals(ACCEPT_XML)) {
				String serialization = aasEnvironment.createXMLAASEnvironmentSerialization(getOriginalIds(aasIds), getOriginalIds(submodelIds), includeConceptDescriptions);
				return new ResponseEntity<Resource>(new ByteArrayResource(serialization.getBytes()), HttpStatus.OK);
			}

			String serialization = aasEnvironment.createJSONAASEnvironmentSerialization(getOriginalIds(aasIds), getOriginalIds(submodelIds), includeConceptDescriptions);
			return new ResponseEntity<Resource>(new ByteArrayResource(serialization.getBytes()), HttpStatus.OK);
		} catch (ElementDoesNotExistException e) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		} catch (SerializationException | IOException e) {
			return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<String> getOriginalIds(List<String> ids) {
		List<String> results = new ArrayList<>();
		ids.forEach(id -> {
			results.add(Base64UrlEncodedIdentifier.fromEncodedValue(id).getIdentifier());
		});
		return results;
	}

	private boolean areParametersValid(String accept, @Valid List<String> aasIds, @Valid List<String> submodelIds) {
		if (aasIds.isEmpty() || submodelIds.isEmpty()) {
			return false;
		}
		return (accept.equals(ACCEPT_AASX) || accept.equals(ACCEPT_JSON) || accept.equals(ACCEPT_XML));
	}
}
