package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
public class DummyAASXFileServerComponent {

    public static void main(String[] args) {
        SpringApplication.run(DummyAASXFileServerComponent.class, args);
    }
}
