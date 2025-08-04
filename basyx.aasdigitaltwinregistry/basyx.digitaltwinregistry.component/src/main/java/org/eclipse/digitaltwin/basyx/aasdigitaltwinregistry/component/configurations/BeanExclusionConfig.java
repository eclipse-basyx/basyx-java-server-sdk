package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.configurations;

import lombok.AllArgsConstructor;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBAasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class BeanExclusionConfig {

    private final AasDiscoveryDocumentBackend aasDiscoveryDocumentBackend;

    @Bean
    public static BeanFactoryPostProcessor removeDuplicateControllers() {
        return beanFactory -> {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;

            String[] duplicateBeans = {
                    "descriptionApiController",
                    "searchApiController",
                    "shellDescriptorsApiController",
                    "homeController",
                    "registryEventLogSink"
            };

            for (String beanName : duplicateBeans) {
                if (factory.containsBeanDefinition(beanName)) {
                    factory.removeBeanDefinition(beanName);
                }
            }
        };
    }

    @Bean
    public MongoDBCrudAasDiscovery mongoDBCrudAasDiscoveryBean() {
        return new MongoDBCrudAasDiscovery(aasDiscoveryDocumentBackend, "Discovery");
    }
}