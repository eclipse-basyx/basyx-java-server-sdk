package org.eclipse.digitaltwin.basyx.authorization.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Configuration
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "'")
public class CommonRbacConfig implements WebMvcConfigurer {
    public static final String RBAC_AUTHORIZATION_TYPE = "rbac";

    public static final String PROPERTIES_PREFIX = CommonAuthorizationConfig.PROPERTIES_PREFIX + ".rbac";
    public static final String RULES_FILE_KEY = PROPERTIES_PREFIX + ".file";
    public final static String DEFAULT_RULES_FILE = "/rbac_rules.json";
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final Optional<MappingJackson2HttpMessageConverter> jacksonConverter = converters.stream().filter(converter -> converter instanceof MappingJackson2HttpMessageConverter).map(converter -> (MappingJackson2HttpMessageConverter) converter).findFirst();

        jacksonConverter.ifPresent(converter -> {
           final ObjectMapper objectMapper = converter.getObjectMapper();

            objectMapper.addMixIn(RbacRule.class, RbacRuleSetDeserializer.RbacRuleMixin.class);
            objectMapper.addMixIn(ITargetInfo.class, RbacRuleSetDeserializer.TargetInfoMixin.class)
                    .registerSubtypes(new NamedType(BaSyxObjectTargetInfo.class, "basyx"));
            objectMapper.registerSubtypes(new NamedType(RbacRuleTargetInfo.class, "rbac"));
        });
    }
}
