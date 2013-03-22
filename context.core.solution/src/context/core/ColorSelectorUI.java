package context.core;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

@SuppressWarnings("restriction")
public class ColorSelectorUI {
	@Inject
	public ColorSelectorUI(Composite parent, IEclipseContext context) {
		context = context.createChild();
		parent.setLayout(new GridLayout(2, false));
		
		createSelectedColor(context);
		
		{
			Control choices = createColorChoices(parent, context);
			choices.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}
	
	private void createSelectedColor(IEclipseContext context) {
		context.set(Point.class, new Point(300, 300));
		ContextInjectionFactory.make(PreviewItem.class, context);
	}
	
	private Control createColorChoices(Composite parent, IEclipseContext context) {
		Group g = new Group(parent, SWT.BORDER);
		g.setText("Colors");
		g.setLayout(new GridLayout(10, true));
		
		IEclipseContext locContext = context.createChild();
		locContext.set(Composite.class, g);
		locContext.set(Point.class, new Point(40, 40));
		
		for( RGB rgb : RGBValues.RGBS ) {
			IEclipseContext child = locContext.createChild();
			child.set(RGB.class, rgb);
			ContextInjectionFactory.make(ColorItem.class, child);
		}
		
		return g;
	}
}
