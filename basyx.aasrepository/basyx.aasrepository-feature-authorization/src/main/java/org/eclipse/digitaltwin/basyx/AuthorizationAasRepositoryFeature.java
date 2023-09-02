package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("#{${" + AuthorizationAasRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
@Order(0)
@Component
public class AuthorizationAasRepositoryFeature implements AasRepositoryFeature {
    public final static String FEATURENAME = "basyx.aasrepository.feature.authorization";

    @Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
    private boolean enabled;

    @Autowired
    public AuthorizationAasRepositoryFeature() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public String getName() {
        return "AasRepository Authorization";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Autowired
    private PermissionResolver permissionResolver;

    @Autowired
    private SecurityResolver securityResolver;

    @Override
    public AasRepositoryFactory decorate(AasRepositoryFactory aasRepositoryFactory) {
        return new AuthorizationAasRepositoryFactory(aasRepositoryFactory, permissionResolver, securityResolver);
    }
}
