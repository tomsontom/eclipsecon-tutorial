package context.core;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application implements IApplication {

	public Object start(IApplicationContext appContext) throws Exception {
		BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		
		Display d = new Display();
		Shell s = new Shell(d);
		s.setLayout(new FillLayout());
		
		IEclipseContext context = EclipseContextFactory.getServiceContext(bundleContext);
		context.set(Composite.class, s);
		
		ContextInjectionFactory.make(ColorSelectorUI.class, context);
		
		s.open();
		
		while( ! s.isDisposed() ) {
			if( ! d.readAndDispatch() ) {
				d.sleep();
			}
		}
		
		d.dispose();
		
		return IApplication.EXIT_OK;
	}

	public void stop() {
		// nothing to do
	}
}
