/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac.backend.submodel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel.TargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.SubmodelTargetInformation;

/**
 * An implementation of the {@link TargetInformationAdapter} to adapt with Submodel
 * {@link TargetInformation}
 * 
 * @author danish
 */
public class SubmodelTargetInformationAdapter implements TargetInformationAdapter {

	@Override
	public SubmodelElementCollection adapt(TargetInformation targetInformation) {

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").build();

		SubmodelElementList submodelId = new DefaultSubmodelElementList.Builder().idShort("submodelIds").build();
		SubmodelElementList submodelElementId = new DefaultSubmodelElementList.Builder().idShort("submodelElementIdShortPaths").build();
		Property typeProperty = new DefaultProperty.Builder().idShort("@type").value("submodel").build();

		List<SubmodelElement> submodelIds = ((SubmodelTargetInformation) targetInformation).getSubmodelIds().stream().map(this::transform).collect(Collectors.toList());
		List<SubmodelElement> submodelElementIds = ((SubmodelTargetInformation) targetInformation).getSubmodelElementIdShortPaths().stream().map(this::transform).collect(Collectors.toList());
		submodelId.setValue(submodelIds);
		submodelElementId.setValue(submodelElementIds);

		targetInformationSMC.setValue(Arrays.asList(submodelId, submodelElementId, typeProperty));

		return targetInformationSMC;
	}

	@Override
	public TargetInformation adapt(SubmodelElementCollection targetInformation) {

		String targetInformationType = getTargetInformationType(targetInformation);

		if (!targetInformationType.equals("submodel"))
			throw new InvalidTargetInformationException("The TargetInformation @type: " + targetInformationType + " is not compatible with " + getClass().getName() + ".");

		SubmodelElement submodelIdSubmodelElement = targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("submodelIds")).findAny().orElseThrow(
				() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName()));

		SubmodelElement smeIdSubmodelElement = targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("submodelElementIdShortPaths")).findAny().orElseThrow(
				() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName()));

		if (!(submodelIdSubmodelElement instanceof SubmodelElementList) || !(smeIdSubmodelElement instanceof SubmodelElementList))
			throw new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName());

		SubmodelElementList submodelIdList = (SubmodelElementList) submodelIdSubmodelElement;
		SubmodelElementList smeIdList = (SubmodelElementList) smeIdSubmodelElement;

		List<String> submodelIds = submodelIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).collect(Collectors.toList());
		List<String> smeIds = smeIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).collect(Collectors.toList());

		return new SubmodelTargetInformation(submodelIds, smeIds);
	}

	private Property transform(String submodelId) {
		return new DefaultProperty.Builder().value(submodelId).build();
	}

	private String getTargetInformationType(SubmodelElementCollection targetInformation) {

		Property typeProperty = (Property) targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("@type")).findAny()
				.orElseThrow(() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " does not have @type definition"));

		return typeProperty.getValue();
	}

}
