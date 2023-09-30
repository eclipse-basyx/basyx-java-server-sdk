package org.eclipse.digitaltwin.basyx.authorization.rbac;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Tag(name = "RBAC Api", description = "API to manage rules for Role-based Access Control")
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "'")
public class RbacApi {
    private final IRbacService service;

    public RbacApi(IRbacService storage) {
        this.service = storage;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/authorization-rbac"
    )
    public ResponseEntity<RbacRuleSet> getRBACRule() {
        return ResponseEntity.of(Optional.ofNullable(service.getRbacRuleSet()));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/authorization-rbac",
            consumes = { "application/json" }
    )
    public void putRBACRule(
            @Parameter(name = "RbacRule", description = "Rbac Rule object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.addRule(rbacRule);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/authorization-rbac",
            consumes = { "application/json" }
    )
    public void deleteRBACRule(
            @Parameter(name = "RbacRule", description = "Rbac Rule object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.removeRule(rbacRule);
    }
}
