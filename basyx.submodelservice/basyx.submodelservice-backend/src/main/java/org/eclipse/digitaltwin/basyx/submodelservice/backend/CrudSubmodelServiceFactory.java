package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
@ConditionalOnBean(SubmodelBackend.class)
public class CrudSubmodelServiceFactory implements SubmodelServiceFactory{

    private final SubmodelBackend backend;
    private final SubmodelFileOperations submodelFileOperations;

    public CrudSubmodelServiceFactory(SubmodelBackend backend, SubmodelFileOperations submodelFileOperations) {
        this.backend = backend;
        this.submodelFileOperations = submodelFileOperations;
    }

    @Override
    public SubmodelService create(Submodel submodel) {
        return new CrudSubmodelService(backend, submodelFileOperations, submodel);
    }
    
}
