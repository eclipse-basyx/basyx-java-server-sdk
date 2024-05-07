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

package org.eclipse.digitaltwin.basyx.submodelservice.client.connectedSubmodelElements;

import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

public class ConnectedBlob implements ConnectedSubmodelElement<byte[], Blob> {

	private String submodelServiceUrl;
	private String idShort;
	private ConnectedSubmodelService service;

	public ConnectedBlob(String submodelServiceUrl, String idShort) {
		this.submodelServiceUrl = submodelServiceUrl;
		this.idShort = idShort;
		service = new ConnectedSubmodelService(submodelServiceUrl);
	}

	@Override
	public byte[] getValue() {
		Blob blobElement = (Blob) service.getSubmodelElement(idShort);
		return blobElement.getValue();
	}

	@Override
	public void setValue(byte[] value) {
		Blob blobElement = (Blob) service.getSubmodelElement(idShort);
		blobElement.setValue(value);
		service.updateSubmodelElement(idShort, blobElement);
	}

	@Override
	public Blob getSubmodelElement() {
		// TODO Auto-generated method stub
		return null;
	}

}