package simple.swt.app;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class MyPart {
	@Inject
	void init(Composite parent, final MWindow window) {
		parent.setLayout(new GridLayout(3,false));
		
		{
			{
				Label l = new Label(parent, SWT.NONE);
				l.setText("Width");
			}
			{
				Label l = new Label(parent, SWT.NONE);
				l.setText("Height");
			}
			{
				new Label(parent, SWT.NONE);
			}
		}
		
		{
			final Text w;
			final Text h;
			{
				w = new Text(parent, SWT.BORDER);
				w.setText(window.getWidth()+"");
			}
			{
				h = new Text(parent, SWT.BORDER);
				h.setText(window.getHeight()+"");
			}
			
			Button b = new Button(parent, SWT.PUSH);
			b.setText("Apply");
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					window.setWidth(Integer.parseInt(w.getText()));
					window.setHeight(Integer.parseInt(h.getText()));
				}
			});
		}		
	}

}
