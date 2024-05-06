package org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements;

public interface ConnectedSubmodelElement<E, T> {

	public E getValue();

	public void setValue(E value);

	public T getSubmodelElement();
}
