package vc.views;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;

public class FirstView
{
	@Inject
	public FirstView(ComponentContainer parent, IEclipseContext con, MTrimmedWindow win)
	{
		
		Label label = new Label("A vaadin label");
		parent.addComponent(label);
        
        
	}
}
