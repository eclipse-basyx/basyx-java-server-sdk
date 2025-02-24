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
package org.eclipse.digitaltwin.basyx.submodelservice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCreateAndRemoveUnderPath {
	
	private Submodel submodel;
	private SubmodelService service;

	@Before
	public void init() throws DeserializationException, IOException {
		submodel = loadSubmodel();
		service = new CrudSubmodelServiceFactory(new InMemorySubmodelBackend(), new InMemoryFileRepository()).create(submodel);
	}

	@Test
	public void testDeleteEntity() {
		service.deleteSubmodelElement("E1.P1");
		Entity entity = (Entity) service.getSubmodelElement("E1");
		Assert.assertTrue(entity.getStatements().isEmpty());
	}

	@Test
	public void testDeleteEntityUnderList() {
		service.deleteSubmodelElement("C2.L1[1][0]");
		SubmodelElementList list = (SubmodelElementList) service.getSubmodelElement("C2.L1[1]");
		Assert.assertEquals(1, list.getValue().size());
		Assert.assertEquals("4", ((Property) list.getValue().get(0)).getValue());
	}

	@Test
	public void testCreateProperty() {
		service.createSubmodelElement("C2.L1[1][0]", new DefaultProperty.Builder().idShort("P77").value("77").valueType(DataTypeDefXsd.STRING).build());
		Entity entity = (Entity) service.getSubmodelElement("C2.L1[1][0]");
		Assert.assertEquals(2, entity.getStatements().size());
		Assert.assertEquals("77", ((Property) entity.getStatements().get(1)).getValue());
	}

	private Submodel loadSubmodel() throws DeserializationException, IOException {
		try (InputStream in = TestCreateAndRemoveUnderPath.class.getResourceAsStream("submodel.json"); BufferedInputStream bIn = new BufferedInputStream(in)) {
			JsonDeserializer deserializer = new JsonDeserializer();
			return deserializer.read(bIn, Submodel.class);
		}
	}
}