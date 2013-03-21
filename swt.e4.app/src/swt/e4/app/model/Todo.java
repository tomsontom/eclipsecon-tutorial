package swt.e4.app.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

public class Todo {
	public enum Repeat {
		NEVER, DAILY, WEEKLY, BI_WEEKLY, MONTHLY, YEARLY
	}
	
	private String title;
	private String extraInfo;
	private boolean hasDate;
	private Date date;
	private Repeat repeat = Repeat.NEVER;
	private Date endDate;
	
	private PropertyChangeSupport changesupport = new PropertyChangeSupport(this);
	
	public Todo() {
		this(null);
	}

	public Todo(String title) {
		this(title, null);
	}

	public Todo(String title, Date date) {
		this.title = title;
		this.date = date;
		this.hasDate = date != null;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changesupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changesupport.removePropertyChangeListener(listener);
	}
	
	private void fire(String name, Object oldValue, Object newValue) {
		changesupport.firePropertyChange(name, oldValue, newValue);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		fire("title",this.title,this.title = title);
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		fire("extraInfo",this.extraInfo,this.extraInfo = extraInfo);
	}
	public boolean isHasDate() {
		return hasDate;
	}
	public void setHasDate(boolean hasDate) {
		fire("hasDate",this.hasDate,this.hasDate = hasDate);
		if( hasDate && date == null ) {
			setDate(new Date());
		}
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		fire("date",this.date,this.date = date);
	}
	public Repeat getRepeat() {
		return repeat;
	}
	public void setRepeat(Repeat repeat) {
		fire("repeat",this.repeat,this.repeat = repeat);
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		fire("endDate",this.endDate,this.endDate = endDate);
	}
}
