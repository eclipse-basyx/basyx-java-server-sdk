import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class TestOperation {

	public OperationVariable[] invoke(OperationVariable[] vars)  {
		Property prop = (Property) vars[0].getValue();
		// can't assign int to string
		String value = Integer.parseInt(prop.getValue());
		int result = value * value;
		Property toReturn = new DefaultProperty.Builder().value(String.valueOf(result)).valueType(DataTypeDefXsd.INT).build();
		return new OperationVariable[] {new DefaultOperationVariable.Builder().value(toReturn).build()};
	}
	
}
