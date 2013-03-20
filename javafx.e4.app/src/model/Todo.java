package model;

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Todo {

	public enum Repeat {
		NEVER, DAILY, WEEKLY, BI_WEEKLY, MONTHLY, YEARLY
	}

	private StringProperty title = new SimpleStringProperty(this, "title");
	private StringProperty extraInfo = new SimpleStringProperty(this,
			"extraInfo");

	private BooleanProperty hasDate = new SimpleBooleanProperty(this, "hasDate");
	private ObjectProperty<Date> date = new SimpleObjectProperty<Date>(this,
			"date");

	private ObjectProperty<Repeat> repeat = new SimpleObjectProperty<Repeat>(this,
			"repeat", Repeat.NEVER);
	private ObjectProperty<Date> endDate = new SimpleObjectProperty<Date>(this,
			"endDate");

	public Todo() {
		this(null);
	}

	public Todo(String title) {
		this(title, null);
	}

	public Todo(String title, Date date) {
		this.title.set(title);
		this.date.set(date);
		this.hasDate.set(date != null);
		this.hasDate.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue && Todo.this.date.get() == null) {
					Todo.this.date.set(new Date());
				}
			}
		});
	}

	// -----------------------------------
	public void setTitle(String title) {
		this.title.set(title);
	}

	public String getTitle() {
		return this.title.get();
	}

	public StringProperty titleProperty() {
		return this.title;
	}

	// -----------------------------------
	public void setExtraInfo(String extraInfo) {
		this.extraInfo.set(extraInfo);
	}

	public String getExtraInfo() {
		return this.extraInfo.get();
	}

	public StringProperty extraInfoProperty() {
		return this.extraInfo;
	}

	// -----------------------------------
	public void setHasDate(boolean hasDate) {
		this.hasDate.set(hasDate);
	}

	public boolean isHasDate() {
		return this.hasDate.get();
	}

	public BooleanProperty hasDateProperty() {
		return this.hasDate;
	}

	// -----------------------------------
	public void setDate(Date date) {
		this.date.set(date);
	}

	public Date getDate() {
		return this.date.get();
	}

	public ObjectProperty<Date> dateProperty() {
		return this.date;
	}
	
	// -----------------------------------
	public void setRepeat(Repeat repeat) {
		this.repeat.set(repeat);
	}
	
	public Repeat getRepeat() {
		return this.repeat.get();
	}
	
	public ObjectProperty<Repeat> repeatProperty() {
		return this.repeat;
	}

	// -----------------------------------
	public void setEndDate(Date endDate) {
		this.endDate.set(endDate);
	}

	public Date getEndDate() {
		return this.endDate.get();
	}

	public ObjectProperty<Date> endDateProperty() {
		return this.endDate;
	}
}
