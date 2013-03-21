package controller;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;

import model.Todo;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import service.TodoDataService;
import service.TodoDataService.Callback;
import service.TodoDataService.TodoItem;

@SuppressWarnings("restriction")
@Creatable
public class TodoListViewController {
	private ObservableList<Todo> items;
	
	private final EPartService partService;
	
	private final MApplication application;
	
	private final EModelService modelService;
	
	private final TodoDataService dataService;
	
	@Inject
	public TodoListViewController(EPartService partService, EModelService modelService, MApplication application, TodoDataService dataService) {
		this.partService = partService;
		this.modelService = modelService;
		this.application = application;
		this.dataService = dataService;
	}
	
	public void openDetail(Todo item) {
		application.getContext().set(Todo.class, item);
		partService.switchPerspective((MPerspective) modelService.find("javafx.e4.app.perspective.detail", application));
	}
	
	public ObservableList<Todo> getItems() {
		if( items == null ) {
			items = FXCollections.observableArrayList();
			dataService.loadItems(new Callback<List<TodoItem>>() {
				
				@Override
				public void call(final List<TodoItem> list) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							for( TodoItem i : list ) {
								Todo t = createItem(i);
								if( ! items.contains(t) ) {
									items.add(t);	
								}
							}
						}
					});
				}
			});
		}

		return items;
	}
	
	private Todo createItem(TodoItem item) {
		Todo t = Todo.create(item.id);
		t.setTitle(item.title);
		t.setExtraInfo(item.extraInfo);
		t.setDate(item.date);
		t.setHasDate(item.hasDate);
		t.setEndDate(item.endDate);
		t.setRepeat(item.repeat);
		return t;
	}
	
	public void createNewItem() {
		Todo item = new Todo("New Todo");
		getItems().add(item);
		openDetail(item);
	}
}
