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

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.rbac.backend.submodel;

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
import org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.AasEnvironmentTargetInformation;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.AasTargetInformation;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac.backend.submodel.AasTargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel.TargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.ConceptDescriptionTargetInformation;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.rbac.backend.submodel.CDTargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.SubmodelTargetInformation;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac.backend.submodel.SubmodelTargetInformationAdapter;

/**
 * An implementation of the {@link TargetInformationAdapter} to adapt with Aas
 * {@link TargetInformation}
 * 
 * @author danish
 */
public class AasEnvironmentTargetInformationAdapter implements TargetInformationAdapter {

	@Override
	public SubmodelElementCollection adapt(TargetInformation targetInformation) {
		
		if (targetInformation instanceof AasTargetInformation)
			return new AasTargetInformationAdapter().adapt(targetInformation);
		
		if (targetInformation instanceof SubmodelTargetInformation)
			return new SubmodelTargetInformationAdapter().adapt(targetInformation);
		
		if (targetInformation instanceof ConceptDescriptionTargetInformation)
			return new CDTargetInformationAdapter().adapt(targetInformation);

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").build();

		SubmodelElementList aasId = new DefaultSubmodelElementList.Builder().idShort("aasIds").build();
		SubmodelElementList submodelId = new DefaultSubmodelElementList.Builder().idShort("submodelIds").build();
		Property typeProperty = new DefaultProperty.Builder().idShort("@type").value("aas-environment").build();

		List<SubmodelElement> aasIds = ((AasEnvironmentTargetInformation) targetInformation).getAasIds().stream().map(this::transform).collect(Collectors.toList());
		List<SubmodelElement> submodelIds = ((AasEnvironmentTargetInformation) targetInformation).getSubmodelIds().stream().map(this::transform).collect(Collectors.toList());
		aasId.setValue(aasIds);
		submodelId.setValue(submodelIds);

		targetInformationSMC.setValue(Arrays.asList(aasId, submodelId, typeProperty));

		return targetInformationSMC;
	}

	@Override
	public TargetInformation adapt(SubmodelElementCollection targetInformation) {
		
		String targetInformationType = getTargetInformationType(targetInformation);
		
		if (targetInformationType.equals("aas"))
			return new AasTargetInformationAdapter().adapt(targetInformation);
		
		if (targetInformationType.equals("submodel"))
			return new SubmodelTargetInformationAdapter().adapt(targetInformation);
		
		if (targetInformationType.equals("concept-description"))
			return new CDTargetInformationAdapter().adapt(targetInformation);
		
		if (!targetInformationType.equals("aas-environment"))
			throw new InvalidTargetInformationException(
					"The TargetInformation @type: " + targetInformationType + " is not compatible with "
							+ getClass().getName() + ".");

		SubmodelElement aasIdSubmodelElement = targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("aasIds")).findAny().orElseThrow(
				() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName()));
		
		SubmodelElement submodelIdSubmodelElement = targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("submodelIds")).findAny().orElseThrow(
				() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName()));

		if (!(aasIdSubmodelElement instanceof SubmodelElementList) || !(submodelIdSubmodelElement instanceof SubmodelElementList))
			throw new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName());

		SubmodelElementList aasIdList = (SubmodelElementList) aasIdSubmodelElement;
		SubmodelElementList submodelIdList = (SubmodelElementList) submodelIdSubmodelElement;

		List<String> aasIds = aasIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).collect(Collectors.toList());
		List<String> submodelIds = submodelIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).collect(Collectors.toList());

		return new AasEnvironmentTargetInformation(aasIds, submodelIds);
	}

	private String getTargetInformationType(SubmodelElementCollection targetInformation) {
		
		Property typeProperty = (Property) targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("@type")).findAny().orElseThrow(() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " does not have @type definition"));
		
		return typeProperty.getValue();
	}

	private Property transform(String aasId) {
		return new DefaultProperty.Builder().value(aasId).build();
	}

}
