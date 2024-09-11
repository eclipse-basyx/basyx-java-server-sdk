package org.eclipse.digitaltwin.basyx.submodelservice.component;

import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class GenericOperationSubmodelService extends AbstractSubmodelServiceDecorator {

	private final Function<String, OperationInvokation> invokableProvider;
	
	public GenericOperationSubmodelService(SubmodelService service, Function<String, OperationInvokation> invokableProvider) {
		super(service);
		this.invokableProvider = invokableProvider;
	}
	
	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input)
			throws ElementDoesNotExistException {
		SubmodelElement elem = getSubmodelElement(idShortPath);
		if (elem == null || !(elem instanceof Operation)) {
			throw new ResponseStatusException(HttpStatusCode.valueOf(404));
		} 
		OperationInvokation invokation = invokableProvider.apply(idShortPath);
		return invokation.invoke(idShortPath, (Operation)elem, input);
	}
}
