package renderer;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

@SuppressWarnings("restriction")
public class CustomRendererFactory extends WorkbenchRendererFactory {
	private PGroupContributedPartRenderer pgroupRenderer;
	
	@Override
	public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
		if( uiElement instanceof MPart ) {
			if( pgroupRenderer == null ) {
				pgroupRenderer = new PGroupContributedPartRenderer();
				initRenderer(pgroupRenderer);
			}
			return pgroupRenderer;
		}
		return super.getRenderer(uiElement, parent);
	}
}
