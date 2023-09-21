package org.eclipse.digitaltwin.basyx.conceptdescription.feature.authorization.rbac;

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
@Tag(name = "Concept Description Repository RBAC", description = "RBAC API for the Content Description Repository")
@ConditionalOnExpression(value = "'${basyx.conceptdescriptionrepository.feature.authorization.enabled:false}' and '${basyx.conceptdescriptionrepository.feature.authorization.type}' == 'rbac'")
public class RbacApi<RbacRuleFilterType> {
    private final IRbacService service;

    public RbacApi(IRbacService service) {
        this.service = service;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/concept-descriptions-rbac"
    )
    public ResponseEntity<RbacRuleSet> putRBACRule() {
        return ResponseEntity.of(Optional.ofNullable(service.getRbacRuleSet()));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/concept-descriptions-rbac",
            consumes = { "application/json" }
    )
    public void putRBACRule(
            @Parameter(name = "Concept Descriptions RBAC Rule", description = "Concept Descriptions RBAC Rule object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.addRule(rbacRule);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/concept-descriptions-rbac",
            consumes = { "application/json" }
    )
    public void deleteRBACRule(
            @Parameter(name = "Concept Descriptions RBAC Rule", description = "Concept Descriptions RBAC Rule object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.removeRule(rbacRule);
    }
}
