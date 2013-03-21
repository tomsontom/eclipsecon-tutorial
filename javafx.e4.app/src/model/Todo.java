package model;

import java.util.Date;
import java.util.UUID;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import service.TodoDataService.Repeat;

public class Todo {

	private String uid = UUID.randomUUID().toString();
	
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
	
	public static Todo create(String id) {
		Todo t = new Todo();
		t.uid = id;
		return t;
	}
	
	public String getId() {
		return uid;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todo other = (Todo) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
}
