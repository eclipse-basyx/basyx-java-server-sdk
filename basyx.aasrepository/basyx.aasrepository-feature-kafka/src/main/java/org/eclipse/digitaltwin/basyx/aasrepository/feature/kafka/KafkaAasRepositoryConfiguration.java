/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.AasEventDistributer;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.AasEventHandler;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.DistributingAasEventHandler;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.KafkaAasEventDistributer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@ConditionalOnExpression(KafkaAasRepositoryFeature.FEATURE_ENABLED_EXPRESSION)
@Configuration
public class KafkaAasRepositoryConfiguration {
	
	@ConditionalOnMissingBean
	@Bean
	public JsonSerializer aas4jSerializer() {
		return new JsonSerializer();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AasEventDistributer aasEventDistributer(JsonSerializer serializer,
			KafkaTemplate<String, String> template,
			@Value("${" + KafkaAasRepositoryFeature.FEATURENAME + ".topic.name:aas-events}") String topicName) {
		return new KafkaAasEventDistributer(serializer, template, topicName);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AasEventHandler aasEventHandler(AasEventDistributer distributer) {
		return new DistributingAasEventHandler(distributer);
	}
}
