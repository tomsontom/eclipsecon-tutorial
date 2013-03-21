package service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import service.TodoDataService;

public class MemoryTodoItemService implements TodoDataService {
	private Vector<TodoItem> items = new Vector<>();
	
	private Callback<TodoItem> removeCallback;
	private Callback<TodoItem> addCallback;
	private Callback<TodoItem> modifiedCallback;
	
	public MemoryTodoItemService() {
		try {
			items.add(new TodoItem("e4 Tutorial","2013-03-25 13:00"));
			items.add(new TodoItem("e(fx)clipse BoF","2013-03-25 16:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setItemRemoved(Callback<TodoItem> callback) {
		this.removeCallback = callback;
	}

	@Override
	public void setItemAddedCallback(Callback<TodoItem> callback) {
		this.addCallback = callback;
	}
	
	@Override
	public void setItemModifiedCallback(Callback<TodoItem> callback) {
		this.modifiedCallback = callback;
	}

	@Override
	public void loadItems(Callback<List<TodoItem>> callback) {
		callback.call(items);
	}

	@Override
	public void saveItem(TodoItem item, Callback<Void> callback) {
		if( ! items.contains(item) ) {
			items.add(item);
		}
		callback.call(null);
	}

	@Override
	public void deleteItem(TodoItem item, Callback<Void> callback) {
		items.remove(item);
		callback.call(null);
	}

}
