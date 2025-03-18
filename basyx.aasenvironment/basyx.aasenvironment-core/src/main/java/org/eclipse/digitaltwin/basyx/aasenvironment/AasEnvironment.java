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
package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.io.IOException;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;

/**
 * Specifies the overall AasEnvironment API
 *
 * @author zhangzai, danish
 *
 */
public interface AasEnvironment {

	/**
	 * Generate serialization from given aas and
	 *
	 * @param aasIds
	 * @param submodelIds
	 * @param includeConceptDescriptions
	 * @return
	 * @throws SerializationException
	 */
	public String createJSONAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException;

	public String createXMLAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException;

	public byte[] createAASXAASEnvironmentSerialization(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) throws SerializationException, IOException;

	void loadEnvironment(CompleteEnvironment completeEnvironment, boolean ignoreDuplicates);
	void loadEnvironment(CompleteEnvironment completeEnvironment);
}
