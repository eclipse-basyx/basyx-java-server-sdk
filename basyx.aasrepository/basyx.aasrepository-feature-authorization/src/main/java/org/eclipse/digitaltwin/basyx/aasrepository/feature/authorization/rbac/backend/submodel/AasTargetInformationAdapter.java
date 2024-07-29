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
	
	private Property transform(String aasId) {
		return new DefaultProperty.Builder().value(aasId).build();
	}

}
