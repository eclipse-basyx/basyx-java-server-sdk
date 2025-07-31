package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.configurations;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanExclusionConfig {

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
}