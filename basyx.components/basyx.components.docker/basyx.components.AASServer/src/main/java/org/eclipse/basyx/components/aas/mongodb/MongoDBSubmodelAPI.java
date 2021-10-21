/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Implements the ISubmodelAPI for a mongoDB backend.
 * 
 * @author espen
 */
public class MongoDBSubmodelAPI implements ISubmodelAPI {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";
	private static final String SMIDPATH = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected BaSyxMongoDBConfiguration config;
	protected MongoOperations mongoOps;
	protected String collection;
	protected String smId;

	/**
	 * Receives the path of the configuration.properties file in it's constructor.
	 * 
	 * @param config
	 */
	public MongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId) {
		this.setConfiguration(config);
		this.setSubmodelId(smId);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a resource.
	 */
	public MongoDBSubmodelAPI(String resourceConfigPath, String smId) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.setConfiguration(config);
		this.setSubmodelId(smId);
	}

	/**
	 * Constructor using default sql connections
	 */
	public MongoDBSubmodelAPI(String smId) {
		this(DEFAULT_CONFIG_PATH, smId);
	}

	/**
	 * Sets the db configuration for the submodel API.
	 * 
	 * @param config
	 */
	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.config = config;
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.collection = config.getSubmodelCollection();
	}
	
	/**
	 * Sets the submodel id, so that this API points to the submodel with smId. Can be changed
	 * to point to a different submodel in the database.
	 * 
	 * @param smId
	 */
	public void setSubmodelId(String smId) {
		this.smId = smId;
	}

	/**
	 * Depending on whether the model is already in the db, this method inserts or replaces the existing data.
	 * The new submodel id for this API is taken from the given submodel.
	 * 
	 * @param sm
	 */
	public void setSubmodel(Submodel sm) {
		String id = sm.getIdentification().getId();
		this.setSubmodelId(id);

		Query hasId = query(where(SMIDPATH).is(smId));
		Object replaced = mongoOps.findAndReplace(hasId, sm, collection);
		if (replaced == null) {
			mongoOps.insert(sm, collection);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodel getSubmodel() {
		// Query Submodel from MongoDB
		Query hasId = query(where(SMIDPATH).is(smId));
		Submodel result = mongoOps.findOne(hasId, Submodel.class, collection);
		if (result == null) {
			throw new ResourceNotFoundException("The submodel " + smId + " could not be found in the database.");
		}

		// Remove mongoDB-specific map attribute from AASDescriptor
		result.remove("_id");

		// Cast all SubmodelElement maps to ISubmodelElements before returning the submodel
		Map<String, ISubmodelElement> elements = new HashMap<>();
		Map<String, Map<String, Object>> elemMaps = (Map<String, Map<String, Object>>) result
				.get(Submodel.SUBMODELELEMENT);
		for (Entry<String, Map<String, Object>> entry : elemMaps.entrySet()) {
			String shortId = entry.getKey();
			Map<String, Object> elemMap = entry.getValue();
			ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement(elemMap);
			elements.put(shortId, element);
		}
		// Replace the element map in the submodel
		result.put(Submodel.SUBMODELELEMENT, elements);
		// Return the "fixed" submodel
		return result;
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Add element
		sm.addSubmodelElement(elem);
		// Replace db entry
		Query hasId = query(where(SMIDPATH).is(smId));
		mongoOps.findAndReplace(hasId, sm, collection);
	}

	private ISubmodelElement getTopLevelSubmodelElement(String idShort) {
		Submodel sm = (Submodel) getSubmodel();
		Map<String, ISubmodelElement> submodelElements = sm.getSubmodelElements();
		ISubmodelElement element = submodelElements.get(idShort);
		if (element == null) {
			throw new ResourceNotFoundException("The element \"" + idShort + "\" could not be found");
		}
		return convertSubmodelElement(element);
	}

	@SuppressWarnings("unchecked")
	private ISubmodelElement convertSubmodelElement(ISubmodelElement element) {
		// FIXME: Convert internal data structure of ISubmodelElement
		Map<String, Object> elementMap = (Map<String, Object>) element;
		IModelProvider elementProvider = new SubmodelElementProvider(new VABMapProvider(elementMap));
		Object elementVABObj = elementProvider.getValue("");
		return SubmodelElement.createAsFacade((Map<String, Object>) elementVABObj);
	}

	private void deleteTopLevelSubmodelElement(String idShort) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Remove element
		sm.getSubmodelElements().remove(idShort);
		// Replace db entry
		Query hasId = query(where(SMIDPATH).is(smId));
		mongoOps.findAndReplace(hasId, sm, collection);
	}

	@Override
	public Collection<IOperation> getOperations() {
		Submodel sm = (Submodel) getSubmodel();
		return sm.getOperations().values();
	}


	private void addNestedSubmodelElement(List<String> idShorts, ISubmodelElement elem) {
		Submodel sm = (Submodel) getSubmodel();
		// > 1 idShorts => add new sm element to an existing sm element
		if (idShorts.size() > 1) {
			idShorts = idShorts.subList(0, idShorts.size() - 1);
			// Get parent SM element if more than 1 idShort
			ISubmodelElement parentElement = getNestedSubmodelElement(sm, idShorts);
			if (parentElement instanceof SubmodelElementCollection) {
				((SubmodelElementCollection) parentElement).addSubmodelElement(elem);
				// Replace db entry
				Query hasId = query(where(SMIDPATH).is(smId));
				mongoOps.findAndReplace(hasId, sm, collection);
			}
		} else {
			// else => directly add it to the submodel
			sm.addSubmodelElement(elem);
			// Replace db entry
			Query hasId = query(where(SMIDPATH).is(smId));
			mongoOps.findAndReplace(hasId, sm, collection);
		}
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		Submodel sm = (Submodel) getSubmodel();
		return sm.getSubmodelElements().values();
	}

	private void updateTopLevelSubmodelElement(String idShort, Object newValue) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Unwrap value
		newValue = unwrapParameter(newValue);
		// Get and update property value
		getElementProvider(sm, idShort).setValue(Property.VALUE, newValue);
		// Replace db entry
		Query hasId = query(where(SMIDPATH).is(smId));
		mongoOps.findAndReplace(hasId, sm, collection);
	}

	@SuppressWarnings("unchecked")
	private void updateNestedSubmodelElement(List<String> idShorts, Object newValue) {
		Submodel sm = (Submodel) getSubmodel();

		// Get parent SM element
		ISubmodelElement element = getNestedSubmodelElement(sm, idShorts);

		// Update value
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) element);
		IModelProvider elemProvider = SubmodelElementProvider.getElementProvider(mapProvider);
		elemProvider.setValue(Property.VALUE, newValue);

		// Replace db entry
		Query hasId = query(where(SMIDPATH).is(smId));
		mongoOps.findAndReplace(hasId, sm, collection);
	}

	private Object getTopLevelSubmodelElementValue(String idShort) {
		Submodel sm = (Submodel) getSubmodel();
		return getElementProvider(sm, idShort).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	private Object getNestedSubmodelElementValue(List<String> idShorts) {
		ISubmodelElement lastElement = getNestedSubmodelElement(idShorts);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) lastElement);
		return SubmodelElementProvider.getElementProvider(mapProvider).getValue("/value");
	}

	@SuppressWarnings("unchecked")
	protected Object unwrapParameter(Object parameter) {
		if (parameter instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) parameter;
			// Parameters have a strictly defined order and may not be omitted at all.
			// Enforcing the structure with valueType is ok, but we should unwrap null values, too.
			if (map.get("valueType") != null && map.containsKey("value")) {
				return map.get("value");
			}
		}
		return parameter;
	}

	@SuppressWarnings("unchecked")
	private IModelProvider getElementProvider(Submodel sm, String idShort) {
		ISubmodelElement elem = sm.getSubmodelElements().get(idShort);
		IModelProvider mapProvider = new VABMapProvider((Map<String, Object>) elem);
		return SubmodelElementProvider.getElementProvider(mapProvider);
	}

	private ISubmodelElement getNestedSubmodelElement(Submodel sm, List<String> idShorts) {
		Map<String, ISubmodelElement> elemMap = sm.getSubmodelElements();
		// Get last nested submodel element
		for (int i = 0; i < idShorts.size() - 1; i++) {
			String idShort = idShorts.get(i);
			ISubmodelElement elem = elemMap.get(idShort);
			if (elem instanceof SubmodelElementCollection) {
				elemMap = ((SubmodelElementCollection) elem).getSubmodelElements();
			} else {
				throw new ResourceNotFoundException(
						idShort + " in the nested submodel element path could not be resolved.");
			}
		}
		String lastIdShort = idShorts.get(idShorts.size() - 1);
		if (!elemMap.containsKey(lastIdShort)) {
			throw new ResourceNotFoundException(lastIdShort
					+ " in the nested submodel element path could not be resolved.");
		}
		return elemMap.get(lastIdShort);
	}

	private ISubmodelElement getNestedSubmodelElement(List<String> idShorts) {
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Get nested sm element from this sm
		return convertSubmodelElement(getNestedSubmodelElement(sm, idShorts));
	}

	private Object invokeTopLevelOperation(String idShort, Object... params) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	private void deleteNestedSubmodelElement(List<String> idShorts) {
		if ( idShorts.size() == 1 ) {
			deleteSubmodelElement(idShorts.get(0));
			return;
		}
		
		// Get sm from db
		Submodel sm = (Submodel) getSubmodel();
		// Get parent collection
		List<String> parentIds = idShorts.subList(0, idShorts.size() - 1);
		ISubmodelElement parentElement = getNestedSubmodelElement(sm, parentIds);
		// Remove element
		SubmodelElementCollection coll = (SubmodelElementCollection) parentElement;
		coll.deleteSubmodelElement(idShorts.get(idShorts.size() - 1));
		// Replace db entry
		Query hasId = query(where(SMIDPATH).is(smId));
		mongoOps.findAndReplace(hasId, sm, collection);
	}

	private Object invokeNestedOperation(List<String> idShorts, Object... params) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	private Object invokeNestedOperationAsync(List<String> idShorts, Object... params) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		// not possible to invoke operations on a submodel that is stored in a db
		throw new MalformedRequestException("Invoke not supported by this backend");
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		if(idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			return getNestedSubmodelElement(idShorts);
		}else {
			return getTopLevelSubmodelElement(idShortPath);
		}
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		if(idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			deleteNestedSubmodelElement(idShorts);
		}else {
			deleteTopLevelSubmodelElement(idShortPath);
		}
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		if(idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			updateNestedSubmodelElement(idShorts, newValue);
		}else {
			updateTopLevelSubmodelElement(idShortPath, newValue);
		}
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		if(idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			return getNestedSubmodelElementValue(idShorts);
		}else {
			return getTopLevelSubmodelElementValue(idShortPath);
		}
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		if(idShortPath.contains("/")) {
			String[] splitted = VABPathTools.splitPath(idShortPath);
			List<String> idShorts = Arrays.asList(splitted);
			return invokeNestedOperation(idShorts, params);
		}else {
			return invokeTopLevelOperation(idShortPath, params);
		}
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		return invokeNestedOperationAsync(idShorts, params);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		String[] splitted = VABPathTools.splitPath(idShortPath);
		List<String> idShorts = Arrays.asList(splitted);
		addNestedSubmodelElement(idShorts, elem);
	}
}
