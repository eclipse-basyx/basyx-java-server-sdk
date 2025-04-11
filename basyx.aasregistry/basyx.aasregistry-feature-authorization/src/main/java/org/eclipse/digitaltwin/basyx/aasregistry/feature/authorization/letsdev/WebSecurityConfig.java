package org.eclipse.digitaltwin.basyx.aasregistry.feature.authorization.letsdev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.AUD;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {

    private static final String JWKS_PATH = "/.well-known/jwks.json";

    private final LdSsoConfigProperties ldSsoConfigProperties;

    public WebSecurityConfig(LdSsoConfigProperties ldSsoConfigProperties) {

        this.ldSsoConfigProperties = ldSsoConfigProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                        oauth2ResourceServerCustomizer.jwt(jwtCustomizer -> {
                            jwtCustomizer.jwkSetUri(ldSsoConfigProperties.getBaseUrl() + JWKS_PATH);
                            jwtCustomizer.jwtAuthenticationConverter(new JwtAuthenticationConverter());
                            jwtCustomizer.decoder(jwtDecoder()); // Validate jwt aud claim
                        })
                )
                .authorizeHttpRequests(customizer ->
                        customizer
                                .anyRequest()
                                .authenticated()
                );

        return http.build();
    }

    OAuth2TokenValidator<Jwt> audienceValidator() {
        return new JwtClaimValidator<List<String>>(AUD, aud -> aud.contains(ldSsoConfigProperties.getAudience()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        String issuerUri = ldSsoConfigProperties.getBaseUrl();

        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return web -> web.debug(ldSsoConfigProperties.getDebugEnabled());
    }
}
