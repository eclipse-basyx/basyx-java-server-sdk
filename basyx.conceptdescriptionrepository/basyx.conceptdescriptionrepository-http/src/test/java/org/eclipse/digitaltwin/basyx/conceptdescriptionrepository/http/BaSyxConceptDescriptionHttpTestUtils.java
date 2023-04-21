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


package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;

/**
 * Supports the tests working with the HTTP/REST API of ConceptDescription
 * 
 * @author schnicke, danish
 *
 */
public class BaSyxConceptDescriptionHttpTestUtils {
	public static final String CONCEPT_DESCRIPTION_ACCESS_URL = "http://localhost:8080/concept-descriptions";
	private static final String ID_SHORT_PARAM_NAME = "idShort";
	private static final String IS_CASE_OF_PARAM_NAME = "isCaseOf";
	private static final String DATA_SPEC_REF_PARAM_NAME = "dataSpecificationRef";

	public static String getSpecificConceptDescriptionAccessPath(String conceptDescriptionId) {
		return CONCEPT_DESCRIPTION_ACCESS_URL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(conceptDescriptionId);
	}
	
	public static String getAllConceptDescriptionsWithIdShortParameterAccessPath(String idShort) {
		return CONCEPT_DESCRIPTION_ACCESS_URL + "?" + ID_SHORT_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(idShort);
	}
	
	public static String getAllConceptDescriptionsWithIsCaseOfParameterAccessPath(String isCaseOf) {
		return CONCEPT_DESCRIPTION_ACCESS_URL + "?" + IS_CASE_OF_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(isCaseOf);
	}
	
	public static String getAllConceptDescriptionsWithDataSpecRefParameterAccessPath(String dataSpecificationRef) {
		return CONCEPT_DESCRIPTION_ACCESS_URL + "?" + DATA_SPEC_REF_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(dataSpecificationRef);
	}
	
	public static String getAllConceptDescriptionsWithTwoParametersAccessPath(String idShort, String dataSpecificationRef) {
		String idShortOption = ID_SHORT_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(idShort);
		String dataSpecOption = DATA_SPEC_REF_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(dataSpecificationRef);
		
		return CONCEPT_DESCRIPTION_ACCESS_URL + "?" + idShortOption + "&" + dataSpecOption;
	}
	
	public static String getAllConceptDescriptionsWithAllParametersAccessPath(String idShort, String isCaseOf, String dataSpecificationRef) {
		String idShortOption = ID_SHORT_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(idShort);
		String isCaseOfOption = IS_CASE_OF_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(isCaseOf);
		String dataSpecOption = DATA_SPEC_REF_PARAM_NAME + "=" + Base64UrlEncodedIdentifier.encodeIdentifier(dataSpecificationRef);
		
		return CONCEPT_DESCRIPTION_ACCESS_URL + "?" + idShortOption + "&" + isCaseOfOption + "&" + dataSpecOption;
	}
}
