package org.eclipse.digitaltwin.basyx.aasregistry.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean(name = "org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("DotAAS Part 2 | HTTP/REST | Asset Administration Shell Registry Service Specification")
                                .description("The Full Profile of the Asset Administration Shell Registry Service Specification as part of the [Specification of the Asset Administration Shell: Part 2](http://industrialdigitaltwin.org/en/content-hub).   Publisher: Industrial Digital Twin Association (IDTA) 2023")
                                .contact(
                                        new Contact()
                                                .name("Industrial Digital Twin Association (IDTA)")
                                                .email("info@idtwin.org")
                                )
                                .license(
                                        new License()
                                                .name("CC BY 4.0")
                                                .url("https://creativecommons.org/licenses/by/4.0/")
                                )
                                .version("V3.0.1_SSP-001")
                )
        ;
    }
}