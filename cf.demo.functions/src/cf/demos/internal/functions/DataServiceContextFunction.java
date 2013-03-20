package cf.demos.internal.functions;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class DataServiceContextFunction extends ContextFunction{

	@Override
	public Object compute(IEclipseContext context) {
		
		
		MPart mPart = context.get(MPart.class);
		if (mPart==null) return null;
		System.err.println("IDataService being requested in context of part: "+mPart.getLabel());
		if ("p2".equals(mPart.getElementId())){
			return ContextInjectionFactory.make(SpecialDataService.class, context);
		}
		return ContextInjectionFactory.make(SimpleDataService.class, context);
	}

}
