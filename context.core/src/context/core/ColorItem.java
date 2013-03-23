package context.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class ColorItem {
	private RGB rgb;
	private Composite comp;
	
	@Inject
	private Point size;
	
	@Inject
	private ColorChooserAction action;
	
	@PostConstruct
	void create(Composite parent, final IEclipseContext context) {
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
		comp.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				//TODO Lab1: Decactivate the context
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				//TODO Lab1: Activate the current context branch
			}
		});
		comp.setBackground(new Color(comp.getDisplay(),rgb));
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				selectColor(context);
			}
		});
	}
	
	void selectColor(IEclipseContext context) {
		RGB rgb = null;
		//TODO Lab1: Invoke the method annotated with @Execute on the action-Object
		if( rgb != null ) {
			context.set(RGB.class, rgb);
		}
	}
	
	@Inject
	public void setRGB(RGB rgb) {
		this.rgb = rgb;
		if( comp != null ) {
			if( comp.getBackground() != null ) {
				comp.getBackground().dispose();
			}
			comp.setBackground(new Color(comp.getDisplay(),rgb));
		}
	}
}
