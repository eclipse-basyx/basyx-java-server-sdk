package org.eclipse.digitaltwin.basyx.authorization.abac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ConditionalOnProperty("basyx.feature.authorization.enabled")
@ConditionalOnExpression(value = "'${basyx.feature.authorization.type}' == 'abac' && ('${basyx.feature.authorization.rules.backend}' == 'MongoDB')")
@RequestMapping("/abac-rules")
public class AbacRuleController {

    @Autowired
    private AbacRuleService abacRuleService;

    /**
     * Get all ABAC rules.
     */
    @GetMapping
    public ResponseEntity<List<AccessPermissionRule>> getAllRules() {
        List<AccessPermissionRule> rules = abacRuleService.getAbacRules();
        return ResponseEntity.ok(rules);
    }

    /**
     * Get filtered ABAC rules based on RightsEnum, Access type, and Model.
     */
    @GetMapping("/filtered")
    public ResponseEntity<List<AccessPermissionRule>> getFilteredRules(
            @RequestParam RightsEnum right,
            @RequestParam Acl.Access access,
            @RequestBody ObjectItem objectItem) {
        
        List<AccessPermissionRule> filteredRules = abacRuleService.getFilteredAbacRules(right, access, objectItem);
        return ResponseEntity.ok(filteredRules);
    }

    /**
     * Add a new ABAC rule.
     */
    @PostMapping
    public ResponseEntity<String> addRule(@RequestBody AccessPermissionRule rule) {
        abacRuleService.addRule(rule);
        return ResponseEntity.ok("ABAC Rule added successfully.");
    }

    /**
     * Remove an ABAC rule by ID.
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<String> removeRule(@PathVariable String ruleId) {
        abacRuleService.removeRule(ruleId);
        return ResponseEntity.ok("ABAC Rule removed successfully.");
    }

    /**
     * Check if a rule exists by ID.
     */
    @GetMapping("/exists/{ruleId}")
    public ResponseEntity<Boolean> ruleExists(@PathVariable String ruleId) {
        boolean exists = abacRuleService.exists(ruleId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get a specific rule by ID.
     */
    @GetMapping("/{ruleId}")
    public ResponseEntity<AccessPermissionRule> getRule(@PathVariable String ruleId) {
        AccessPermissionRule rule = abacRuleService.getRule(ruleId);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * Update an existing rule.
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<AccessPermissionRule> updateRule(@PathVariable String ruleId, @RequestBody AccessPermissionRule rule) {
    	abacRuleService.update(ruleId, rule);
    	return ResponseEntity.ok(rule);
    }
}
