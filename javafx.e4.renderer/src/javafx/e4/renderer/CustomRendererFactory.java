package javafx.e4.renderer;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;

import at.bestsolution.efxclipse.runtime.workbench.renderers.base.BaseWindowRenderer;
import at.bestsolution.efxclipse.runtime.workbench.renderers.fx.DefWorkbenchRendererFactory;

@SuppressWarnings("restriction")
public class CustomRendererFactory extends DefWorkbenchRendererFactory {

	@Inject
	public CustomRendererFactory(IEclipseContext context) {
		super(context);
	}

	@Override
	protected Class<? extends BaseWindowRenderer<?>> getWindowRendererClass() {
		return CustomStageRenderer.class;
	}
}