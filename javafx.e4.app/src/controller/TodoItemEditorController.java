package controller;

import javax.inject.Inject;

import model.Todo;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
@Creatable
public class TodoItemEditorController {
	private Todo staticItem = new Todo();
	private Todo currentItem;
	
	private final EPartService partService;
	
	private final MApplication application;
	
	private final EModelService modelService;
	
	@Inject
	public TodoItemEditorController(EPartService partService, EModelService modelService, MApplication application) {
		this.partService = partService;
		this.modelService = modelService;
		this.application = application;
	}
	
	@Inject
	public void setTodo(Todo todo) {
		this.currentItem = todo;
		
		staticItem.setTitle(todo.getTitle());
		staticItem.setExtraInfo(todo.getExtraInfo());
		staticItem.setDate(todo.getDate());
		staticItem.setHasDate(todo.isHasDate());
		staticItem.setEndDate(todo.getEndDate());
		staticItem.setRepeat(todo.getRepeat());
	}
	
	public Todo getItem() {
		return staticItem;
	}

	public void cancelEdit() {
		setTodo(currentItem);
		switchBack();
	}

	public void applyEdit() {
		currentItem.setTitle(staticItem.getTitle());
		currentItem.setExtraInfo(staticItem.getExtraInfo());
		currentItem.setDate(staticItem.getDate());
		currentItem.setHasDate(staticItem.isHasDate());
		currentItem.setEndDate(staticItem.getEndDate());
		currentItem.setRepeat(staticItem.getRepeat());
		switchBack();
	}
	
	private void switchBack() {
		partService.switchPerspective((MPerspective) modelService.find("javafx.e4.app.perspective.list", application));
	}
}