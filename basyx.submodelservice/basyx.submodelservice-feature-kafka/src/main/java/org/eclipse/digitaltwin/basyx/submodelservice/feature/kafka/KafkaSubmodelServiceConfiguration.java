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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.BlobRemovingSubmodelShrinker;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.DistributingSubmodelEventHandler;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.IdOnlyEventDistributer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.KafkaSubmodelEventDistributer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventDistributer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventHandler;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.WithoutBlobEventDistributer;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.DataPreservationLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@ConditionalOnExpression(KafkaSubmodelServiceFeature.FEATURE_ENABLED_EXPRESSION)
@Configuration
public class KafkaSubmodelServiceConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public JsonSerializer aas4jSerializer() {
		return new JsonSerializer();
	}

	@ConditionalOnMissingBean
	@Bean
	public DataPreservationLevel preservationLevel(
			@Value("${" + KafkaSubmodelServiceFeature.FEATURENAME + ".preservationlevel:RETAIN_FULL}") String level) {
		return DataPreservationLevel.valueOf(level);
	}

	@Bean
	@ConditionalOnMissingBean
	public SubmodelEventDistributer eventDistributer(DataPreservationLevel level, JsonSerializer serializer,
			KafkaTemplate<String, String> template,
			@Value("${" + KafkaSubmodelServiceFeature.FEATURENAME + ".topic.name:submodel-events}") String topicName) {
		SubmodelEventDistributer distributer = new KafkaSubmodelEventDistributer(serializer, template, topicName);
		if (DataPreservationLevel.REMOVE_BLOB_VALUE == level) {
			BlobRemovingSubmodelShrinker shrinker = new BlobRemovingSubmodelShrinker();
			return new WithoutBlobEventDistributer(distributer, shrinker);
		} else if (DataPreservationLevel.IDS_ONLY == level) {
			return new IdOnlyEventDistributer(distributer);
		}
		return distributer;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SubmodelEventHandler submodelEventHandler(SubmodelEventDistributer distributer) {
		return new DistributingSubmodelEventHandler(distributer);
	}
}
