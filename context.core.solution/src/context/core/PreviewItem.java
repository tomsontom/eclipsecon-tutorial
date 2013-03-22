package context.core;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class PreviewItem {
	private Composite comp;

	@Inject
	public PreviewItem(Composite parent, final Point size) {
		comp = new Composite(parent,SWT.BORDER) {
			@Override
			public Point computeSize(int wHint, int hHint) {
				return size;
			}
			
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				return size;
			}
		};
	}
	
	@Inject
	void currentColor(@Active @Optional RGB rgb) {
		if( comp.getBackground() != null ) {
			comp.getBackground().dispose();
		}
		if( rgb != null ) {
			comp.setBackground(new Color(comp.getDisplay(), rgb));
		} else {
			comp.setBackground(null);
		}
	}
}
