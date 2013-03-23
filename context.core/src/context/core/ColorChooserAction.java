package context.core;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
@Creatable	
@Singleton
public class ColorChooserAction {
	
	//TODO Lab1: Allow the action to get invoked using @Execute
	private RGB run(Shell shell) {
		ColorDialog dialog = new ColorDialog(shell);
		return dialog.open();
	}
}
