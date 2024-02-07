package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;

public interface OperationDelegation {
	
	OperationVariable[] delegate(Qualifier qualifier, OperationVariable[] input) throws OperationDelegationException;

}
