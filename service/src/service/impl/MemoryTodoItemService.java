package service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import service.TodoDataService;

public class MemoryTodoItemService implements TodoDataService {
	private Vector<TodoItem> items = new Vector<>();
	
	private List<Callback<TodoItem>> removeCallback = new ArrayList<>();
	private List<Callback<TodoItem>> modifiedCallback = new ArrayList<>();
	
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
	public void addItemRemoved(Callback<TodoItem> callback) {
		this.removeCallback.add(callback);
	}

	@Override
	public void addItemModifiedCallback(Callback<TodoItem> callback) {
		this.modifiedCallback.add(callback);
	}

	@Override
	public void loadItems(Callback<List<TodoItem>> callback) {
		callback.call(items);
	}

	@Override
	public void saveItem(TodoItem item) {
		if( ! items.contains(item) ) {
			items.add(item);
		}
	}

	@Override
	public void deleteItem(TodoItem item) {
		items.remove(item);
	}

}
