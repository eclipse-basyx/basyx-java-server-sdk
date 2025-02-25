package org.eclipse.digitaltwin.basyx.examples.operationdelegation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).httpBasic(Customizer.withDefaults()).csrf(c -> c.ignoringRequestMatchers("/operation-invocation"));

        return httpSecurity.build();
    }
}
