package addon;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import at.bestsolution.animationutils.pagetransition.ACenterSwitchAnimation;
import at.bestsolution.animationutils.pagetransition.animation.PageChangeAnimation;
import at.bestsolution.animationutils.pagetransition.animation.SlideAnimation;
import at.bestsolution.efxclipse.runtime.workbench.renderers.base.services.PerspectiveTransitionService;

@SuppressWarnings("restriction")
public class AnimationAddon {
	@PostConstruct
	void init(IEclipseContext context) {
		TransitionImpl impl = ContextInjectionFactory.make(TransitionImpl.class, context);
		context.set(PerspectiveTransitionService.class, impl);
		context.set(TransitionImpl.class, impl);
	}
	
	static class TransitionImpl implements PerspectiveTransitionService<BorderPane, Node> {
		private ACenterSwitchAnimation openDetail = new PageChangeAnimation();
		private ACenterSwitchAnimation backToList = new SlideAnimation()/*new ZoomSlideAnimation()*/;
		
		@Override
		public at.bestsolution.efxclipse.runtime.workbench.renderers.base.services.PerspectiveTransitionService.AnimationDelegate<BorderPane, Node> getDelegate(
				MPerspective fromPerspective, MPerspective toPerspective) {
			if( "javafx.e4.app.perspective.list".equals(toPerspective.getElementId()) ) {
				return new AnimationDelegate<BorderPane, Node>() {
					
					@Override
					public void animate(BorderPane container, Node control) {
						backToList.animate(container, control);
					}
				};
			} else if( "javafx.e4.app.perspective.detail".equals(toPerspective.getElementId()) ) {
				return new AnimationDelegate<BorderPane, Node>() {
					
					@Override
					public void animate(BorderPane container, Node control) {
						openDetail.animate(container, control);
					}
				};
			}
			return null;
		}
		
	}
}
