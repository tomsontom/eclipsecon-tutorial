package swt.e4.app.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import swt.e4.app.views.TodoListView;

@SuppressWarnings("restriction")
public class NewHandler {
	@Execute
	void cancel(MPart part) {
		((TodoListView)part.getObject()).getController().createNewItem();
	}
}
