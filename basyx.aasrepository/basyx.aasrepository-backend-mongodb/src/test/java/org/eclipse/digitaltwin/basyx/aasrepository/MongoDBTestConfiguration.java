package org.eclipse.digitaltwin.basyx.aasrepository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan("org.eclipse.digitaltwin.basyx.*")
@EnableMongoRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasrepository*")
public class MongoDBTestConfiguration {

}
