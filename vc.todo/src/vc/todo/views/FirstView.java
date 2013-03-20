package vc.todo.views;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import vc.todo.model.Todo;
import vc.todo.service.ITodoService;
import vc.todo.service.TodoServiceProvider;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

public class FirstView
{
	private Table todoList;

	@Inject
	public FirstView(ComponentContainer parent, final MPerspective p)
	{
		ITodoService provider = new TodoServiceProvider();
        todoList = new Table("List of todos");
        todoList.setSelectable(true);
        BeanItemContainer<Todo> bic = new BeanItemContainer<Todo>(Todo.class);
		todoList.setContainerDataSource(bic);
		todoList.setItemCaptionPropertyId("title");
        List<Todo> todos = provider.getTodos();
        for (Todo todo : todos) {
			bic.addBean(todo);
		}
        parent.addComponent(todoList);
        todoList.setMultiSelect(false);
        todoList.setImmediate(true);
        todoList.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				p.getContext().set(Todo.class.getName(), todoList.getValue());				
			}
		});
        Button c = new Button("Refresh");
        c.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				todoList.setContainerDataSource(todoList.getContainerDataSource());
			}
		});
		parent.addComponent(c);
	}
	
	
	
}
