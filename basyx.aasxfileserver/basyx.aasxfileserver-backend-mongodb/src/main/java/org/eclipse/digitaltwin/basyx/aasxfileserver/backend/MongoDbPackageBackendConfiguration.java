package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.common.mongocore.MappingEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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

    @Bean
    MappingEntry packageMappingEntry(@Value("${" + COLLECTION_NAME_FIELD + ":" + DEFAULT_COLLECTION_NAME + "}") String collectionName) {
        return MappingEntry.of(collectionName, Package.class);
    }
}
