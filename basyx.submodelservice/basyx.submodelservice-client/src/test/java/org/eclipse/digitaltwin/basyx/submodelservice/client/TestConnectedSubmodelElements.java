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


package org.eclipse.digitaltwin.basyx.submodelservice.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.DummySubmodelRepositoryComponent;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedBlob;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedProperty;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.junit.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;


public class TestConnectedSubmodelElements {

	private static final String SUBMODEL_ELEMENT_ID_SHORT = "ExampleIdShort";
	private static final String EXPECTED_STRING = "This is a test";
	private static final String SUBMODEL_ID_SHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final String ACCESS_URL = "http://localhost:8080/submodels/";
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startSubmodelService() throws Exception {
		appContext = new SpringApplication(DummySubmodelRepositoryComponent.class).run(new String[] {});
	}

	@AfterClass
	public static void shutdownSubmodelService() {
		appContext.close();
	}

	@After
	public void cleanUpService(){
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.deleteSubmodel(SUBMODEL_ID);
	}

	@Test
	public void getBlobValue() {
		byte[] expectedValue = EXPECTED_STRING.getBytes();
		ConnectedBlob blob = getConnectedBlob(expectedValue);
		assertTrue(new String(blob.getValue()).equals(EXPECTED_STRING));
	}

	@Test
	public void setBlobValue() {
		byte[] expectedValue = EXPECTED_STRING.getBytes();
		ConnectedBlob blob = getConnectedBlob(expectedValue);
		String newString = "This is a new test";
		byte[] newValue = newString.getBytes();
		blob.setValue(newValue);
        assertEquals(new String(blob.getValue()), newString);
	}

	@Test
	public void getBlobSubmodelElement() {
		byte[] expectedValue = EXPECTED_STRING.getBytes();
		ConnectedBlob blob = getConnectedBlob(expectedValue);
        assertEquals(EXPECTED_STRING, new String(blob.getSubmodelElement().getValue()));
	}

	@Test
	public void getPropertyValue(){
		ConnectedProperty property = getConnectedProperty();
		assertEquals(EXPECTED_STRING, property.getValue().getValue());
	}

	@Test
	public void setPropertyValue(){
		ConnectedProperty property = getConnectedProperty();
		String newValue = "This is a new test";
		property.setValue(new PropertyValue(newValue));
		String actual = property.getValue().getValue();
		assertEquals(newValue, actual);
	}

	@Test
	public void getProperty(){
		Property expected = getDefaultProperty();
		ConnectedProperty property = getConnectedProperty();
		assertEquals(property.getValue().getValue(),expected.getValue());
	}

	private static DefaultProperty getDefaultProperty() {
		return new DefaultProperty.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(EXPECTED_STRING).build();
	}

	private ConnectedProperty getConnectedProperty() {
		Submodel sm = createSubmodel(getDefaultProperty());
		ConnectedProperty property = new ConnectedProperty(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())),SUBMODEL_ELEMENT_ID_SHORT);
		return property;
	}

	private ConnectedBlob getConnectedBlob(byte[] expectedValue) {
		Submodel sm = createSubmodel(createBlobWithValue(expectedValue));
		ConnectedBlob blob = new ConnectedBlob(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
		return blob;
	}

	private DefaultBlob createBlobWithValue(byte[] expectedValue) {
		return new DefaultBlob.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).contentType("application/pdf").value(expectedValue).build();
	}

	private Submodel createSubmodel(SubmodelElement submodelElement) {
		Submodel submodel = new DefaultSubmodel.Builder().id(SUBMODEL_ID).idShort(SUBMODEL_ID_SHORT).submodelElements(submodelElement).build();
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.createSubmodel(submodel);
		String base64UrlEncodedId = Base64UrlEncodedIdentifier.encodeIdentifier(submodel.getId());
		new ConnectedSubmodelService(getSubmodelServiceUrl(base64UrlEncodedId));
		return submodel;
	}

	private String getSubmodelServiceUrl(String base64UrlEncodedId) {
		return ACCESS_URL + base64UrlEncodedId;
	}

}
