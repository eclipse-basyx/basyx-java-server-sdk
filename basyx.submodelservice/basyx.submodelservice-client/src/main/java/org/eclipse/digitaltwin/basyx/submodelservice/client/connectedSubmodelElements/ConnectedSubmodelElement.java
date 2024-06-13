package org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

/**
 * Base class for connected submodel elements
 * 
 * @param <E> Implementation of the SubmodelElementValue
 * @param <T> Implementation of the SubmodelElement
 * 
 * @author zielstor, fried
 */
public class ConnectedSubmodelElement<E extends SubmodelElementValue, T extends SubmodelElement> {
	protected String submodelServiceUrl;
	protected String idShortPath;
	protected ConnectedSubmodelService service;

	/**
	 * 
	 * @param submodelServiceUrl URL of the submodel service
	 * @param idShort            idShort of the submodel element
	 */
	public ConnectedSubmodelElement(String submodelServiceUrl, String idShort){
		this.submodelServiceUrl = submodelServiceUrl;
		this.idShortPath = idShort;
		service = new ConnectedSubmodelService(submodelServiceUrl);
	}

	/**
	 * @return the value only representation of the SubmodelElement
	 */
	public E getValue() {
		return (E) service.getSubmodelElementValue(idShortPath);
	}

	/**
	 * Sets the value of the SubmodelElement
	 * 
	 * @param value the new value
	 */
	public void setValue(E value) {
		service.setSubmodelElementValue(idShortPath, value);
	}

	/**
	 * @return the SubmodelElement
	 */
	public T getSubmodelElement() {
		T blobElement = (T) service.getSubmodelElement(idShortPath);
		return blobElement;
	}
}
