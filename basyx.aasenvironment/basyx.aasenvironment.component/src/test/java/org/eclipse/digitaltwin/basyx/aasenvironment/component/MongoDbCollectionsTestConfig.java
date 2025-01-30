package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@TestConfiguration
public class MongoDbCollectionsTestConfig {
    protected static final String CONNECTION_URL = "mongodb://mongoAdmin:mongoPassword@localhost:27017/";
    protected static final String DB_NAME = "aas-env";
    protected static final String AAS_REPO_COLLECTION = "aas-repo";
    protected static final String SM_REPO_COLLECTION = "submodel-repo";
    protected static final String CD_REPO_COLLECTION = "cd-repo";

    @Bean
    MongoTemplate buildMongoTemplate() {
        MongoClient client = MongoClients.create(CONNECTION_URL);
        return new MongoTemplate(client, DB_NAME);
    }
}
