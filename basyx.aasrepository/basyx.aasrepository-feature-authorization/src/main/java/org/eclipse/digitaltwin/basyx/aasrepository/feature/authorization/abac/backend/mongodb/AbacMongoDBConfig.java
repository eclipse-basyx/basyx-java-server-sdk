package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac.backend.mongodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
	
	private static final String DATABASE_NAME = "basyx"; // Change accordingly

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create("mongodb://mongoAdmin:mongoPassword@localhost:27017"), DATABASE_NAME);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }

}
