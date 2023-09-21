package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization.rbac;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@RestController
@Tag(name = "Submodel Registry RBAC", description = "RBAC API for the Submodel Registry")
@ConditionalOnExpression(value = "'${basyx.submodelrepository.feature.authorization.enabled:false}' and '${basyx.submodelrepository.feature.authorization.type}' == 'rbac'")
public class RbacApi {
    private final IRbacService service;

    public RbacApi(IRbacService service) {
        this.service = service;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/submodel-descriptors-rbac"
    )
    public ResponseEntity<RbacRuleSet> getRBACRule() {
        return ResponseEntity.of(Optional.ofNullable(service.getRbacRuleSet()));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/submodel-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void putRBACRule(
            @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.addRule(rbacRule);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/submodel-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void deleteRBACRule(
            @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.removeRule(rbacRule);
    }
}
