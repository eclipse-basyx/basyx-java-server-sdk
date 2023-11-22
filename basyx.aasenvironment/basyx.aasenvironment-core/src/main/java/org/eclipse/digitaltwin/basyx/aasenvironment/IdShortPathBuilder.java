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
