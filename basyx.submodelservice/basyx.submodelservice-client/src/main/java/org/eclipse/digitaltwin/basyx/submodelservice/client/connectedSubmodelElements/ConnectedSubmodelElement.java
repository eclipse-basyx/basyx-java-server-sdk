package org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class ConnectedSubmodelElement<E, T> {
	protected String submodelServiceUrl;
	protected String idShortPath;
	protected ConnectedSubmodelService service;
	public ConnectedSubmodelElement(String submodelServiceUrl, String idShort){
		this.submodelServiceUrl = submodelServiceUrl;
		this.idShortPath = idShort;
		service = new ConnectedSubmodelService(submodelServiceUrl);
	}

	public E getValue()
	{
		throw new NotImplementedException();
	};

	public void setValue(E value){
		throw new NotImplementedException();

	};

	public T getSubmodelElement(){
		throw new NotImplementedException();
	};
}
