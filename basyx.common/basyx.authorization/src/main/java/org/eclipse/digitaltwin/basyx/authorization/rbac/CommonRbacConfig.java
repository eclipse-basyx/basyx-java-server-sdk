/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
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
