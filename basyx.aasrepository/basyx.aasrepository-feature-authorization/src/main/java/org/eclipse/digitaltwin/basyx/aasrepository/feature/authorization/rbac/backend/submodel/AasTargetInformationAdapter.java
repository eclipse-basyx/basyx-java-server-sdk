/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac.backend.submodel;

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
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.AasTargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel.TargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;

/**
 * An implementation of the {@link TargetInformationAdapter} to adapt with Aas
 * {@link TargetInformation}
 * 
 * @author danish
 */
public class AasTargetInformationAdapter implements TargetInformationAdapter {

	@Override
	public SubmodelElementCollection adapt(TargetInformation targetInformation) {

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").build();

		SubmodelElementList aasId = new DefaultSubmodelElementList.Builder().idShort("aasIds").build();

		List<SubmodelElement> aasIds = ((AasTargetInformation) targetInformation).getAasIds().stream().map(this::transform).collect(Collectors.toList());
		aasId.setValue(aasIds);

		targetInformationSMC.setValue(Arrays.asList(aasId));

		return targetInformationSMC;
	}

	@Override
	public TargetInformation adapt(SubmodelElementCollection targetInformation) {

		SubmodelElement aasIdSubmodelElement = targetInformation.getValue().stream().filter(sme -> sme.getIdShort().equals("aasIds")).findAny().orElseThrow(
				() -> new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName()));

		if (!(aasIdSubmodelElement instanceof SubmodelElementList))
			throw new InvalidTargetInformationException("The TargetInformation defined in the SubmodelElementCollection Rule with id: " + targetInformation.getIdShort() + " is not compatible with the " + getClass().getName());

		SubmodelElementList aasIdList = (SubmodelElementList) aasIdSubmodelElement;

		List<String> aasIds = aasIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).collect(Collectors.toList());

		return new AasTargetInformation(aasIds);
	}

	private Property transform(String aasId) {
		return new DefaultProperty.Builder().value(aasId).build();
	}

}
