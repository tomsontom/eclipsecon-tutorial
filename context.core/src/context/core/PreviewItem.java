package context.core;

import javax.inject.Inject;

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
	
	//TODO Lab1: Get the current selected rgb value based on the active-context
	void currentColor(RGB rgb) {
		if( comp.isDisposed() ) {
			return;
		}
		
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
