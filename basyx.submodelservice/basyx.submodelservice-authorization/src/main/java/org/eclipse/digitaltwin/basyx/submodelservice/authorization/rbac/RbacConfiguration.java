package org.eclipse.digitaltwin.basyx.submodelservice.authorization.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Configuration
public class RbacConfiguration implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final Optional<MappingJackson2HttpMessageConverter> jacksonConverter = converters.stream().filter(converter -> converter instanceof MappingJackson2HttpMessageConverter).map(converter -> (MappingJackson2HttpMessageConverter) converter).findFirst();

        jacksonConverter.ifPresent(converter -> {
           final ObjectMapper objectMapper = converter.getObjectMapper();

            objectMapper.addMixIn(RbacRule.class, RbacRuleSetDeserializer.RbacRuleMixin.class);
            objectMapper.addMixIn(ITargetInfo.class, RbacRuleSetDeserializer.TargetInformationMixin.class)
                    .registerSubtypes(new NamedType(BaSyxObjectTargetInfo.class, "basyx"));
            objectMapper.registerSubtypes(new NamedType(RbacRuleTargetInfo.class, "rbac"));
        });
    }
}
