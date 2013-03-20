package vc.todo.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import vc.todo.model.Todo;

public class SaveTodo {
	
	@Execute
	public void save(Todo todo){
		System.err.println(todo.getTitle());
	}

}
