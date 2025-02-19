import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class HelloOperation {

	public OperationVariable[] invoke(OperationVariable[] in)  {
		String value = new HelloWorld().sayHello();
		Property prop = new DefaultProperty.Builder().value(value).valueType(DataTypeDefXsd.STRING).build();
		return new OperationVariable[] {new DefaultOperationVariable.Builder().value(prop).build()};
	}
	
}
