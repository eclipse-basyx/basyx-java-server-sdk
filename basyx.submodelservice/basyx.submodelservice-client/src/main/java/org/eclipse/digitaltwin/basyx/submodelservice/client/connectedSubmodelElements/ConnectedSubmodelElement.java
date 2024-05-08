package org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

public class ConnectedSubmodelElement<E extends SubmodelElementValue, T extends SubmodelElement> {
	protected String submodelServiceUrl;
	protected String idShortPath;
	protected ConnectedSubmodelService service;
	public ConnectedSubmodelElement(String submodelServiceUrl, String idShort){
		this.submodelServiceUrl = submodelServiceUrl;
		this.idShortPath = idShort;
		service = new ConnectedSubmodelService(submodelServiceUrl);
	}

	public E getValue() {
		return (E) service.getSubmodelElementValue(idShortPath);
	}

	public void setValue(E value) {
		service.setSubmodelElementValue(idShortPath, value);
	}

	public T getSubmodelElement() {
		T blobElement = (T) service.getSubmodelElement(idShortPath);
		return blobElement;
	}
}
