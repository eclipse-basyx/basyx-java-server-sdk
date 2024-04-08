package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryPersistencyTestSuite;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.common.mongocore.MongoDBUtilities;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Test persistency for AasDiscovery with MongoDB Storage backend
 * 
 * @author mateusmolina
 */
public class TestMongoDBAasDiscoveryPersistency extends AasDiscoveryPersistencyTestSuite {

	private static final String[] ARGS = { "--spring.config.name=application-mongodb" };

	private static final String COLLECTION = "aasdiscovery-service";

	private static ConfigurableApplicationContext applicationContext;

	private static AasDiscoveryService discoveryService;

	private static MongoTemplate mongoTemplate;

	@BeforeClass
	public static void initComponent() {
		applicationContext = new SpringApplication(AasDiscoveryServiceComponent.class).run(ARGS);
		discoveryService = applicationContext.getBean(AasDiscoveryService.class);
		mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	}

	@BeforeClass
	public static void clearTemplate() {
		MongoDBUtilities.clearCollection(mongoTemplate, COLLECTION);
	}

	@Override
	protected AasDiscoveryService getAasDiscoveryService() {
		return discoveryService;
	}

	@Override
	protected void restartComponent() {
		applicationContext.close();
		initComponent();
	}

}
