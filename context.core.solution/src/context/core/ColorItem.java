package context.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
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
	
	private IEclipseContext context;
	
	@PostConstruct
	void create(Composite parent, final IEclipseContext context) {
		this.context = context;
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
				context.deactivate();
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				context.activateBranch();
			}
		});
		comp.setBackground(new Color(comp.getDisplay(),rgb));
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				selectColor();
			}
		});
	}
	
	void selectColor() {
		
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
