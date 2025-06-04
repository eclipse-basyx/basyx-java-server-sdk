
package org.eclipse.digitaltwin.basyx.core.query;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RightsEnum {

    CREATE("CREATE"),
    READ("READ"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    EXECUTE("EXECUTE"),
    VIEW("VIEW"),
    ALL("ALL"),
    TREE("TREE");
    private final String value;
    private final static Map<String, RightsEnum> CONSTANTS = new HashMap<String, RightsEnum>();

    static {
        for (RightsEnum c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RightsEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static RightsEnum fromValue(String value) {
        RightsEnum constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
