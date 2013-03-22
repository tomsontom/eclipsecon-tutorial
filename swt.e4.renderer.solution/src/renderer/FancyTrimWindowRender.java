package renderer;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.renderers.swt.SWTPartRenderer;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class FancyTrimWindowRender extends SWTPartRenderer {
	private Image closeImage;
	
	@Inject
	Display display;
	
	static void loadPath(Region region, float[] points, byte[] types) {
		int start = 0, end = 0;
		for (int i = 0; i < types.length; i++) {
			switch (types[i]) {
				case SWT.PATH_MOVE_TO: {
					if (start != end) {
						int n = 0;
						int[] temp = new int[end - start];
						for (int k = start; k < end; k++) {
							temp[n++] = Math.round(points[k]);
						}
						region.add(temp);
					}
					start = end;
					end += 2;
					break;
				}
				case SWT.PATH_LINE_TO: {
					end += 2;
					break;
				}
				case SWT.PATH_CLOSE: {
					if (start != end) {
						int n = 0;
						int[] temp = new int[end - start];
						for (int k = start; k < end; k++) {
							temp[n++] = Math.round(points[k]);
						}
						region.add(temp);
					}
					start = end;
					break;
				}
			}
		}
	}
	
		
	public static Path createRoundedPath(Display d, int x, int y, int w, int h, int r1, int r2, int r3, int r4) {
		Path path = new Path(d);
		path.moveTo(x,r1+y);
		path.quadTo(x,y, x+r1,y);
		path.lineTo(x+w-r2,y);
		path.quadTo(x+w,y, x+w,y+r2);
		path.lineTo(x+w,y+h-r3);
		path.quadTo(x+w,y+h, x+w-r3,y+h);
		path.lineTo(x+r4,y+h);
		path.quadTo(x,y+h, x,y+h-r4);
		path.close();
		return path;
	}
	
	private void initializeImages() {
		if( closeImage == null ) {
			try(
					InputStream in = getClass().getClassLoader().getResourceAsStream("icons/system-shutdown.png")) {
				closeImage = new Image(display, in);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Object createWidget(MUIElement element, Object parent) {
		MWindow window = (MWindow) element;
		Shell s = new Shell(SWT.NO_TRIM);
		s.setLayout(GridLayoutFactory.fillDefaults().create());
		s.setText(window.getLocalizedLabel());
		s.setBackgroundMode(SWT.INHERIT_DEFAULT);
		s.setSize(window.getWidth(),window.getHeight());
		
		Path path = createRoundedPath(s.getDisplay(), 0, 0, window.getWidth(), window.getHeight(), 10, 10, 10, 10);
		Path path2 = new Path(s.getDisplay(), path, 0.1f);
		path.dispose();
		PathData data = path2.getPathData();
		path2.dispose();
		Region region = new Region(s.getDisplay());
		loadPath(region, data.points, data.types);
		s.setRegion(region);
		
		Control c = createTrimControl(s,s);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint=30;
		c.setLayoutData(gd);
		
		Composite contentArea = new Composite(s,SWT.NONE);
		contentArea.setLayout(new FillLayout());
		contentArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		bindWidget(window, s);
		
		return s;
	}
	
	private Control createTrimControl(Composite parent, final Shell s) {
		initializeImages();
		final Canvas c = new Canvas(parent,SWT.DOUBLE_BUFFERED);
		c.setBackground(c.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		c.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				Point s = c.getSize();
				if( closeImage != null ) {
					int x = s.x - closeImage.getBounds().width - 5;
					e.gc.drawImage(closeImage, x, s.y / 2 - closeImage.getBounds().height/2);
				}				
			}
		});
		
		Listener l = new Listener() {
			Point origin;
			public void handleEvent(Event e) {
				switch (e.type) {
					case SWT.MouseDown:
						origin = new Point(e.x, e.y);
						break;
					case SWT.MouseUp:
						origin = null;
						break;
					case SWT.MouseMove:
						if (origin != null) {
							Point p = e.widget.getDisplay().map(c, null, e.x, e.y);
							s.setLocation(p.x - origin.x, p.y - origin.y);
						}
						break;
				}
			}
		};
		c.addListener(SWT.MouseDown, l);
		c.addListener(SWT.MouseUp, l);
		c.addListener(SWT.MouseMove, l);
		
		return c;
	}
	
	@Override
	public Object getUIContainer(MUIElement element) {
		Shell s = (Shell) element.getParent().getWidget();
		System.err.println("PARENT: " + s.getChildren()[1]); 
		return s.getChildren()[1];
	}
	
	@Override
	public void postProcess(MUIElement childElement) {
		super.postProcess(childElement);
		Shell s = (Shell) childElement.getWidget();
		s.open();
	}
}
