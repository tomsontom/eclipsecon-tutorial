package vc.todo.views;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import vc.todo.model.Todo;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.Button.ClickEvent;

public class SecondView {
	@Inject
	public SecondView(ComponentContainer parent, @Optional Todo todo) {
		if (!(todo == null)) {
			final Form form = new Form();
			form.setImmediate(true);
			form.setItemDataSource(new BeanItem<Todo>(todo));
			parent.addComponent(form);
		}
	}
}
