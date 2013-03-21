package swt.e4.app.views;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerValueProperty;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import swt.e4.app.controller.TodoItemEditorController;
import swt.e4.app.model.Todo.Repeat;

@SuppressWarnings("restriction")
public class TodoItemEditor {
	private static final DateFormat FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
	
	@Inject
	TodoItemEditorController controller;

	private Text titleField;
	
	@PostConstruct
	void init(final Composite parent) {
		DataBindingContext dbc = controller.getDatabindingContext();
		
		parent.setLayout(new GridLayout());
		
		final IObservableValue showStartDate = BeanProperties.value("hasDate").observe(controller.getItem());
		final IObservableValue showRepeatDate = BeanProperties.value("repeat").observe(controller.getItem());
		
		final IObservableValue showEndDate = new ComputedValue(boolean.class) {
			
			@Override
			protected Object calculate() {
				return (Boolean)(showStartDate.getValue()) && showRepeatDate.getValue() != Repeat.NEVER;
			}
		};
		
		
		showStartDate.addValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void handleValueChange(ValueChangeEvent event) {
				
			}
		});
		
		{
			titleField = new Text(parent, SWT.BORDER);
			titleField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			IWidgetValueProperty uiProp = WidgetProperties.text(SWT.FocusOut);
			IBeanValueProperty mProp = BeanProperties.value("title");
			dbc.bindValue(uiProp.observe(titleField), mProp.observe(controller.getItem()));
		}
		
		{
			Group g = new Group(parent, SWT.BORDER);
			g.setLayout(new GridLayout());
			g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			{
				Composite comp = new Composite(g, SWT.NONE);
				comp.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
				comp.setLayoutData(new GridData(GridData.FILL_BOTH));
				Label l = new Label(comp, SWT.NONE);
				l.setText("Tagesabhaengige Erinnerung");
				l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				Button b = new Button(comp, SWT.CHECK);
				
				IWidgetValueProperty uiProp = WidgetProperties.selection();
				IBeanValueProperty mProp = BeanProperties.value("hasDate");
				dbc.bindValue(uiProp.observe(b), mProp.observe(controller.getItem()));
			}
			
			{
				Text dateStart = new Text(g, SWT.BORDER);
				final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.exclude = ! controller.getItem().isHasDate();
				dateStart.setLayoutData(gd);
				
				{
					IWidgetValueProperty uiProp = WidgetProperties.text(SWT.FocusOut);
					IBeanValueProperty mProp = BeanProperties.value("date");
					dbc.bindValue(uiProp.observe(dateStart), mProp.observe(controller.getItem()), createDateTargetToModel(), createDateModelToTarget());
						
				}
				
				{
					IWidgetValueProperty uiProp = WidgetProperties.visible();
					dbc.bindValue(uiProp.observe(dateStart), showStartDate);
				}
				
				showStartDate.addValueChangeListener(new IValueChangeListener() {
					
					@Override
					public void handleValueChange(ValueChangeEvent event) {
						gd.exclude = ! controller.getItem().isHasDate();
						parent.layout(true, true);
					}
				});
			}
			
			{
				Composite comp = new Composite(g, SWT.NONE);
				comp.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
				
				final GridData gd = new GridData(GridData.FILL_BOTH);
				gd.exclude = ! controller.getItem().isHasDate();
				comp.setLayoutData(gd);
				
				Label l = new Label(comp, SWT.NONE);
				l.setText("Wiederholen");
				l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				ComboViewer viewer = new ComboViewer(comp,SWT.READ_ONLY);
				viewer.setContentProvider(ArrayContentProvider.getInstance());
				viewer.setLabelProvider(new LabelProvider() {
					@Override
					public String getText(Object element) {
						return ((Repeat)element).name();
					}
				});
				viewer.setInput(Repeat.values());
				
				{
					IViewerValueProperty uiProp = ViewerProperties.singleSelection();
					IBeanValueProperty mProp = BeanProperties.value("repeat");
					dbc.bindValue(uiProp.observe(viewer), mProp.observe(controller.getItem()));
				}
				
				{
					IWidgetValueProperty uiProp = WidgetProperties.visible();
					dbc.bindValue(uiProp.observe(viewer.getControl()), showStartDate);
				}
				
				showStartDate.addValueChangeListener(new IValueChangeListener() {
					
					@Override
					public void handleValueChange(ValueChangeEvent event) {
						gd.exclude = ! controller.getItem().isHasDate();
						parent.layout(true, true);
					}
				});
			}
			
			{
				Text endDate = new Text(g, SWT.BORDER);
				final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.exclude = ! (showEndDate.getValue() != null && (Boolean)showEndDate.getValue());
				endDate.setLayoutData(gd);
				
				{
					IWidgetValueProperty uiProp = WidgetProperties.text(SWT.FocusOut);
					IBeanValueProperty mProp = BeanProperties.value("endDate");
					dbc.bindValue(uiProp.observe(endDate), mProp.observe(controller.getItem()), createDateTargetToModel(), createDateModelToTarget());					
				}
				
				{
					IWidgetValueProperty uiProp = WidgetProperties.visible();
					dbc.bindValue(uiProp.observe(endDate), showEndDate);
				}
				
				showEndDate.addValueChangeListener(new IValueChangeListener() {
					
					@Override
					public void handleValueChange(ValueChangeEvent event) {
						gd.exclude = !(Boolean)showEndDate.getValue();
						parent.layout(true, true);
					}
				});
			}
		}
		
	}
	
	private UpdateValueStrategy createDateTargetToModel() {
		return new UpdateValueStrategy().setConverter(new Converter(String.class,Date.class) {
			
			@Override
			public Object convert(Object fromObject) {
				if( fromObject == null || fromObject.toString().trim().isEmpty() ) {
					throw new RuntimeException();
				}
				try {
					return FORMAT.parseObject(fromObject.toString());
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	private UpdateValueStrategy createDateModelToTarget() {
		return new UpdateValueStrategy().setConverter(new Converter(Date.class,String.class) {
			
			@Override
			public Object convert(Object fromObject) {
				if( fromObject == null ) {
					return "";
				} else {
					return FORMAT.format(fromObject);
				}
			}
		});
	}
	
	public TodoItemEditorController getController() {
		return controller;
	}
	
	@Focus
	void focus() {
		titleField.setFocus();
	}
}
