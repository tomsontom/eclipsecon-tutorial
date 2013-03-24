package javafx.e4.renderer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindowElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.UIEvents;

import at.bestsolution.efxclipse.runtime.panels.FillLayoutPane;
import at.bestsolution.efxclipse.runtime.services.theme.Theme;
import at.bestsolution.efxclipse.runtime.services.theme.ThemeManager;
import at.bestsolution.efxclipse.runtime.services.theme.ThemeManager.Registration;
import at.bestsolution.efxclipse.runtime.workbench.renderers.base.widget.WLayoutedWidget;
import at.bestsolution.efxclipse.runtime.workbench.renderers.base.widget.WWindow;
import at.bestsolution.efxclipse.runtime.workbench.renderers.fx.DefWindowRenderer;
import at.bestsolution.efxclipse.runtime.workbench.renderers.fx.widget.WLayoutedWidgetImpl;

@SuppressWarnings("restriction")
public class CustomStageRenderer extends DefWindowRenderer {

	@Override
	protected Class<? extends WWindow<Stage>> getWidgetClass(MWindow element) {
		//TODO Return the stage-widget class
		return MyStageWidget.class;
	}
	
	static class MyStageWidget extends WLayoutedWidgetImpl<Stage, BorderPane, MWindow> implements WWindow<Stage> {

		private BorderPane pane;
		private FillLayoutPane contentPane;
		private Stage stage;
		
		@Inject
		@Optional
		ThemeManager themeManager; 
		private Registration sceneRegistration;
		
		@Override
		protected BorderPane getWidgetNode() {
			return new BorderPane();
		}

		@Override
		protected Stage createWidget() {
			stage = null;//TODO create a stage instance using StageStyle.UNDECORATED;
			pane = new BorderPane();
			contentPane = new FillLayoutPane();
			pane.setCenter(contentPane);
			Scene s = new Scene(pane);
			stage.setScene(s);
			
			
			if (themeManager != null) {
				Theme theme = themeManager.getCurrentTheme();
				if (theme != null) {
					List<String> sUrls = new ArrayList<String>();
					for (URL url : theme.getStylesheetURL()) {
						sUrls.add(url.toExternalForm());
					}

					s.getStylesheets().addAll(sUrls);
				}
				sceneRegistration = themeManager.registerScene(s);
			}

			
			return stage;
		}
		
		@Override
		protected void doCleanup() {
			super.doCleanup();
			sceneRegistration.dispose();
		}
		
		@Override
		public void addChild(WLayoutedWidget<MWindowElement> widget) {
			//TODO implement appending of child at the end of the list
		}

		@Override
		public void addChild(int idx, WLayoutedWidget<MWindowElement> widget) {
			contentPane.getChildren().add(idx, (Node) widget.getStaticLayoutNode());
		}

		@Override
		public void removeChild(WLayoutedWidget<MWindowElement> widget) {
			contentPane.getChildren().remove(widget.getStaticLayoutNode());
		}

		@Override
		public void show() {
			stage.show();
		}

		@Override
		public void close() {
			stage.close();
		}
		
		@Inject
		void setWidth(@Named(UIEvents.Window.WIDTH) int width) {
			getWidget().setWidth(width);
		}
		
		@Inject
		void setHeight(@Named(UIEvents.Window.HEIGHT) int height) {
			getWidget().setWidth(height);
		}

		@Override
		public void setMainMenu(WLayoutedWidget<MMenu> menuWidget) {
		}

		@Override
		public void setTopTrim(WLayoutedWidget<MTrimBar> trimBar) {
		}

		@Override
		public void setLeftTrim(WLayoutedWidget<MTrimBar> trimBar) {
		}

		@Override
		public void setRightTrim(WLayoutedWidget<MTrimBar> trimBar) {
		}

		@Override
		public void setBottomTrim(WLayoutedWidget<MTrimBar> trimBar) {
		}
	}
}