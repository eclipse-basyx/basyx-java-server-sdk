package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression(KafkaSubmodelServiceFeature.FEATURE_ENABLED_EXPRESSION)
@ConditionalOnProperty(name = KafkaSubmodelServiceApplicationListener.SUBMODEL_EVENTS_ACTIVATED, havingValue = "true", matchIfMissing = false)
public class KafkaSubmodelServiceApplicationListener  implements ApplicationListener<ApplicationEvent> {
	
	public static final String SUBMODEL_EVENTS_ACTIVATED = KafkaSubmodelServiceFeature.FEATURENAME + ".submodelevents";
	
	private final SubmodelEventHandler handler;
	private final Submodel submodel;
	
	@Autowired
	private KafkaSubmodelServiceApplicationListener(SubmodelEventHandler handler, Submodel submodel) {
		this.handler = handler;
		this.submodel = submodel;
	}
	
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
    	// only fired if submodelEvents are active
    	if (event instanceof ApplicationReadyEvent) {
    		handler.onSubmodelCreated(submodel);
    	} else if (event instanceof ContextClosedEvent) {
    		handler.onSubmodelDeleted(submodel.getId());
    	}
    }
}
