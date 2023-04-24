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
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

/**
 * Test helper class for {@link ConceptDescriptionRepositorySuite}
 * 
 * @author danish
 *
 */
public class ConceptDescriptionRepositorySuiteHelper {

	//CONCEPT_DESCRIPTION
	public static final String CONCEPT_DESCRIPTION_ID_SHORT = "ConceptDescription";
	public static final String CONCEPT_DESCRIPTION_ID = "7A7104BDAB57E184";
	
	public static final String CD_REVISION = "2";
	public static final String CD_VERSION = "1.2";

	public final List<Key> CD_FIRST_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.DATA_ELEMENT).value("DataElement").build());
	public final List<Key> CD_SECOND_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.BASIC_EVENT_ELEMENT).value("BasicEventElement").build());
	public final Reference CD_FIRST_REFERENCE = new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE)
			.keys(CD_FIRST_KEYS).build();
	public final Reference CD_SECOND_REFERENCE = new DefaultReference.Builder()
			.type(ReferenceTypes.EXTERNAL_REFERENCE).keys(CD_SECOND_KEYS).build();

	public final AdministrativeInformation CD_ADMINISTRATIVE_INFORMATION = new DefaultAdministrativeInformation.Builder()
			.revision(CD_REVISION).version(CD_VERSION).build();

	public final List<LangStringTextType> CD_DESCRIPTIONS = new ArrayList<>(
			Arrays.asList(new DefaultLangStringTextType.Builder().text("Hello").language("en").build(), new DefaultLangStringTextType.Builder().text("Hallo").language("de").build()));

	public final List<LangStringNameType> CD_DISPLAY_NAME = new ArrayList<>(Arrays.asList(
			new DefaultLangStringNameType.Builder().text("Concept Description").language("en").build(), new DefaultLangStringNameType.Builder().text("Konzeptbeschreibung").language("de").build()));
	
	//BASIC_CONCEPT_DESCRIPTION
	public static final String BASIC_CONCEPT_DESCRIPTION_ID_SHORT = "BasicConceptDescription";
	public static final String BASIC_CONCEPT_DESCRIPTION_ID = "7A7104BDAH56TH2";
	
	public final List<Key> BCD_FIRST_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.CONCEPT_DESCRIPTION).value("ConceptDescriptionKey").build());
	public final List<Key> BCD_SECOND_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.CAPABILITY).value("CapabilityKey").build());
	public final Reference BCD_FIRST_REFERENCE = new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
			.keys(BCD_FIRST_KEYS).build();
	public final Reference BCD_SECOND_REFERENCE = new DefaultReference.Builder()
			.type(ReferenceTypes.MODEL_REFERENCE).keys(BCD_SECOND_KEYS).build();
	
	//BASIC_CONCEPT_DESCRIPTION_WITH_COMMON_ISCASEOF
	public static final String BASIC_CONCEPT_DESCRIPTION_COMMON_IS_CASEOF_ID_SHORT = "BasicConDescCommonIsCaseOf";
	public static final String BASIC_CONCEPT_DESCRIPTION_COMMON_IS_CASEOF_ID = "7A7104BDA6544322";
	
	public final List<Key> NOT_COMMON_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.ENTITY).value("EntityKey").build());
	public final Reference NOT_COMMON_REFERENCE = new DefaultReference.Builder()
			.type(ReferenceTypes.EXTERNAL_REFERENCE).keys(NOT_COMMON_KEYS).build();
	
	//CONCEPT_DESCRIPTION_WITH_DATA_SPECIFICATION
	public static final String CONCEPT_DESCRIPTION_WITH_DS_ID_SHORT = "ConDescWithDataSpec";
	public static final String CONCEPT_DESCRIPTION_WITH_DS_ID = "7A7104IHTREFN4322";
	
	public final List<Key> CD_WITH_DS_KEYS = Arrays
			.asList(new DefaultKey.Builder().type(KeyTypes.REFERENCE_ELEMENT).value("ReferenceElementKey").build());
	public final Reference CD_WITH_DS_REFERENCE = new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
			.keys(CD_WITH_DS_KEYS).build();
	
	public final EmbeddedDataSpecification embeddedDataSpecification = new DefaultEmbeddedDataSpecification.Builder().dataSpecification(CD_WITH_DS_REFERENCE).build();
		
}
