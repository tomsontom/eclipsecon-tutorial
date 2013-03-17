 
package parts;


import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Part2 {
	
	
	private Label label;



	@Inject
	public Part2(Composite parent) {
		label = new Label(parent, SWT.NONE);
		label.setText("Label in the second part");
	}
	
	
	
	@Focus
	public void onFocus() {
		label.setFocus();
	}
	
	
}