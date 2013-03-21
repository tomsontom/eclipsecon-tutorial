package swt.e4.app.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import swt.e4.app.controller.TodoListViewController;
import swt.e4.app.model.Todo;

@SuppressWarnings("restriction")
public class TodoListView {
	private static final DateFormat FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
	private static final int LINE_SPACING = 5;
	
	@Inject
	TodoListViewController controller;
	
	private Image nextIcon;
	private Image nextIconActive;
	private Font titleFont;
	private Font dateFont;
	private TableViewer viewer;
	
	private Todo currentOver;
	
	private void initResources(Display display) {
		nextIcon = new Image(display, getClass().getClassLoader().getResourceAsStream("icons/next.png"));
		nextIconActive = new Image(display, getClass().getClassLoader().getResourceAsStream("icons/next_hover.png"));
		FontData data = display.getSystemFont().getFontData()[0];
		titleFont = new Font(display, data.name, 15, SWT.BOLD);
		dateFont = new Font(display,data.name,10,SWT.NONE);
	}
	
	@PostConstruct
	void init(Composite parent) {
		initResources(parent.getDisplay());
		
		FillLayout l = new FillLayout();
		parent.setLayout(l);
		
		viewer = new TableViewer(parent,SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION);
		viewer.setContentProvider(new ObservableListContentProvider());
		viewer.setLabelProvider(new OwnerDrawLabelProvider() {
			
			@Override
			protected void paint(Event event, Object element) {
				Todo item = (Todo) element;
				
				event.gc.setFont(titleFont);
				int textHeight = event.gc.textExtent(item.getTitle()).y + LINE_SPACING;
				event.gc.drawText(item.getTitle(), event.x+10, event.y+10, true);
				
				event.gc.setFont(dateFont);
				event.gc.drawText(getDate(item), event.x+10, event.y+textHeight+10,true);
				
				int x = viewer.getTable().getBounds().width-nextIcon.getBounds().width-20;
				int y = event.y + event.height/2 - nextIcon.getBounds().height/2;
				if( currentOver == element ) {
					event.gc.drawImage(nextIconActive, x, y);
				} else {
					event.gc.drawImage(nextIcon, x, y);
				}
			}
			
			@Override
			protected void measure(Event event, Object element) {
				Todo item = (Todo) element;
				if( item == null ) {
					return;
				}
				event.gc.setFont(titleFont);
				int y = event.gc.textExtent(item.getTitle()).y + LINE_SPACING;
				
				event.gc.setFont(dateFont);
				y+=event.gc.textExtent(getDate(item)).y;
				
				event.height = y + 20;
			}
			
			private String getDate(Todo item) {
				if( item.isHasDate() ) {
					if( item.getDate() != null ) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(item.getDate());
						
						Calendar now = Calendar.getInstance();
						Date nextDate = item.getDate();
						
						if( cal.before(now) ) {
							int field = Calendar.DATE;
							int amout = 1;
									
							switch (item.getRepeat()) {
							case NEVER:
								return FORMAT.format(nextDate);
							case DAILY:
								field = Calendar.DATE;
								break;
							case BI_WEEKLY:
								field = Calendar.WEEK_OF_YEAR;
								amout = 2;
								break;
							case MONTHLY:
								field = Calendar.MONTH;
								break;
							case WEEKLY:
								field = Calendar.WEEK_OF_YEAR;
								break;
							case YEARLY:
								field = Calendar.YEAR;
								break;
							}
							
							cal.add(field, amout);
							if( cal.before(now) ) {
								cal.add(field, amout);
							}
							
							if( item.getEndDate() != null && cal.before(item.getEndDate()) ) {
								return "";
							}
							
							nextDate = cal.getTime();
						}
						
						return FORMAT.format(nextDate);
					}
				}
				return "";
			}
		});
		viewer.setInput(controller.getItems());
		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				ViewerCell c = viewer.getCell(new Point(e.x, e.y));
				if( c != null ) {
					if( isImageArea(c.getBounds().y, c.getBounds().height, e) ) {
						controller.openDetail((Todo) c.getElement());	
					}
				}
			}
			
		});
		viewer.getControl().addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				ViewerCell c = viewer.getCell(new Point(e.x, e.y));
				if( c != null ) {
					if( isImageArea(c.getBounds().y, c.getBounds().height, e) ) {
						if( currentOver != c.getElement() ) {
							currentOver = (Todo) c.getElement();
							viewer.getControl().redraw();
						}
					} else {
						if( currentOver != null ) {
							currentOver = null;
							viewer.getControl().redraw();
						}
					}
				}
			}
		});
		viewer.getControl().addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				currentOver = null;
				viewer.getControl().redraw();
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				
			}
		});
		
	}
	
	private boolean isImageArea(int y, int height, MouseEvent event) {
		int x = viewer.getTable().getBounds().width-nextIcon.getBounds().width-20;
		y = y + height/2 - nextIcon.getBounds().height/2;
		Rectangle rect = new Rectangle(x, y, nextIcon.getBounds().width, nextIcon.getBounds().height);
		return rect.contains(event.x,event.y);
	}
	
	@Focus
	void focus() {
		viewer.getControl().setFocus();
	}
	
	@PreDestroy
	void cleanup() {
		if( nextIcon != null ) {
			nextIcon.dispose();
		}
	}
	
	public TodoListViewController getController() {
		return controller;
	}
}
