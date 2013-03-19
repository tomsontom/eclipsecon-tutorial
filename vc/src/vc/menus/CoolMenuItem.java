package vc.menus;

import org.eclipse.e4.core.di.annotations.Execute;

public class CoolMenuItem {
	
	@Execute
	public void execute(){
		System.err.println("cool menu in action");
	}

}
