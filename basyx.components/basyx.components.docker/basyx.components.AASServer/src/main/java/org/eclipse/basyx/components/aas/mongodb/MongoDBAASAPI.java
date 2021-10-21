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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Implements the IAASAPI for a mongoDB backend.
 * 
 * @author espen
 */
public class MongoDBAASAPI implements IAASAPI {
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";
	private static final String AASIDPATH = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected BaSyxMongoDBConfiguration config;
	protected MongoOperations mongoOps;
	protected String collection;
	protected String aasId;

	/**
	 * Receives the path of the configuration.properties file in it's constructor.
	 * 
	 * @param config
	 */
	public MongoDBAASAPI(BaSyxMongoDBConfiguration config, String aasId) {
		this.setConfiguration(config);
		this.setAASId(aasId);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a resource.
	 */
	public MongoDBAASAPI(String resourceConfigPath, String aasId) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.setConfiguration(config);
		this.setAASId(aasId);
	}

	/**
	 * Constructor using default sql connections
	 */
	public MongoDBAASAPI(String aasId) {
		this(DEFAULT_CONFIG_PATH, aasId);
	}

	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.config = config;
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.collection = config.getAASCollection();
	}

	/**
	 * Sets the aas id, so that this API points to the aas with aasId. Can be changed
	 * to point to a different aas in the database.
	 * 
	 * @param aasId
	 */
	public void setAASId(String aasId) {
		this.aasId = aasId;
	}

	/**
	 * Depending on whether the model is already in the db, this method inserts or replaces the existing data.
	 * The new aas id for this API is taken from the given aas.
	 * 
	 * @param aas
	 */
	public void setAAS(AssetAdministrationShell aas) {
		String id = aas.getIdentification().getId();
		this.setAASId(id);
		
		Query hasId = query(where(AASIDPATH).is(aasId));
		// Try to replace if already present - otherwise: insert it
		Object replaced = mongoOps.findAndReplace(hasId, aas, collection);
		if (replaced == null) {
			mongoOps.insert(aas, collection);
		}
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		Query hasId = query(where(AASIDPATH).is(aasId));
		AssetAdministrationShell aas = mongoOps.findOne(hasId, AssetAdministrationShell.class, collection);
		if (aas == null) {
			throw new ResourceNotFoundException("The AAS " + aasId + " could not be found in the database.");
		}
		// Remove mongoDB-specific map attribute from AASDescriptor
		aas.remove("_id");
		return aas;
	}

	@Override
	public void addSubmodel(IReference submodel) {
		// Get AAS from db
		Query hasId = query(where(AASIDPATH).is(aasId));
		AssetAdministrationShell aas = mongoOps.findOne(hasId, AssetAdministrationShell.class, collection);
		if (aas == null) {
			throw new ResourceNotFoundException("The AAS " + aasId + " could not be found in the database.");
		}
		// Add reference
		aas.addSubmodelReference(submodel);
		// Update db entry
		mongoOps.findAndReplace(hasId, aas, collection);
	}

	@Override
	public void removeSubmodel(String id) {
		// Get AAS from db
		Query hasId = query(where(AASIDPATH).is(aasId));
		AssetAdministrationShell aas = mongoOps.findOne(hasId, AssetAdministrationShell.class, collection);
		if (aas == null) {
			throw new ResourceNotFoundException("The AAS " + aasId + " could not be found in the database.");
		}
		// Remove reference
		Collection<IReference> smReferences = aas.getSubmodelReferences();
		// Reference to submodel could be either by idShort (=> local) or directly via
		// its identifier
		for (Iterator<IReference> iterator = smReferences.iterator(); iterator.hasNext();) {
			IReference ref = iterator.next();
			List<IKey> keys = ref.getKeys();
			IKey lastKey = keys.get(keys.size() - 1);
			String idValue = lastKey.getValue();
			// remove this reference, if the last key points to the submodel
			if (idValue.equals(id)) {
				iterator.remove();
				break;
			}
		}
		// Update db entry
		mongoOps.findAndReplace(hasId, aas, collection);
	}

}
