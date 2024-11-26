package org.eclipse.digitaltwin.basyx.authorization.abac;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME, // Use a type name to distinguish types
	    include = JsonTypeInfo.As.PROPERTY, // Include type info as a property
	    property = "type" // The property that indicates the type
	)
	@JsonSubTypes({
	    @JsonSubTypes.Type(value = LogicalExpression__1.class, name = "logicalExpression"),
	    @JsonSubTypes.Type(value = SimpleExpression.class, name = "simpleExpression")
	})
public interface LogicalComponent {

}
