package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.documentation;

import io.swagger.v3.oas.models.OpenAPI;
import org.eclipse.digitaltwin.basyx.http.documentation.RepositoryApiDocumentationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Primary;

@Configuration
public class DTRegistryApiDocumentationConfiguration extends RepositoryApiDocumentationConfiguration {

	private static final String TITLE = "BaSyx Digital Twin Registry";
	private static final String DESCRIPTION = "BaSyx Digital Twin Registry API";

	@Bean
	@Primary
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(apiInfo());
	}

	@Override
	protected Info apiInfo() {
		return new Info().title(TITLE)
				.description(DESCRIPTION)
				.version(VERSION)
				.contact(apiContact())
				.license(apiLicence());
	}
}