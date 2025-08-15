package org.eclipse.digitaltwin.basyx.aasrepository.feature.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Class that prints error and warning messages to inform the user about possible misconfiguration
 *
 * @author fried, aaronzi
 */
@Component
@ConditionalOnExpression("#{${" + SearchAasRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.search.enabled:false}}")
public class SearchAasRepositoryConfigurationGuard implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SearchAasRepositoryConfigurationGuard.class);

    @Value("${spring.elasticsearch.uris:#{null}}")
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.username:#{null}}")
    private String elasticsearchUsername;

    @Value("${spring.elasticsearch.password:#{null}}")
    private String elasticsearchPassword;

    @Value("${basyx.backend:#{null}}")
    private String basyxBackend;

    @Value("${" + SearchAasRepositoryFeature.FEATURENAME + ".indexname:" + SearchAasRepositoryFeature.DEFAULT_INDEX + "}")
    private String indexName;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean error = false;
        logger.info(":::::::::::::::: BaSyx Feature Search Configuration ::::::::::::::::");

        if (elasticsearchUrl == null || elasticsearchUrl.isEmpty()) {
            logger.error("Elasticsearch URL is not configured. Please set the property 'spring.elasticsearch.uris'.");
            error = true;
        } else {
            logger.info("Elasticsearch URL: " + elasticsearchUrl);
        }

        if (elasticsearchUsername == null || elasticsearchUsername.isEmpty()) {
            logger.error("Elasticsearch username is not configured. Please set the property 'spring.elasticsearch.username'.");
            error = true;
        } else {
            logger.info("Elasticsearch Username: " + elasticsearchUsername);
        }

        if (elasticsearchPassword == null || elasticsearchPassword.isEmpty()) {
            logger.error("Elasticsearch password is not configured. Please set the property 'spring.elasticsearch.password'.");
            error = true;
        } else {
            logger.info("Elasticsearch Password: " + "***");
        }

        if(basyxBackend.equals("InMemory")){
            logger.error("BaSyx Backend is set to InMemory. Search feature requires a persistent backend.");
            error = true;
        } else {
            logger.info("BaSyx Backend: " + basyxBackend);
        }

        if (indexName == null || indexName.isEmpty()) {
            logger.error("Index name is not configured. Please set the property '" + SearchAasRepositoryFeature.FEATURENAME + ".indexname'.");
            error = true;
        } else {
            logger.info("Index Name: " + indexName);
        }

        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
        if(error){
            System.exit(1);
        }
    }
}
