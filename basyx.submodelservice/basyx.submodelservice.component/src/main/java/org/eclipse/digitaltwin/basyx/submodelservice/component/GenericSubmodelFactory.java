/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author gerhard sonnenberg (DFKI GmbH)
 */
@Component
public class GenericSubmodelFactory  {
	
	private final String filePath;
	
	@Autowired
	public GenericSubmodelFactory(@Value("${basyx.submodel.file}") String filePath) {
		this.filePath = filePath;
		if (StringUtils.isEmpty(filePath)) {
			throw new IllegalStateException("Application property 'basyx.submodel.file' not set.");
		}
	} 
	
	public Submodel create() {
		return loadSubmodel();
	}
	
	public Submodel loadSubmodel() {
		File submodelFile = new File(filePath);
		try ( FileInputStream fIn = new FileInputStream(submodelFile); BufferedInputStream bIn = new BufferedInputStream(fIn)) {
			JsonDeserializer deserializer = new JsonDeserializer();
			deserializer.useImplementation(Operation.class, InvokableOperation.class);
			return deserializer.read(bIn, DefaultSubmodel.class);
		} catch (IOException | DeserializationException e) {
			throw new IllegalStateException("Could not load submodel: " + submodelFile.getAbsolutePath(), e);
		}
	}
}