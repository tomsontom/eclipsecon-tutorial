 
package cf.demo.app.parts;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SpecialPart {
	
	
	private Label label;



	@Inject
	public SpecialPart(Composite parent) {
		label = new Label(parent, SWT.NONE);
		label.setText("This is a label in the special part");
	}
	
	
	
	@Focus
	public void onFocus() {
		
		label.setFocus();
		
	}
	
	
}