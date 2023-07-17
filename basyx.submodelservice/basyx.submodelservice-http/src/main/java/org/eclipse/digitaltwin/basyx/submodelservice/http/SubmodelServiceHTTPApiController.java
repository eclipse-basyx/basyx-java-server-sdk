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
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-07-16T14:12:09.075410867Z[GMT]")
@RestController
public class SubmodelServiceHTTPApiController implements SubmodelServiceHTTPApi {

	private static final Logger log = LoggerFactory.getLogger(SubmodelServiceHTTPApiController.class);

	private final SubmodelService service;

	@Autowired
	public SubmodelServiceHTTPApiController(SubmodelService service) {
		this.service = service;
	}

	public ResponseEntity<Result> deleteSubmodelElementByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath) {
		service.deleteSubmodelElement(seIdShortPath);
		return new ResponseEntity<Result>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<Submodel> getSubmodel() {
		Submodel submodel = service.getSubmodel();
		return new ResponseEntity<Submodel>(submodel, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelElement> getSubmodelElementByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath) {
		SubmodelElement result = service.getSubmodelElement(seIdShortPath);
		return new ResponseEntity<SubmodelElement>(result, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelElementValue> getSubmodelElementValueByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath) {
		SubmodelElementValue result = service.getSubmodelElementValue(seIdShortPath);
		return new ResponseEntity<SubmodelElementValue>(result, HttpStatus.OK);
	}

	public ResponseEntity<List<SubmodelElement>> getSubmodelElements() {
		Submodel submodel = service.getSubmodel();
		List<SubmodelElement> submodelElementList = submodel.getSubmodelElements();
		return new ResponseEntity<List<SubmodelElement>>(submodelElementList, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelValueOnly> getSubmodelValues() {
		Submodel submodel = service.getSubmodel();
		SubmodelValueOnly values = new SubmodelValueOnly(submodel.getSubmodelElements());
		return new ResponseEntity<SubmodelValueOnly>(values, HttpStatus.OK);
	}

	public ResponseEntity<SubmodelElement> putSubmodelElement(@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The Submodel-Element object", schema = @Schema()) @Valid @RequestBody SubmodelElement body) {
		if (doesSubmodelElementExist(seIdShortPath)) {
			service.deleteSubmodelElement(seIdShortPath);
		}
		service.createSubmodelElement(body);
		return new ResponseEntity<SubmodelElement>(body, HttpStatus.CREATED);
	}

	private boolean doesSubmodelElementExist(String seIdShortPath) {
		return service.getSubmodelElements()
				.stream()
				.anyMatch(sme -> {
					return sme.getIdShort()
							.equals(seIdShortPath);
				});
	}

	public ResponseEntity<SubmodelElementValue> putSubmodelElementValueByIdShort(
			@Parameter(in = ParameterIn.PATH, description = "The Submodel-Element's IdShort-Path", required = true, schema = @Schema()) @PathVariable("seIdShortPath") String seIdShortPath,
			@Parameter(in = ParameterIn.DEFAULT, description = "The new value", schema = @Schema()) @Valid @RequestBody SubmodelElementValue body) {
		service.setSubmodelElementValue(seIdShortPath, body);
		return new ResponseEntity<SubmodelElementValue>(HttpStatus.NO_CONTENT);
	}

}
