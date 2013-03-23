package simple.swt.renderer;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindowElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.osgi.service.event.Event;

@SuppressWarnings("restriction")
public class SimpleRenderingEngine implements IPresentationEngine {
	@Inject
	Display display;
	
	public Object createGui(MUIElement element, Object parentWidget,
			IEclipseContext parentContext) {
		return null;
	}

	public Object createGui(MUIElement element) {
		if( element instanceof MWindow ) {
			return createMainWindow((MWindow) element, ((MApplication)(MUIElement)element.getParent()).getContext());
		} else if( element instanceof MPart ) {
			return createPart((MPart) element, ((MWindow)(MUIElement)element.getParent()).getContext(), (Composite)element.getParent().getWidget());
		}
		return null;
	}

	private Object createMainWindow(final MWindow window, IEclipseContext parentContext) {
		IEclipseContext context = parentContext.createChild("Window");
		context.set(MWindow.class, window);
		window.setContext(context);
		
		Shell s = new Shell(display);
		s.setBounds(window.getX(),window.getY(),window.getWidth(),window.getHeight());
		s.setLayout(new FillLayout());
		window.setWidget(s);
		
		for( MWindowElement e : window.getChildren() ) {
			if( e.isToBeRendered() ) {
				createGui(e);
			}
		}
		
		s.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				removeGui(window);
			}
		});
		
		s.open();
		
		return s;
	}
	
	@Inject
	@Optional
	private void handleWindowWidthChanged(@UIEventTopic(UIEvents.Window.TOPIC_WIDTH) Event event) {
		MWindow window = (MWindow) event.getProperty(UIEvents.EventTags.ELEMENT);
		Integer value = (Integer) event.getProperty(UIEvents.EventTags.NEW_VALUE);
		Shell s = (Shell) window.getWidget();
		s.setSize(new Point(value.intValue(), s.getSize().y));
	}
	
	@Inject
	@Optional
	private void handleWindowHeightChanged(@UIEventTopic(UIEvents.Window.TOPIC_HEIGHT) Event event) {
		MWindow window = (MWindow) event.getProperty(UIEvents.EventTags.ELEMENT);
		Integer value = (Integer) event.getProperty(UIEvents.EventTags.NEW_VALUE);
		Shell s = (Shell) window.getWidget();
		s.setSize(new Point(s.getSize().x, value.intValue()));
	}
	
	private Object createPart(MPart part, IEclipseContext parentContext, Composite parent) {
		IEclipseContext context = parentContext.createChild("Part");
		context.set(MPart.class, part);
		part.setContext(context);
		
		Composite container = new Composite(parent,SWT.NONE);
		context.set(Composite.class, container);
		
		IContributionFactory factory = context.get(IContributionFactory.class);
		factory.create(part.getContributionURI(), context);
		
		return container;
	}
	
	public void removeGui(MUIElement element) {
		if( element instanceof MWindow ) {
			MWindow window = (MWindow) element;
			for( MWindowElement e : window.getChildren() ) {
				removeGui(e);
			}
			disposeContext(window);
			disposeWidget(window);
		} else if( element instanceof MPart ) {
			MPart p = (MPart) element;
			disposeContext(p);
			disposeWidget(p);
		}
	}

	private void disposeWidget(MUIElement element) {
		if( element.getWidget() != null ) {
			if( ((Widget)element.getWidget()).isDisposed() ) {
				((Widget)element.getWidget()).dispose();	
			}
			
			element.setWidget(null);
		}
	}
	
	private void disposeContext(MContext element) {
		if( element.getContext() != null ) {
			element.getContext().dispose();
			element.setContext(null);
		}
	}
	
	public Object run(MApplicationElement uiRoot, IEclipseContext appContext) {
		MApplication application = (MApplication) uiRoot;
		
		for( MWindow w : application.getChildren() ) {
			createGui(w);
		}
		
		while( oneVisible(application.getChildren()) ) {
			if( ! display.readAndDispatch() ) {
				display.sleep();
			}
		}
		
		return null;
	}

	private boolean oneVisible(List<MWindow> list) {
		for( MWindow m : list ) {
			if( m.isToBeRendered() && m.getWidget() != null ) {
				return true;
			}
		}
		return false;
	}
	
	public void stop() {
		
	}

}
