package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;

public class HTTPOperationDelegation implements OperationDelegation {
	
	public static final String INVOCATION_DELEGATION_TYPE = "invocationDelegation";

	@Override
	public OperationVariable[] delegate(Qualifier qualifier, OperationVariable[] input) {
		return null;
	}

}
