package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.confguration;

import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebugConfig {

    @Bean
    public CommandLineRunner debugBeans(ApplicationContext context) {
        return args -> {
            System.out.println("=== Available ShellDescriptorsApiDelegate beans ===");
            String[] beanNames = context.getBeanNamesForType(ShellDescriptorsApiDelegate.class);
            for (String beanName : beanNames) {
                System.out.println("Bean name: " + beanName);
                Object bean = context.getBean(beanName);
                System.out.println("Bean class: " + bean.getClass().getName());
                System.out.println("---");
            }
        };
    }
}
