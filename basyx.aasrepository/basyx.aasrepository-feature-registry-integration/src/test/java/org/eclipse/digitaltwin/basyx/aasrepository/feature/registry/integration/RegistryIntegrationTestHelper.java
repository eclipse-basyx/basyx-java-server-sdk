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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultExtension;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Key;

/**
 * A helper class for testing RegistryIntegration feature
 * 
 * @author danish
 */
public class RegistryIntegrationTestHelper {

	// LangStringTextType AAS4J
	private static final LangStringTextType AAS4J_LANG_STRING_TEXT_TYPE_1 = new DefaultLangStringTextType.Builder().language("de").text("Ein Beispiel").build();
	private static final LangStringTextType AAS_LANG_STRING_TEXT_TYPE_2 = new DefaultLangStringTextType.Builder().language("en").text("An Example").build();

	// LangStringTextType AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType AAS_REG_LANG_STRING_TEXT_TYPE_1 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("de")
			.text("Ein Beispiel");
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType AAS_REG_LANG_STRING_TEXT_TYPE_2 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType().language("en")
			.text("An Example");

	// LangStringNameType AAS4J
	private static final LangStringNameType AAS4J_LANG_STRING_NAME_TYPE_1 = new DefaultLangStringNameType.Builder().language("en").text("Name type string").build();
	private static final LangStringNameType AAS4J_LANG_STRING_NAME_TYPE_2 = new DefaultLangStringNameType.Builder().language("de").text("Namenstypzeichenfolge").build();

	// LangStringNameType AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType AAS_REG_LANG_STRING_NAME_TYPE_1 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType().language("en")
			.text("Name type string");
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType AAS_REG_LANG_STRING_NAME_TYPE_2 = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType().language("de")
			.text("Namenstypzeichenfolge");

	// AssetKind AAS4J
	public static final AssetKind AAS4J_ASSET_KIND = AssetKind.INSTANCE;

	// AssetKind AasRegistry
	public static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind AASREG_ASSET_KIND = org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind.INSTANCE;

	// Administration AAS4J
	private static final Reference AAS4J_DATASPECIFICATION = new DefaultReference.Builder().keys(new DefaultKey.Builder().type(KeyTypes.BLOB).value("BlobValue").build()).type(ReferenceTypes.EXTERNAL_REFERENCE).build();
	private static final EmbeddedDataSpecification AAS4JEMB_EMBEDDED_DATA_SPECIFICATION = new DefaultEmbeddedDataSpecification.Builder().dataSpecification(AAS4J_DATASPECIFICATION).build();

	// Administration AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference AAS_REG_DATASPECIFICATION = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Reference()
			.keys(Arrays.asList(new Key().type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes.BLOB).value("BlobValue"))).type(org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes.EXTERNALREFERENCE);
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.EmbeddedDataSpecification AAS_REG_EMBEDDED_DATA_SPECIFICATION = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.EmbeddedDataSpecification()
			.dataSpecification(AAS_REG_DATASPECIFICATION);
	
	private static final String VERSION = "1.0.0";
	private static final String REVISION = "3";
	private static final String TEMPLATE_ID = "ID2.0";

	// Extension AAS4J
	private static final Extension AAS4J_EXTENSION = new DefaultExtension.Builder().semanticId(AAS4J_DATASPECIFICATION).name("extension").valueType(org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd.STRING).value("extensionValue").build();

	// Extension AasRegistry
	private static final org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension AAS_REG_EXTENSION = new org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension().semanticId(AAS_REG_DATASPECIFICATION).name("extension")
			.valueType(DataTypeDefXsd.STRING).value("extensionValue");

	public static List<LangStringTextType> getAas4jLangStringTextTypes() {
		return Arrays.asList(AAS4J_LANG_STRING_TEXT_TYPE_1, AAS_LANG_STRING_TEXT_TYPE_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType> getAasRegLangStringTextTypes() {
		return Arrays.asList(AAS_REG_LANG_STRING_TEXT_TYPE_1, AAS_REG_LANG_STRING_TEXT_TYPE_2);
	}

	public static AdministrativeInformation getAas4jAdministration() {
		return new DefaultAdministrativeInformation.Builder().embeddedDataSpecifications(AAS4JEMB_EMBEDDED_DATA_SPECIFICATION).version(VERSION).revision(REVISION).templateId(TEMPLATE_ID).build();
	}

	public static org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation getAasRegAdministration() {
		return new org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation().embeddedDataSpecifications(Arrays.asList(AAS_REG_EMBEDDED_DATA_SPECIFICATION)).version(VERSION).revision(REVISION)
				.templateId(TEMPLATE_ID);
	}

	public static List<LangStringNameType> getAas4jLangStringNameTypes() {
		return Arrays.asList(AAS4J_LANG_STRING_NAME_TYPE_1, AAS4J_LANG_STRING_NAME_TYPE_2);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType> getAasRegLangStringNameTypes() {
		return Arrays.asList(AAS_REG_LANG_STRING_NAME_TYPE_1, AAS_REG_LANG_STRING_NAME_TYPE_2);
	}

	public static List<Extension> getAas4jExtensions() {
		return Arrays.asList(AAS4J_EXTENSION);
	}

	public static List<org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension> getAasRegExtensions() {
		return Arrays.asList(AAS_REG_EXTENSION);
	}

}
