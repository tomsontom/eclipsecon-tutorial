package service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TodoDataService {
	public enum Repeat {
		NEVER, DAILY, WEEKLY, BI_WEEKLY, MONTHLY, YEARLY
	}
	
	public class TodoItem {
		private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		public final String id;
		public final String title;
		public final String extraInfo;
		public final boolean hasDate;
		public final Date date;
		public final Repeat repeat;
		public final Date endDate;
		
		public TodoItem(String title, String isoDate) throws ParseException {
			this(UUID.randomUUID().toString(), title, null, isoDate != null, FORMAT.parse(isoDate), Repeat.NEVER, null);
		}
		
		public TodoItem(String id, String title, String extraInfo, boolean hasDate, Date date, Repeat repeat, Date endDate) {
			this.id = id;
			this.title = title;
			this.extraInfo = extraInfo;
			this.hasDate = hasDate;
			this.date = date;
			this.repeat = repeat;
			this.endDate = endDate;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
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
			TodoItem other = (TodoItem) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
	
	public interface Callback<T> {
		public void call(T t);
	}
	
	public void setItemRemoved(Callback<TodoItem> callback);
	public void setItemAddedCallback(Callback<TodoItem> callback);
	public void setItemModifiedCallback(Callback<TodoItem> callback);
	public void loadItems(Callback<List<TodoItem>> callback);
	public void saveItem(TodoItem item, Callback<Void> callback);
	public void deleteItem(TodoItem item, Callback<Void> callback);
}
