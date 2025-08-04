package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component.AasDiscoveryServiceComponent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.DescriptionApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.SearchApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiController;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.HomeController;
import org.eclipse.digitaltwin.basyx.http.description.DescriptionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication

@ComponentScan(
        basePackages = {
               "org.eclipse.digitaltwin.basyx",

        },
        excludeFilters = {
                // Existing excludes
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = DescriptionApiController.class
                ),

                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = DescriptionController.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = SearchApiController.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = ShellDescriptorsApiController.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = HomeController.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = AasDiscoveryServiceComponent.class
                ),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.SpringDocConfiguration.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.documentation.AasDiscoveryServiceApiDocumentationConfiguration.class)
        }
)
public class DigitalTwinRegistry {

    public static void main(String[] args) {
        SpringApplication.run(DigitalTwinRegistry.class, args);
    }
}