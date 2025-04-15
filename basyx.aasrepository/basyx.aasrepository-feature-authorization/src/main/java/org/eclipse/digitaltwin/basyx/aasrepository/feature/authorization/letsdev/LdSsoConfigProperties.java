package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.letsdev;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ld-sso")
public class LdSsoConfigProperties {

    String baseUrl;
    String audience;
    Boolean debugEnabled = false;
    String[] whitelistedIps = {};

    public String[] getWhitelistedIps() {

        return whitelistedIps;
    }

    public String getBaseUrl() {

        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {

        this.baseUrl = baseUrl;
    }

    public String getAudience() {

        return audience;
    }

    public void setAudience(String audience) {

        this.audience = audience;
    }

    public Boolean getDebugEnabled() {

        return debugEnabled;
    }

    public void setDebugEnabled(Boolean debugEnabled) {

        this.debugEnabled = debugEnabled;
    }
}
