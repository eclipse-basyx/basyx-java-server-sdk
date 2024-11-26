package org.eclipse.digitaltwin.basyx.aasregistry.service;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
    basePackages = {"org.eclipse.digitaltwin.basyx.authorization", "org.eclipse.digitaltwin.basyx.authorization.rbac", "org.eclipse.digitaltwin.basyx.aasregistry.feature", "org.eclipse.digitaltwin.basyx.aasregistry.service", "org.eclipse.digitaltwin.basyx.aasregistry.service.api" , "org.eclipse.digitaltwin.basyx.aasregistry.service.configuration"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class OpenApiGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiGeneratorApplication.class, args);
    }

    @Bean(name = "org.eclipse.digitaltwin.basyx.aasregistry.service.OpenApiGeneratorApplication.jsonNullableModule")
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}