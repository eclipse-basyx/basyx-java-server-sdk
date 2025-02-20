package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac.backend.mongodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClients;

@Configuration
@ConditionalOnProperty("basyx.feature.authorization.enabled")
@ConditionalOnExpression(value = "'${basyx.feature.authorization.type}' == 'abac' && ('${basyx.feature.authorization.rules.backend}' == 'MongoDB')")
public class AbacMongoDBConfig {
	
	private static final String DATABASE_NAME = "basyx";

    @Bean
    @ConditionalOnMissingBean
    public MongoDatabaseFactory mongoDatabaseFactoryABAC() {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create("mongodb://mongoAdmin:mongoPassword@localhost:27017"), DATABASE_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoTemplate mongoTemplateABAC() {
        return new MongoTemplate(mongoDatabaseFactoryABAC());
    }

}
