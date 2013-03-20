package vc.todo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vc.todo.model.Todo;

public class TodoServiceProvider implements ITodoService {

	List<Todo> todos = new ArrayList<Todo>();
	
	public TodoServiceProvider() {
		todos.add(new Todo("Title 1", "Pick up the milk", new Date(), new Date()));
		todos.add(new Todo("Title 2", "Don't forget the beer", new Date(), new Date()));
		todos.add(new Todo("Title 3", "Remember another thing", new Date(), new Date()));
	}
	
	@Override
	public List<Todo> getTodos() {
		return todos;
	}

	@Override
	public void saveTodo(Todo todo) {
//		if (todos.contains(todo))
//			todo.
		
	}

	@Override
	public void createTodo(Todo todo) {
		todos.add(todo);
		
	}

}
