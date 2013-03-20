package vc.todo.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import vc.todo.model.Todo;

import com.vaadin.Application;

public class OpenTodo {
	
	@Execute
	public void execute(Application vaadinApp,
			MWindow win, EModelService modelService, Todo todo) {
//		vaadinApp.getMainWindow().showNotification("Vaadin E4 Application");
		MPartStack find = (MPartStack) modelService.find("vc.todo.partstack.right", win);
		MPart p = MBasicFactory.INSTANCE.createPart();
		p.setLabel(todo.getTitle());
		p.setContributionURI("bundleclass://vc.todo/vc.todo.views.SecondView");
		find.getChildren().add(p);
	}

}
