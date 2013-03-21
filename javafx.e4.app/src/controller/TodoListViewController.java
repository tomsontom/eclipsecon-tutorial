package controller;

import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;

import model.Todo;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

@SuppressWarnings("restriction")
@Creatable
public class TodoListViewController {
	private ObservableList<Todo> items;
	
	private final EPartService partService;
	
	private final MApplication application;
	
	private final EModelService modelService;
	
	@Inject
	public TodoListViewController(EPartService partService, EModelService modelService, MApplication application) {
		this.partService = partService;
		this.modelService = modelService;
		this.application = application;
	}
	
	public void openDetail(Todo item) {
		application.getContext().set(Todo.class, item);
		partService.switchPerspective((MPerspective) modelService.find("javafx.e4.app.perspective.detail", application));
	}
	
	public ObservableList<Todo> getItems() {
		if( items == null ) {
			items = FXCollections.observableArrayList(new Todo("Go shopping", new Date()),new Todo("TODO 1"),new Todo("TODO 2"));	
		}

		return items;
	}
	
	public void createNewItem() {
		Todo item = new Todo("New Todo");
		getItems().add(item);
		openDetail(item);
	}
}
