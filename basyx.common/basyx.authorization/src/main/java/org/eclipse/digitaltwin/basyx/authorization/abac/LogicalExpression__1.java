
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"type",
    "$and",
    "$or",
    "$not"
})
public class LogicalExpression__1 implements LogicalComponent {

	@JsonProperty("type")
    private Object type;
    @JsonProperty("$and")
    private List<LogicalComponent> $and = new ArrayList<>();
    @JsonProperty("$or")
    private List<LogicalComponent> $or = new ArrayList<>();
    @JsonProperty("$not")
    private LogicalComponent $not;
    
    @JsonProperty("type")
    public Object getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Object type) {
        this.type = type;
    }

    @JsonProperty("$and")
    public List<LogicalComponent> get$and() {
        return $and;
    }

    @JsonProperty("$and")
    public void set$and(List<LogicalComponent> $and) {
        this.$and = $and;
    }

    @JsonProperty("$or")
    public List<LogicalComponent> get$or() {
        return $or;
    }

    @JsonProperty("$or")
    public void set$or(List<LogicalComponent> $or) {
        this.$or = $or;
    }

    @JsonProperty("$not")
    public LogicalComponent get$not() {
        return $not;
    }

    @JsonProperty("$not")
    public void set$not(LogicalComponent $not) {
        this.$not = $not;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LogicalExpression__1 other = (LogicalExpression__1) obj;

        return Objects.equals($and, other.$and) &&
               Objects.equals($or, other.$or) &&
               Objects.equals($not, other.$not);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash($and, $or, $not);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LogicalExpression__1 {");

        if ($and != null && !$and.isEmpty()) {
            sb.append("\"$and\": ").append($and).append(", ");
        }
        if ($or != null && !$or.isEmpty()) {
            sb.append("\"$or\": ").append($or).append(", ");
        }
        if ($not != null) {
            sb.append("\"$not\": ").append($not).append(", ");
        }

        // Remove trailing comma and space if present
        if (sb.length() > 19 && sb.charAt(sb.length() - 2) == ',') {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");
        return sb.toString();
    }



}

