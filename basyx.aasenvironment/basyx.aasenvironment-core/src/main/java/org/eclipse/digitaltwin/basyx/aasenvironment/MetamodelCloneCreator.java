/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

/**
 * Creates clone of AAS Metamodels
 * 
 * @author danish
 *
 */
public class MetamodelCloneCreator {
	
	private JsonSerializer jsonSerializer = new JsonSerializer();
	private JsonDeserializer jsonDeserializer = new JsonDeserializer();
	
	/**
	 * Creates clone of the provided list of {@link AssetAdministrationShell}
	 * 
	 * @param originalShells
	 * @return the cloned AAS list
	 */
	public List<AssetAdministrationShell> cloneAssetAdministrationShells(List<AssetAdministrationShell> originalShells) {
		String serializedAASs = getSerializedShells(originalShells);
		
		return getDeserializedShells(serializedAASs);
	}
	
	/**
	 * Creates clone of the provided list of {@link Submodel}
	 * 
	 * @param originalSubmodels
	 * @return the cloned Submodel list
	 */
	public List<Submodel> cloneSubmodels(List<Submodel> originalSubmodels) {
		String serializedSubmodels = getSerializedSubmodels(originalSubmodels);
		
		return getDeserializedSubmodels(serializedSubmodels);
	}
	
	/**
	 * Creates clone of the provided list of {@link ConceptDescription}
	 * 
	 * @param originalConceptDescriptions
	 * @return the cloned ConceptDescription list
	 */
	public List<ConceptDescription> cloneConceptDescriptions(List<ConceptDescription> originalConceptDescriptions) {
		String serializedCDs = getSerializedCDs(originalConceptDescriptions);
		
		return getDeserializedCDs(serializedCDs);
	}

	private String getSerializedShells(List<AssetAdministrationShell> originalShells) {
		try {
			return jsonSerializer.write(originalShells);
		} catch (SerializationException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Unable to serialize the AASs");
		}
	}
	
	private List<AssetAdministrationShell> getDeserializedShells(String serializedShells) {
		try {
			return jsonDeserializer.readList(serializedShells, AssetAdministrationShell.class);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to deserialize the AASs");
		}
	}
	
	private String getSerializedSubmodels(List<Submodel> originalSubmodels) {
		try {
			return jsonSerializer.write(originalSubmodels);
		} catch (SerializationException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Unable to serialize the Submodels");
		}
	}
	
	private List<Submodel> getDeserializedSubmodels(String serializedSubmodels) {
		try {
			return jsonDeserializer.readList(serializedSubmodels, Submodel.class);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to deserialize the Submodels");
		}
	}
	
	private String getSerializedCDs(List<ConceptDescription> originalCDs) {
		try {
			return jsonSerializer.write(originalCDs);
		} catch (SerializationException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Unable to serialize the Concept descriptions");
		}
	}
	
	private List<ConceptDescription> getDeserializedCDs(String serializedCDs) {
		try {
			return jsonDeserializer.readList(serializedCDs, ConceptDescription.class);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to deserialize the Concept descriptions");
		}
	}

}
