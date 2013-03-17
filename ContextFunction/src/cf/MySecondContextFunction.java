package cf;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;

import service.internal.FreezingService;

public class MySecondContextFunction implements IContextFunction{
	
	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(FreezingService.class, context);
	}

}
