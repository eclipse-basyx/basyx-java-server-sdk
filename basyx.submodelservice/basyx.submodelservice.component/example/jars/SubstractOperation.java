import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class SubstractOperation {

	public OperationVariable[] invoke(String path, Operation op, OperationVariable[] in)  {
		Property first = (Property) in[0].getValue();
		Property second = (Property) in[1].getValue();
		int iFirst = Integer.parseInt(first.getValue());
		int iSecond = Integer.parseInt(second.getValue());
		int result = iFirst - iSecond;
		Property prop = new DefaultProperty.Builder().value(String.valueOf(result)).valueType(DataTypeDefXsd.INT).build();
		return new OperationVariable[] {new DefaultOperationVariable.Builder().value(prop).build()};
	}
	
}
