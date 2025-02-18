package org.eclipse.digitaltwin.basyx.submodelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
public class DummyMongoDbSubmodelServiceComponent {
 
    public static void main(String[] args) {
        SpringApplication.run(DummyMongoDbSubmodelServiceComponent.class, args);
    }
}
