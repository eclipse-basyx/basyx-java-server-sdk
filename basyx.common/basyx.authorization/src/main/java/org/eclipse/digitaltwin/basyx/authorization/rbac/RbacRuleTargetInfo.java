package org.eclipse.digitaltwin.basyx.authorization.rbac;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

public class RbacRuleTargetInfo implements ITargetInfo {
    @JsonCreator
    public RbacRuleTargetInfo() {
    }

    @Override
    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RbacRuleTargetInfo)) {
            return false;
        }

        final RbacRuleTargetInfo other = (RbacRuleTargetInfo) o;

        return new EqualsBuilder().isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).toHashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder("RbacRuleTargetInfo{").toString();
    }
}
