package renderer;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

@SuppressWarnings("restriction")
public class CustomRendererFactory extends WorkbenchRendererFactory {
	private FancyTrimWindowRender windowRenderer;
	private PGroupContributedPartRenderer pgroupRenderer;
	
	@Override
	public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
		if( uiElement instanceof MWindow ) {
			if( windowRenderer == null ) {
				windowRenderer = new FancyTrimWindowRender();
				initRenderer(windowRenderer);
			}
			return windowRenderer;
		}
		//TODO add extra handler for MPart elements
		return super.getRenderer(uiElement, parent);
	}
}
