package controller;

import model.Todo;
import service.TodoDataService.TodoItem;

public class Util {
	public static TodoItem toDTO(Todo item) {
		return new TodoItem(item.getId(), item.getTitle(), item.getExtraInfo(), item.isHasDate(), item.getDate(), item.getRepeat(), item.getEndDate());
	}
	
	public static Todo fromDTO(TodoItem item) {
		Todo t = Todo.create(item.id);
		updateData(t, item);
		return t;
	}
	
	public static void updateData(Todo t, TodoItem item) {
		t.setTitle(item.title);
		t.setExtraInfo(item.extraInfo);
		t.setDate(item.date);
		t.setHasDate(item.hasDate);
		t.setEndDate(item.endDate);
		t.setRepeat(item.repeat);		
	}

}
