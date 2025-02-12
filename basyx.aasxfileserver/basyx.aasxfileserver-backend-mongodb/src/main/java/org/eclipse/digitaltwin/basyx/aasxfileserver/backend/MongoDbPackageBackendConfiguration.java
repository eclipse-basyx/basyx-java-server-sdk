package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.eclipse.digitaltwin.basyx.common.mongocore.BasyxMongoMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;

/**
 * Configuration for the MongoDB {@link PackageBackend}
 * 
 * @author mateusmolina
 */
@Configuration
@ConditionalOnExpression("'${basyx.backend}'.equals('MongoDB')")
@EnableMongoRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasxfileserver.backend")
public class MongoDbPackageBackendConfiguration {

    static final String COLLECTION_NAME_FIELD = "basyx.aasxfileserver.mongodb.collectionName";
    static final String DEFAULT_COLLECTION_NAME = "aasxfileserver";

    @Autowired
    void mapMongoEntity(BasyxMongoMappingContext mappingContext, @Value("${" + COLLECTION_NAME_FIELD + ":" + DEFAULT_COLLECTION_NAME + "}") String collectionName) {
        mappingContext.addEntityMapping(Package.class, collectionName);
    }
}
