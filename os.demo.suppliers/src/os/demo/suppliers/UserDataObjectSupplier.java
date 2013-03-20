package os.demo.suppliers;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;

import os.demo.suppliers.model.User;

public class UserDataObjectSupplier extends ExtendedObjectSupplier{

	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor,
			boolean track, boolean group) {
		
		System.err.println(requestor+" is requesting something that looks like "+descriptor);
		
		return new User("sopot.cela","ilikefreebeer");
	}

}
