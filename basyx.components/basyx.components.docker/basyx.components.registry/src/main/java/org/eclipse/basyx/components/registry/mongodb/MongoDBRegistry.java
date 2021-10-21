package org.eclipse.basyx.components.registry.mongodb;

import org.eclipse.basyx.aas.registration.memory.AASRegistry;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;

/**
 * Wrapper class for AASRegistry with MongoDB backend
 * 
 * @author espen
 *
 */
public class MongoDBRegistry extends AASRegistry {

	/**
	 * Constructor for initializing the registry with a mongoDB config
	 * 
	 * @param mongoDBConfig
	 */
	public MongoDBRegistry(BaSyxMongoDBConfiguration mongoDBConfig) {
		super(new MongoDBRegistryHandler(mongoDBConfig));
	}
}
