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

package org.eclipse.digitaltwin.basyx.submodelservice.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Provides the spring bean configuration for the {@link SubmodelService}
 * utilizing all found features for the respective services
 * 
 * @author fried
 *
 */
@Configuration
public class SubmodelServiceConfiguration {
	@Primary
	@Bean
	@Autowired
	public SubmodelService getSubmodelService(Submodel submodel) {
		return getSubmodelServiceFactory().create(submodel);
	}

	@Bean
	static SubmodelServiceFactory getSubmodelServiceFactory() {
		return new InMemorySubmodelServiceFactory();
	}

	@Bean
	public static Submodel getSubmodel() {
		List<LangStringTextType> description = new ArrayList<LangStringTextType>();
		description.add(new DefaultLangStringTextType.Builder().language("de-DE")
				.text("Test")
				.build());
		List<LangStringNameType> displayName = new ArrayList<LangStringNameType>();
		displayName.add(new DefaultLangStringNameType.Builder().language("de-DE")
				.text("Test")
				.build());
		List<Key> refKeys = new ArrayList<Key>();
		refKeys.add(new DefaultKey.Builder().value("123")
				.build());

		List<SubmodelElement> smeList = new ArrayList<>();
		SubmodelElement sme1 = new DefaultProperty.Builder().value("test")
				.idShort("test")
				.build();
		smeList.add(sme1);

		Submodel submodel = new DefaultSubmodel.Builder().category("TestCategory")
				.description(description)
				.displayName(displayName)
				.id("TestID")
				.idShort("test")
				.kind(ModellingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder().keys(refKeys)
						.build())
				.submodelElements(smeList)
				.build();

		return submodel;
	}
}
