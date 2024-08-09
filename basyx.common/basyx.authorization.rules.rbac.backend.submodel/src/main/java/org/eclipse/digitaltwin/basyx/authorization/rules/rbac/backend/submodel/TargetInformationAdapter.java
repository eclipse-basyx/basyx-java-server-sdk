package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;

public interface TargetInformationAdapter {
	
	SubmodelElementCollection adapt(TargetInformation targetInformation);
	
	TargetInformation adapt(SubmodelElementCollection targetInformation);

}
