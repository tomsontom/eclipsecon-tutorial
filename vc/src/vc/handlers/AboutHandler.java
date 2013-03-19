package vc.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.vaadin.Application;

public class AboutHandler {
	@Execute
	public void execute(Application vaadinApp, MPerspective perspective, EModelService modelService) {
		vaadinApp.getMainWindow().showNotification("This handler will add a part");
		
//		MMenu menu = MMenuFactory.INSTANCE.createMenu();
//		menu.setLabel("Cool menu");
//		MDirectMenuItem menuItem = MMenuFactory.INSTANCE.createDirectMenuItem();
//		menuItem.setLabel("Cool menu item");
//		menuItem.setContributionURI("bundleclass://vc/vc.menus.CoolMenuItem");
//		
//		menu.getChildren().add(menuItem);
//		win.getMainMenu().getChildren().add(menu);
		
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel("This is a cool part");
		part.setContributionURI("bundleclass://vc/vc.parts.CoolPart");
		MPartStack stack = (MPartStack) modelService.find("vc.partstack.left",perspective);
		stack.getChildren().add(part);
		stack.getParent().getChildren().get(1).setToBeRendered(false);
		
	
	}
}
