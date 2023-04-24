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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;

/**
 * Factory for creating ConceptDescriptions for tests
 * 
 * @author danish
 *
 */
public class DummyConceptDescriptionFactory {

	public static Collection<ConceptDescription> getConceptDescriptions() {
		return Arrays.asList(createConceptDescription(), createBasicConceptDescription(), createBasicConceptDescriptionHavingCommonIsCaseOf(), createBasicConceptDescriptionWithDataSpecification());
	}

	public static ConceptDescription createConceptDescription() {
		ConceptDescriptionRepositorySuiteHelper helper = new ConceptDescriptionRepositorySuiteHelper();

		return new DefaultConceptDescription.Builder().id(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID)
				.idShort(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_ID_SHORT)
				.isCaseOf(Arrays.asList(helper.CD_FIRST_REFERENCE, helper.CD_SECOND_REFERENCE))
				.administration(helper.CD_ADMINISTRATIVE_INFORMATION).description(helper.CD_DESCRIPTIONS)
				.displayName(helper.CD_DISPLAY_NAME).build();
	}

	public static ConceptDescription createBasicConceptDescription() {
		ConceptDescriptionRepositorySuiteHelper helper = new ConceptDescriptionRepositorySuiteHelper();

		return new DefaultConceptDescription.Builder().id(ConceptDescriptionRepositorySuiteHelper.BASIC_CONCEPT_DESCRIPTION_ID)
				.idShort(ConceptDescriptionRepositorySuiteHelper.BASIC_CONCEPT_DESCRIPTION_ID_SHORT)
				.isCaseOf(Arrays.asList(helper.BCD_FIRST_REFERENCE, helper.BCD_SECOND_REFERENCE)).build();
	}
	
	public static ConceptDescription createBasicConceptDescriptionHavingCommonIsCaseOf() {
		ConceptDescriptionRepositorySuiteHelper helper = new ConceptDescriptionRepositorySuiteHelper();
		
		return new DefaultConceptDescription.Builder().id(ConceptDescriptionRepositorySuiteHelper.BASIC_CONCEPT_DESCRIPTION_COMMON_IS_CASEOF_ID)
				.idShort(ConceptDescriptionRepositorySuiteHelper.BASIC_CONCEPT_DESCRIPTION_COMMON_IS_CASEOF_ID_SHORT)
				.isCaseOf(Arrays.asList(helper.NOT_COMMON_REFERENCE, helper.BCD_SECOND_REFERENCE)).build();
	}
	
	public static ConceptDescription createBasicConceptDescriptionWithDataSpecification() {
		ConceptDescriptionRepositorySuiteHelper helper = new ConceptDescriptionRepositorySuiteHelper();
		
		return new DefaultConceptDescription.Builder().id(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_WITH_DS_ID)
				.idShort(ConceptDescriptionRepositorySuiteHelper.CONCEPT_DESCRIPTION_WITH_DS_ID_SHORT).embeddedDataSpecifications(helper.embeddedDataSpecification)
				.isCaseOf(Arrays.asList(helper.NOT_COMMON_REFERENCE, helper.CD_FIRST_REFERENCE)).build();
	}

}
