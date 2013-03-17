package supplier;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;


public class CoolObjectSupplier extends ExtendedObjectSupplier {

	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor,
			boolean track, boolean group) {
		System.err.println(requestor+" is requesting " + descriptor.getDesiredType());
		String result = "Sopot";
		System.err.println("Giving him : "+ result);
		return result;
	}

}
