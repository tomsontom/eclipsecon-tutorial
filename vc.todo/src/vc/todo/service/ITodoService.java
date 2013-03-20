package vc.todo.service;

import java.util.List;

import vc.todo.model.Todo;

public interface ITodoService {
	
	public List<Todo> getTodos();
	
	public void saveTodo(Todo todo);
	
	public void createTodo(Todo todo);

}
