//package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.configurations;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.License;
//import org.eclipse.digitaltwin.basyx.http.documentation.RepositoryApiDocumentationConfiguration;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.swagger.v3.oas.models.info.Info;
//
//@Configuration
//public class DigitalTwinRegistryApiDocumentationConfiguration extends RepositoryApiDocumentationConfiguration {
//
//    private static final String TITLE = "BaSyx Digital Twin Registry Component";
//    private static final String DESCRIPTION = "Digital Twin Registry API";
//
//    @Override
//    protected Info apiInfo() {
//        return new Info().title(TITLE).description(DESCRIPTION).version(VERSION).contact(apiContact())
//                .license(apiLicence());
//    }
//
//}