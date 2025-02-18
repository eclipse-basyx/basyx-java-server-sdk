package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudSubmodelServiceFactory implements SubmodelServiceFactory{

    private final SubmodelBackend backend;

    public CrudSubmodelServiceFactory(SubmodelBackend backend) {
        this.backend = backend;
    }

    @Override
    public SubmodelService create(Submodel submodel) {
        return new CrudSubmodelService(backend, submodel);
    }
    
}
