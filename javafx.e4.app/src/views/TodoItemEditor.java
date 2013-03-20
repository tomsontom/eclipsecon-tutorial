package views;

import java.text.DateFormat;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.DateTimeStringConverter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import model.Todo.Repeat;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import controller.TodoItemEditorController;

@SuppressWarnings("restriction")
public class TodoItemEditor {

	@Inject
	TodoItemEditorController controller;
	
	@PostConstruct
	void init(MPart part, BorderPane pane) {
		ToolBar bar = new ToolBar();
		bar.getStyleClass().add("iphone-toolbar");

		{
			Button cancel = new Button();
			cancel.setGraphic(new Label("Abbrechen"));
			cancel.getStyleClass().add("iphone");
			cancel.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					controller.cancelEdit();
				}
			});
			bar.getItems().add(cancel);			
		}

		bar.getItems().add(createSpacer());
		bar.getItems().add(
				LabelBuilder.create().styleClass("title").text(part.getLocalizedLabel()).build());
		bar.getItems().add(createSpacer());

		{
			Button cancel = new Button();
			cancel.setGraphic(new Label("Fertig"));
			cancel.getStyleClass().add("iphone");
			cancel.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					controller.applyEdit();
				}
			});
			bar.getItems().add(cancel);			
		}
		
		pane.setTop(bar);

		VBox box = new VBox(10);
		box.getStyleClass().add("detail");

		{
			TextField titleField = new TextField();
			titleField.textProperty().bindBidirectional(controller.getItem().titleProperty());
			box.getChildren().add(titleField);
		}

		{
			VBox dateArea = new VBox();
			dateArea.getStyleClass().add("contentContainer");

			{
				HBox line = new HBox();
				line.getChildren().add(new Label("Tagesabhaengige Erinnerung"));
				line.getChildren().add(createSpacer());
				CheckBox dateBound = new CheckBox();
				
				dateBound.selectedProperty().bindBidirectional(controller.getItem().hasDateProperty());
				line.getChildren().add(dateBound);
				
				dateArea.getChildren().add(line);
			}

			{
				HBox dateStart = new HBox();
				dateStart.managedProperty().bind(controller.getItem().hasDateProperty());
				dateStart.visibleProperty().bind(controller.getItem().hasDateProperty());
				TextField field = new TextField();
				field.textProperty().bindBidirectional(controller.getItem().dateProperty(), new DateTimeStringConverter(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)));
				HBox.setHgrow(field, Priority.ALWAYS);
				dateStart.getChildren().add(field);
				
				dateArea.getChildren().add(dateStart);
			}
			
			{
				HBox repeatProperty = new HBox();
				repeatProperty.managedProperty().bind(controller.getItem().hasDateProperty());
				repeatProperty.visibleProperty().bind(controller.getItem().hasDateProperty());
				repeatProperty.getChildren().add(new Label("Wiederholen"));
				repeatProperty.getChildren().add(createSpacer());
				
				ChoiceBox<Repeat> choiceBox = new ChoiceBox<Repeat>();
				choiceBox.setItems(FXCollections.observableArrayList(Repeat.values()));
				choiceBox.setConverter(new StringConverter<Repeat>() {
					
					@Override
					public String toString(Repeat arg0) {
						return arg0.name();
					}
					
					@Override
					public Repeat fromString(String arg0) {
						return Repeat.valueOf(arg0);
					}
				});
				choiceBox.valueProperty().bindBidirectional(controller.getItem().repeatProperty());
				repeatProperty.getChildren().add(choiceBox);
				
				dateArea.getChildren().add(repeatProperty);
			}
			
			{
				HBox dateEnd = new HBox();
				dateEnd.managedProperty().bind(controller.getItem().hasDateProperty().and(controller.getItem().repeatProperty().isNotEqualTo(Repeat.NEVER)));
				dateEnd.visibleProperty().bind(controller.getItem().hasDateProperty().and(controller.getItem().repeatProperty().isNotEqualTo(Repeat.NEVER)));
				TextField field = new TextField();
				field.setPromptText("Enddatum");
				HBox.setHgrow(field, Priority.ALWAYS);
				dateEnd.getChildren().add(field);
				
				dateArea.getChildren().add(dateEnd);
			}
			
			box.getChildren().add(dateArea);
			
			TextArea extraInfo = new TextArea();
			extraInfo.setPrefColumnCount(15);
			extraInfo.textProperty().bindBidirectional(controller.getItem().extraInfoProperty());
			box.getChildren().add(extraInfo);
		}

		pane.setCenter(box);
	}

	private static final Node createSpacer() {

		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);

		return spring;

	}
}
