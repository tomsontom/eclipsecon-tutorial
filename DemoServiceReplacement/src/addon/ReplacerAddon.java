package addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import better.BetterImpl;

public class ReplacerAddon {
	
	@PostConstruct
	public void pc(MApplication app){
		System.err.println("Trying to make the exchange");
		IEclipseContext context = app.getContext();
		EModelService ems = context.get(EModelService.class);
		if (ems==null) {
			System.err.println("WHOAAA");
			return ;
		}
		context.set(EModelService.class, new BetterImpl(context));
		System.err.println("Exchange made");
	}
}
