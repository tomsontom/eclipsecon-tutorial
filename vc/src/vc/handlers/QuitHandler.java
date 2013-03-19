package vc.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;

public class QuitHandler {
	
	@Execute
	public void execute(MWindow win)
	{		
		MMenu mMenuElement = (MMenu) win.getMainMenu().getChildren().get(1);
		mMenuElement.setToBeRendered(false);
//		workbench.close();
	}
}
