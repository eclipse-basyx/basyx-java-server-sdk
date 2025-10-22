package org.eclipse.digitaltwin.basyx.authorization.letsdev;

import org.eclipse.digitaltwin.basyx.http.CorsPathPatternProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            List<CorsPathPatternProvider> configurationUrlProviders,
            @Value("${basyx.cors.allowed-origins:}") String[] allowedOrigins,
            @Value("${basyx.cors.allowed-methods:}") String[] allowedMethods,
            @Value("${basyx.cors.allowed-headers:}") String[] allowedHeaders) {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (allowedOrigins.length == 0 && allowedMethods.length == 0) {
            logger.info("No CORS configuration applied (empty origins/methods).");
            return source; // return empty config so app still starts
        }

        logger.info("---- Configuring CORS ----");
        for (CorsPathPatternProvider provider : configurationUrlProviders) {
            String pathPattern = provider.getPathPattern();

            logger.info("{} configured with allowedOriginPatterns {}", pathPattern, Arrays.toString(allowedOrigins));
            logger.info(
                    allowedMethods.length == 0
                            ? "No allowed methods configured"
                            : "{} configured with allowedMethods {}",
                    pathPattern, Arrays.toString(allowedMethods));

            CorsConfiguration cors = new CorsConfiguration();
            cors.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
            cors.setAllowedMethods(Arrays.asList(allowedMethods));
            cors.setAllowedHeaders(Arrays.asList(allowedHeaders));
            cors.setAllowCredentials(true);
            cors.setExposedHeaders(List.of());

            source.registerCorsConfiguration(pathPattern, cors);
        }

        return source;
    }
}
