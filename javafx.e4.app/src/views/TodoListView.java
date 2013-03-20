package views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import model.Todo;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import controller.TodoListViewController;

@SuppressWarnings("restriction")
public class TodoListView {
	private static final DateFormat FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
	
	@Inject
	TodoListViewController controller;

	@PostConstruct
	void initUI(MPart part, BorderPane parent) {
		ToolBar bar = new ToolBar();
		bar.getStyleClass().add("iphone-toolbar");
		
		{
			Region spring = new Region();
			HBox.setHgrow(spring, Priority.ALWAYS);
			
			bar.getItems().add(spring);
		}
		
		bar.getItems().add(LabelBuilder.create().styleClass("title").text(part.getLocalizedLabel()).build());
		
		{
			Region spring = new Region();
			HBox.setHgrow(spring, Priority.ALWAYS);
			
			bar.getItems().add(spring);			
		}
		
		Button add = new Button();
		add.setGraphic(ImageViewBuilder.create().styleClass("add").build());
		add.getStyleClass().add("iphone");
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
			}
		});
		bar.getItems().add(add);
		parent.setTop(bar);
		
		ListView<Todo> view = new ListView<Todo>();
		view.setCellFactory(new Callback<ListView<Todo>, ListCell<Todo>>() {
			
			@Override
			public ListCell<Todo> call(ListView<Todo> arg0) {
				return new TodoCell();
			}
		});
		view.setItems(controller.getTodoItems());
		parent.setCenter(view);
	}

	class TodoCell extends ListCell<Todo> {
		
		@Override
		protected void updateItem(final Todo item, boolean empty) {
			if( item != null && ! empty ) {
				BorderPane p = new BorderPane();
				p.getStyleClass().add("content");
				
				VBox box = new VBox(5);
				box.setPadding(new Insets(10));
				
				{
					Label l = LabelBuilder.create().styleClass("itemTitle").build();
					l.textProperty().bind(item.titleProperty());
					box.getChildren().add(l);	
				}
				
				{
					Label l = LabelBuilder.create().styleClass("itemTime").build();
					l.textProperty().bind(new StringBinding() {
						
						{
							bind(item.hasDateProperty(), item.dateProperty(), item.repeatProperty(), item.endDateProperty());
						}
						
						@Override
						protected String computeValue() {
							if( item.isHasDate() ) {
								if( item.getDate() != null ) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(item.getDate());
									
									Calendar now = Calendar.getInstance();
									Date nextDate = item.getDate();
									
									if( cal.before(now) ) {
										int field = Calendar.DATE;
										int amout = 1;
												
										switch (item.getRepeat()) {
										case NEVER:
											return FORMAT.format(nextDate);
										case DAILY:
											field = Calendar.DATE;
											break;
										case BI_WEEKLY:
											field = Calendar.WEEK_OF_YEAR;
											amout = 2;
											break;
										case MONTHLY:
											field = Calendar.MONTH;
											break;
										case WEEKLY:
											field = Calendar.WEEK_OF_YEAR;
											break;
										case YEARLY:
											field = Calendar.YEAR;
											break;
										}
										
										cal.add(field, amout);
										if( cal.before(now) ) {
											cal.add(field, amout);
										}
										
										if( item.getEndDate() != null && cal.before(item.getEndDate()) ) {
											return null;
										}
										
										nextDate = cal.getTime();
									}
									
									return FORMAT.format(nextDate);
								}
							}
							return null;
						}
					});
					box.getChildren().add(l);
				}
				 
				p.setCenter(box);
				
				p.setRight(BorderPaneBuilder.create().onMouseClicked(new OpenDetail(item)).center(ImageViewBuilder.create().styleClass("next").build()).build());
				setGraphic(p);
			} else {
				setGraphic(null);
			}
			setPrefHeight(50);
			super.updateItem(item, empty);
		}
	}
	
	class OpenDetail implements EventHandler<MouseEvent> {
		private final Todo item;
		
		public OpenDetail(Todo item) {
			this.item = item;
		}
		
		@Override
		public void handle(MouseEvent event) {
			controller.openDetail(item);
		}
	}
}