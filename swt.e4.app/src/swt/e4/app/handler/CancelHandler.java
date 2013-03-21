package swt.e4.app.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import swt.e4.app.views.TodoItemEditor;

@SuppressWarnings("restriction")
public class CancelHandler {
	@Execute
	void cancel(MPart part) {
		((TodoItemEditor)part.getObject()).getController().cancelEdit();
	}
}
