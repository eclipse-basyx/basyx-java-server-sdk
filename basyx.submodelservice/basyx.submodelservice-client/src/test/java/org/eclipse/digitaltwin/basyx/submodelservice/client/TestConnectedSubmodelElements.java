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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EntityType;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBasicEventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultMultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRange;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.DummySubmodelRepositoryComponent;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedBasicEventElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedBlob;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedEntity;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedFile;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedMultiLanguageProperty;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedProperty;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedRange;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedReferenceElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedRelationshipElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedSubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements.ConnectedSubmodelElementList;
import org.eclipse.digitaltwin.basyx.submodelservice.value.AnnotatedRelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.BasicEventValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.AnnotatedRelationshipElementValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.BasicEventValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.BlobValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.RelationshipElementValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.SubmodelElementCollectionValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.SubmodelElementListValueMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class TestConnectedSubmodelElements {

	private static final String SUBMODEL_ELEMENT_ID_SHORT = "ExampleIdShort";
	private static final String EXPECTED_STRING = "This is a test";
	private static final String SUBMODEL_ID_SHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final String ACCESS_URL = "http://localhost:8081/submodels/";
	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startSubmodelService() throws Exception {
		appContext = new SpringApplicationBuilder(DummySubmodelRepositoryComponent.class).profiles("httptests").run(new String[] {});
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
		ConnectedBlob blob = getConnectedBlob(getDefaultBlob());
		assertEquals(EXPECTED_STRING, blob.getValue().getValue());
	}

	@Test
	public void setBlobValue() {
		ConnectedBlob blob = getConnectedBlob(getDefaultBlob());
		String newValue = "This is a new test";
		blob.setValue(new FileBlobValue("text/plain", newValue));
		String actual = blob.getValue().getValue();
		assertEquals(newValue, actual);
	}

	@Test
	public void getBlob() {
		Blob expected = getDefaultBlob();
		ConnectedBlob blob = getConnectedBlob(expected);
		BlobValueMapper valueMapperExpected = new BlobValueMapper(expected);
		BlobValueMapper valueMapper = new BlobValueMapper(blob.getSubmodelElement());
		assertEquals(valueMapperExpected.getValue().getValue(), valueMapper.getValue().getValue());
	}

	@Test
	public void getPropertyValue(){
		ConnectedProperty property = getConnectedProperty(getDefaultProperty());
		assertEquals(EXPECTED_STRING, property.getValue().getValue());
	}

	@Test
	public void setPropertyValue(){
		ConnectedProperty property = getConnectedProperty(getDefaultProperty());
		String newValue = "This is a new test";
		property.setValue(new PropertyValue(newValue));
		String actual = property.getValue().getValue();
		assertEquals(newValue, actual);
	}

	@Test
	public void getProperty(){
		Property expected = getDefaultProperty();
		ConnectedProperty property = getConnectedProperty(expected);
		assertEquals(expected.getValue(), property.getSubmodelElement().getValue());
	}
	@Test
	public void getFileValue(){
		ConnectedFile file = getConnectedFile(getDefaultFile());
		assertEquals(EXPECTED_STRING, file.getValue().getValue());
	}

	@Test
	public void setFileValue(){
		ConnectedFile file = getConnectedFile(getDefaultFile());
		String newValue = "This is a new test";
		file.setValue(new FileBlobValue("plain/text",newValue));
		String actual = file.getValue().getValue();
		assertEquals(newValue, actual);
	}

	@Test
	public void getFile(){
		File expected = getDefaultFile();
		ConnectedFile file = getConnectedFile(expected);
		assertEquals(expected.getValue(), file.getSubmodelElement().getValue());
	}

	@Test
	public void getEntityValue(){
		ConnectedEntity entity = getConnectedEntity(getDefaultEntity());
		assertEquals(EXPECTED_STRING, entity.getValue().getGlobalAssetId());
	}

	@Test
	public void setEntityValue(){
		Entity entity = getDefaultEntity();
		ConnectedEntity connectedEntity = getConnectedEntity(entity);
		EntityValue newValue = new EntityValue(new ArrayList<>(), EntityType.SELF_MANAGED_ENTITY, "New Value", new ArrayList<>());
		connectedEntity.setValue(newValue);
		EntityValue actual = connectedEntity.getValue();
		assertEquals(newValue.getGlobalAssetId(), actual.getGlobalAssetId());
	}

	@Test
	public void getEntity(){
		Entity expected = getDefaultEntity();
		ConnectedEntity entity = getConnectedEntity(expected);
		assertEquals(expected.getGlobalAssetId(), entity.getSubmodelElement().getGlobalAssetId());
	}

	@Test
	public void getRelationshipElementValue() {
		ConnectedRelationshipElement relationshipElement = getConnectedRelationshipElement(getDefaultRelationshipElement());
		assertEquals(EXPECTED_STRING, relationshipElement.getValue().getFirst().getKeys().get(0).getValue());
	}

	@Test
	public void setRelationshipElementValue() {
		RelationshipElement relationshipElement = getDefaultRelationshipElement();
		ConnectedRelationshipElement connectedRelationshipElement = getConnectedRelationshipElement(relationshipElement);
		RelationshipElementValueMapper valueMapper = new RelationshipElementValueMapper(relationshipElement);
		valueMapper.setValue(new RelationshipElementValue(createReferenceValue(getReference("newKey1")), createReferenceValue(getReference("newKey2"))));
		connectedRelationshipElement.setValue(valueMapper.getValue());
		RelationshipElementValue actual = connectedRelationshipElement.getValue();
		assertEquals("newKey1", actual.getFirst().getKeys().get(0).getValue());
		assertEquals("newKey2", actual.getSecond().getKeys().get(0).getValue());
	}

	@Test
	public void getRelationshipElement() {
		RelationshipElement expected = getDefaultRelationshipElement();
		ConnectedRelationshipElement relationshipElement = getConnectedRelationshipElement(expected);
		assertEquals(expected.getFirst().getKeys().get(0).getValue(), relationshipElement.getSubmodelElement().getFirst().getKeys().get(0).getValue());
	}
	
	@Test
	public void getAnnotatedRelationshipElementValue() {
		ConnectedAnnotatedRelationshipElement annotatedRelationshipElement = getConnectedAnnotatedRelationshipElement(getDefaultAnnotatedRelationshipElement());
		assertEquals(EXPECTED_STRING, annotatedRelationshipElement.getValue().getFirst().getKeys().get(0).getValue());
	}

	@Test
	public void setAnnotatedRelationshipElementValue() {
		AnnotatedRelationshipElement annotatedRelationshipElement = getDefaultAnnotatedRelationshipElement();
		ConnectedAnnotatedRelationshipElement connectedAnnotatedRelationshipElement = getConnectedAnnotatedRelationshipElement(annotatedRelationshipElement);
		AnnotatedRelationshipElementValueMapper valueMapper = new AnnotatedRelationshipElementValueMapper(annotatedRelationshipElement);
		valueMapper.setValue(new AnnotatedRelationshipElementValue(createReferenceValue(getReference("newKey1")), createReferenceValue(getReference("newKey2")), new ArrayList<>()));
		connectedAnnotatedRelationshipElement.setValue(valueMapper.getValue());
		AnnotatedRelationshipElementValue actual = connectedAnnotatedRelationshipElement.getValue();
		assertEquals("newKey1", actual.getFirst().getKeys().get(0).getValue());
		assertEquals("newKey2", actual.getSecond().getKeys().get(0).getValue());
	}

	@Test
	public void getAnnotatedRelationshipElement() {
		AnnotatedRelationshipElement expected = getDefaultAnnotatedRelationshipElement();
		ConnectedAnnotatedRelationshipElement annotatedRelationshipElement = getConnectedAnnotatedRelationshipElement(expected);
		assertEquals(expected.getFirst().getKeys().get(0).getValue(), annotatedRelationshipElement.getSubmodelElement().getFirst().getKeys().get(0).getValue());
	}

	@Test
	public void getBasicEventElementValue() {
		ConnectedBasicEventElement basicEventElement = getConnectedBasicEventElement(getDefaultBasicEventElement());
		assertEquals(EXPECTED_STRING, basicEventElement.getValue().getObserved().getKeys().get(0).getValue());
	}

	@Test
	public void setBasicEventElementValue() {
		DefaultBasicEventElement basicEventElement = getDefaultBasicEventElement();
		ConnectedBasicEventElement connectedBasicEventElement = getConnectedBasicEventElement(basicEventElement);
		BasicEventValue newValue = new BasicEventValue(createReferenceValue(getReference("newValue")));
		connectedBasicEventElement.setValue(newValue);
		BasicEventValueMapper valueMapper = new BasicEventValueMapper(basicEventElement);
		String actual = valueMapper.getValue().getObserved().getKeys().get(0).getValue();
		assertEquals("newValue", actual);
	}

	@Test
	public void getBasicEventElement() {
		DefaultBasicEventElement expected = getDefaultBasicEventElement();
		ConnectedBasicEventElement basicEventElement = getConnectedBasicEventElement(expected);
		assertEquals(expected.getObserved().getKeys().get(0).getValue(), basicEventElement.getSubmodelElement().getObserved().getKeys().get(0).getValue());
	}

	@Test
	public void getMultiLanguagePropertyValue() {
		ConnectedMultiLanguageProperty multiLanguageProperty = getConnectedMultiLanguageProperty(getDefaultMultiLanguageProperty());
		assertEquals(EXPECTED_STRING, multiLanguageProperty.getValue().getValue().get(0).getText());
	}

	@Test
	public void setMultiLanguagePropertyValue() {
		DefaultMultiLanguageProperty multiLanguageProperty = getDefaultMultiLanguageProperty();
		ConnectedMultiLanguageProperty connectedMultiLanguageProperty = getConnectedMultiLanguageProperty(multiLanguageProperty);
		DefaultLangStringTextType newValue = new DefaultLangStringTextType.Builder().text("newText").language("de").build();
		multiLanguageProperty.setValue(Arrays.asList(newValue));
		String actual = connectedMultiLanguageProperty.getValue().getValue().get(0).getText();
		assertEquals(newValue.getText(), actual);
	}

	@Test
	public void getMultiLanguageProperty() {
		DefaultMultiLanguageProperty expected = getDefaultMultiLanguageProperty();
		ConnectedMultiLanguageProperty multiLanguageProperty = getConnectedMultiLanguageProperty(expected);
		assertEquals(expected.getValue().get(0).getText(), multiLanguageProperty.getSubmodelElement().getValue().get(0).getText());
	}

	@Test
	public void getRangeValue() {
		ConnectedRange range = getConnectedRange(getDefaultRange());
		assertEquals(0, range.getValue().getMin());
		assertEquals(10, range.getValue().getMax());
	}

	@Test
	public void setRangeValue() {
		DefaultRange range = getDefaultRange();
		ConnectedRange connectedRange = getConnectedRange(range);
		RangeValue newValue = new RangeValue(1, 11);
		connectedRange.setValue(newValue);
		RangeValue actual = connectedRange.getValue();
		assertEquals(1, actual.getMin());
		assertEquals(11, actual.getMax());
	}

	@Test
	public void getRange() {
		DefaultRange expected = getDefaultRange();
		ConnectedRange range = getConnectedRange(expected);
		assertEquals(expected.getMin(), range.getSubmodelElement().getMin());
		assertEquals(expected.getMax(), range.getSubmodelElement().getMax());
	}

	@Test
	public void getReferenceValue() {
		ConnectedReferenceElement reference = getConnectedReference(getDefaultReferenceElement());
		assertEquals(EXPECTED_STRING, reference.getValue().getReferenceValue().getKeys().get(0).getValue());
	}

	@Test
	public void setReferenceValue() {
		DefaultReferenceElement referenceElement = getDefaultReferenceElement();
		ConnectedReferenceElement connectedReferenceElement = getConnectedReference(referenceElement);
		ReferenceElementValue newValue = new ReferenceElementValue(createReferenceValue(getReference("newKey")));
		connectedReferenceElement.setValue(newValue);
		ReferenceElementValue actual = connectedReferenceElement.getValue();
		assertEquals("newKey", actual.getReferenceValue().getKeys().get(0).getValue());
	}

	@Test
	public void getReferenceElement() {
		DefaultReferenceElement expected = getDefaultReferenceElement();
		ConnectedReferenceElement reference = getConnectedReference(expected);
		assertEquals(expected.getValue().getKeys().get(0).getValue(), reference.getSubmodelElement().getValue().getKeys().get(0).getValue());
	}

	@Test
	public void getSubmodelElementCollectionValue() {
		ConnectedSubmodelElementCollection submodelElementCollection = getConnectedSubmodelElementCollection(getDefaultSubmodelElementCollection());
		assertEquals(1, submodelElementCollection.getSubmodelElement().getValue().size());
	}

	@Test
	public void setSubmodelElementCollectionValue() {
		DefaultSubmodelElementCollection submodelElementCollection = getDefaultSubmodelElementCollection();
		ConnectedSubmodelElementCollection connectedSubmodelElementCollection = getConnectedSubmodelElementCollection(submodelElementCollection);
		submodelElementCollection.getValue().add(getDefaultMultiLanguageProperty("newMLP"));
		SubmodelElementCollectionValueMapper valueMapper = new SubmodelElementCollectionValueMapper(submodelElementCollection);
		connectedSubmodelElementCollection.setValue(valueMapper.getValue());
		assertEquals(2, connectedSubmodelElementCollection.getValue().getValue().size());
	}

	@Test
	public void getSubmodelElementCollection() {
		DefaultSubmodelElementCollection expected = getDefaultSubmodelElementCollection();
		ConnectedSubmodelElementCollection submodelElementCollection = getConnectedSubmodelElementCollection(expected);
		assertEquals(expected.getValue().size(), submodelElementCollection.getSubmodelElement().getValue().size());
	}

	@Test
	public void getSubmodelElementListValue() {
		ConnectedSubmodelElementList submodelElementList = getConnectedSubmodelElementList(getDefaultSubmodelElementList());
		assertEquals(1, submodelElementList.getValue().getSubmodelElementValues().size());
	}

	@Test
	public void setSubmodelElementListValue() {
		DefaultSubmodelElementList submodelElementList = getDefaultSubmodelElementList();
		ConnectedSubmodelElementList connectedSubmodelElementList = getConnectedSubmodelElementList(submodelElementList);
		submodelElementList.getValue().add(getDefaultProperty());
		SubmodelElementListValueMapper valueMapper = new SubmodelElementListValueMapper(submodelElementList);
		connectedSubmodelElementList.setValue(valueMapper.getValue());
		assertEquals(2, connectedSubmodelElementList.getValue().getSubmodelElementValues().size());
	}

	@Test
	public void getSubmodelElementList() {
		DefaultSubmodelElementList expected = getDefaultSubmodelElementList();
		ConnectedSubmodelElementList submodelElementList = getConnectedSubmodelElementList(expected);
		assertEquals(expected.getValue().size(), submodelElementList.getSubmodelElement().getValue().size());
	}

	private DefaultSubmodelElementList getDefaultSubmodelElementList() {
		return new DefaultSubmodelElementList.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(getDefaultAnnotatedRelationshipElement()).build();
	}

	private ConnectedSubmodelElementList getConnectedSubmodelElementList(DefaultSubmodelElementList submodelElementList) {
		Submodel sm = createSubmodel(submodelElementList);
		return new ConnectedSubmodelElementList(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private DefaultSubmodelElementCollection getDefaultSubmodelElementCollection() {
		return new DefaultSubmodelElementCollection.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(getDefaultMultiLanguageProperty()).build();
	}

	private ConnectedSubmodelElementCollection getConnectedSubmodelElementCollection(DefaultSubmodelElementCollection submodelElementCollection) {
		Submodel sm = createSubmodel(submodelElementCollection);
		return new ConnectedSubmodelElementCollection(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private DefaultReferenceElement getDefaultReferenceElement() {
		return new DefaultReferenceElement.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(getReference(EXPECTED_STRING)).build();
	}

	private ConnectedReferenceElement getConnectedReference(DefaultReferenceElement referenceElement) {
		Submodel sm = createSubmodel(referenceElement);
		return new ConnectedReferenceElement(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private DefaultRange getDefaultRange() {
		return new DefaultRange.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).min("0").max("10").build();
	}

	private ConnectedRange getConnectedRange(DefaultRange range) {
		Submodel sm = createSubmodel(range);
		return new ConnectedRange(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private DefaultMultiLanguageProperty getDefaultMultiLanguageProperty() {
		return new DefaultMultiLanguageProperty.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(Arrays.asList(new DefaultLangStringTextType.Builder().text(EXPECTED_STRING).language("de").build())).build();
	}

	private DefaultMultiLanguageProperty getDefaultMultiLanguageProperty(String idShort) {
		return new DefaultMultiLanguageProperty.Builder().idShort(idShort).value(Arrays.asList(new DefaultLangStringTextType.Builder().text(EXPECTED_STRING).language("de").build())).build();
	}

	private ConnectedMultiLanguageProperty getConnectedMultiLanguageProperty(DefaultMultiLanguageProperty property) {
		Submodel sm = createSubmodel(property);
		return new ConnectedMultiLanguageProperty(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private DefaultBasicEventElement getDefaultBasicEventElement() {
		return new DefaultBasicEventElement.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).observed(getReference(EXPECTED_STRING)).build();
	}

	private ConnectedBasicEventElement getConnectedBasicEventElement(DefaultBasicEventElement basicEventElement) {
		Submodel sm = createSubmodel(basicEventElement);
		return new ConnectedBasicEventElement(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private static DefaultAnnotatedRelationshipElement getDefaultAnnotatedRelationshipElement() {
		return new DefaultAnnotatedRelationshipElement.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).first(getReference(EXPECTED_STRING)).second(getReference(EXPECTED_STRING)).build();
	}

	private ConnectedAnnotatedRelationshipElement getConnectedAnnotatedRelationshipElement(AnnotatedRelationshipElement annotatedRelationshipElement) {
		Submodel sm = createSubmodel(annotatedRelationshipElement);
		return new ConnectedAnnotatedRelationshipElement(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private ReferenceValue createReferenceValue(Reference reference) {
		return new ReferenceValue(reference.getType(), reference.getKeys());
	}

	private static DefaultRelationshipElement getDefaultRelationshipElement() {
		return new DefaultRelationshipElement.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).first(getReference(EXPECTED_STRING)).second(getReference(EXPECTED_STRING)).build();
	}

	private static DefaultReference getReference(String keyValue) {
		return new DefaultReference.Builder().keys(new DefaultKey.Builder().value(keyValue).type(KeyTypes.PROPERTY).build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	}

	private ConnectedRelationshipElement getConnectedRelationshipElement(RelationshipElement relationshipElement) {
		Submodel sm = createSubmodel(relationshipElement);
		return new ConnectedRelationshipElement(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())),SUBMODEL_ELEMENT_ID_SHORT);
	}

	private static DefaultEntity getDefaultEntity(){
		return new DefaultEntity.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).globalAssetId(EXPECTED_STRING).build();
	}

	private ConnectedEntity getConnectedEntity(Entity entity) {
		Submodel sm = createSubmodel(entity);
		return new ConnectedEntity(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())),SUBMODEL_ELEMENT_ID_SHORT);
	}

	private static DefaultFile getDefaultFile() {
		return new DefaultFile.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(EXPECTED_STRING).contentType("text/plain").build();
	}

	private ConnectedFile getConnectedFile(File file) {
		Submodel sm = createSubmodel(file);
        return new ConnectedFile(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())),SUBMODEL_ELEMENT_ID_SHORT);
	}

	private static DefaultProperty getDefaultProperty() {
		return new DefaultProperty.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).value(EXPECTED_STRING).valueType(DataTypeDefXsd.STRING).build();
	}

	private ConnectedProperty getConnectedProperty(Property property) {
		Submodel sm = createSubmodel(property);
        return new ConnectedProperty(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())),SUBMODEL_ELEMENT_ID_SHORT);
	}

	private ConnectedBlob getConnectedBlob(Blob blob) {
		Submodel sm = createSubmodel(blob);
		return new ConnectedBlob(getSubmodelServiceUrl(Base64UrlEncodedIdentifier.encodeIdentifier(sm.getId())), SUBMODEL_ELEMENT_ID_SHORT);
	}

	private Blob getDefaultBlob() {
		Blob blob = new DefaultBlob.Builder().idShort(SUBMODEL_ELEMENT_ID_SHORT).build();
		BlobValueMapper valueMapper = new BlobValueMapper(blob);
		valueMapper.setValue(new FileBlobValue("text/plain", EXPECTED_STRING));
		return blob;
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
