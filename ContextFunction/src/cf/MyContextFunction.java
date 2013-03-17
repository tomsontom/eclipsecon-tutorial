package cf;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import service.internal.CoolServiceImpl;
import service.internal.HotServiceImpl;


public class MyContextFunction implements IContextFunction {

	@Override
	public Object compute(IEclipseContext context) {
		MPart mPart = context.get(MPart.class);
		if (mPart==null){
			System.err.println("This is wrong");
			return null;
		}
		
		if ("p2".equals(mPart.getElementId()))
			return ContextInjectionFactory.make(CoolServiceImpl.class, context);
		else
			return ContextInjectionFactory.make(HotServiceImpl.class, context);
	}

}
