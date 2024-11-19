package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component;

import org.eclipse.digitaltwin.basyx.aasregistry.service.api.DescriptionApiController;
import org.eclipse.digitaltwin.basyx.http.description.DescriptionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//@Configuration
public class CustomControllerConfig {

    @Autowired
    private DescriptionApiController descriptionApiController1;

    @Autowired
    private DescriptionController descriptionApiController2;

    @Bean
    @Primary
    public DescriptionApiController descriptionApiController() {
        // Return the controller you want to be primary
        return descriptionApiController1;  // This will make DescriptionApiController1 the primary one
    }
}

