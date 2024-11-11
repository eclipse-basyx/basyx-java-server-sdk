/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class SubmodelEvent {

	private SubmodelEventType type;
	private String id;
	private Submodel submodel;
	private SubmodelElement smElement;
	private String smElementPath;
	
	public SubmodelEventType getType() {
		return type;
	}
	
	public void setType(SubmodelEventType type) {
		this.type = type;
	}

	public Submodel getSubmodel() {
		return submodel;
	}
	
	public void setSubmodel(Submodel submodel) {
		this.submodel = submodel;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setSmElement(SubmodelElement element) {
		this.smElement = element;
	}
	
	public SubmodelElement getSmElement() {
		return smElement;
	}
	
	public void setSmElementPath(String path) {
		this.smElementPath = path;
	}
	
	public String getSmElementPath() {
		return smElementPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, smElement, smElementPath, submodel, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmodelEvent other = (SubmodelEvent) obj;
		return Objects.equals(id, other.id) && Objects.equals(smElement, other.smElement)
				&& Objects.equals(smElementPath, other.smElementPath) && Objects.equals(submodel, other.submodel)
				&& type == other.type;
	}

	@Override
	public String toString() {
		return "SubmodelEvent [type=" + type + ", id=" + id + ", submodel=" + submodel + ", smElement=" + smElement
				+ ", smElementPath=" + smElementPath + "]";
	}
}
