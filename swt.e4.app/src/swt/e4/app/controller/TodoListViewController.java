package swt.e4.app.controller;

import java.util.Date;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import swt.e4.app.model.Todo;

@SuppressWarnings("restriction")
@Creatable
public class TodoListViewController {
	private ObservableList items;
	
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
		partService.switchPerspective((MPerspective) modelService.find("swt.e4.app.perspective.detail", application));
	}
	
	public ObservableList getItems() {
		if( items == null ) {
			items = new WritableList();
			items.add(new Todo("Pick up milk", new Date()));
			items.add(new Todo("Don't forget the beer"));
			items.add(new Todo("Attend Eclipse/JavaFX BoF"));
		}
		return items;
	}
	
	public void createNewItem() {
		Todo item = new Todo("New Todo");
		getItems().add(item);
		openDetail(item);
	}
}
