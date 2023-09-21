package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Config {
    @Bean
    public IRoleAuthenticator roleAuthenticator() {
        return new JWTRoleAuthenticator(new SubjectInfoFromSecurityContextProvider());
    }
}
