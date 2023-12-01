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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidIdShortPathElementsException;

/**
 * Builder class for building IdShortPath from a list of {@link SubmodelElement}
 * 
 * <p>
 * Some examples of valid list of {@link SubmodelElement}
 * </p>
 * <pre>
 * [SMC, SML, SMC, Property]
 * [SMC]
 * [File]
 * [SML, SML, MultiLanguageProperty]
 * [SMC, SMC, SML]
 * </pre>
 * 
 * @author danish
 */
public class IdShortPathBuilder {

	private List<SubmodelElement> idShortPathElements;

	public IdShortPathBuilder(List<SubmodelElement> idShortPathElements) {
		super();
		this.idShortPathElements = idShortPathElements;

		validateIdShortPathElements(idShortPathElements);
	}

	/**
	 * Builds the IdShortPath
	 * 
	 * @return the idShortPath
	 */
	public String build() {
		int i = 0;
		int j = 1;

		if (j == idShortPathElements.size())
			return new StringBuilder(idShortPathElements.get(i).getIdShort()).toString();

		boolean previousOccurrenceSubmodelElementList = false;

		StringBuilder idShortPathBuilder = new StringBuilder();

		while (j < idShortPathElements.size()) {
			SubmodelElement parentSME = idShortPathElements.get(i);
			SubmodelElement childSME = idShortPathElements.get(j);

			boolean isFirstElement = i == 0;

			if (parentSME instanceof SubmodelElementList) {
				appendSMLIdShortPath(isFirstElement, previousOccurrenceSubmodelElementList, idShortPathBuilder, parentSME, childSME);

				previousOccurrenceSubmodelElementList = true;
			} else {

				if (previousOccurrenceSubmodelElementList) {
					i++;
					j++;

					previousOccurrenceSubmodelElementList = false;

					continue;
				}

				appendNonSMLIdShortPath(isFirstElement, idShortPathBuilder, parentSME);

				previousOccurrenceSubmodelElementList = false;
			}

			i++;
			j++;
		}

		if (!(idShortPathElements.get(i - 1) instanceof SubmodelElementList))
			idShortPathBuilder.append(".").append(idShortPathElements.get(i).getIdShort());

		return idShortPathBuilder.toString();
	}

	private void appendNonSMLIdShortPath(boolean isFirstElement, StringBuilder idShortPathBuilder, SubmodelElement parentSME) {
		if (!isFirstElement)
			idShortPathBuilder.append(".");

		idShortPathBuilder.append(parentSME.getIdShort());
	}

	private void appendSMLIdShortPath(boolean isFirstElement, boolean previousOccurrenceSubmodelElementList, StringBuilder idShortPathBuilder, SubmodelElement parentSME, SubmodelElement childSME) {
		int index = ((SubmodelElementList) parentSME).getValue().indexOf(childSME);

		if (previousOccurrenceSubmodelElementList) {
			idShortPathBuilder.append("[").append(index).append("]");

			return;
		}

		if (!isFirstElement)
			idShortPathBuilder.append(".");

		idShortPathBuilder.append(parentSME.getIdShort()).append("[").append(index).append("]");
	}

	private void validateIdShortPathElements(List<SubmodelElement> idShortPathElements) {
		if (idShortPathElements == null || idShortPathElements.isEmpty())
			throw new InvalidIdShortPathElementsException("List of SubmodelElements for creating IdShortPath should not be null or empty.");
	}

}
