/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package renderer;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.renderers.swt.SWTPartRenderer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.PGroupToolItem;
import org.eclipse.nebula.widgets.pgroup.RectangleGroupStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * Create a contribute part.
 */
@SuppressWarnings("restriction")
public class PGroupContributedPartRenderer extends SWTPartRenderer {

	@Inject
	private IPresentationEngine engine;

	@Optional
	@Inject
	private Logger logger;

	private MPart partToActivate;

	private Listener activationListener = new Listener() {
		public void handleEvent(Event event) {
			// we only want to activate the part if the activated widget is
			// actually bound to a model element
			MPart part = (MPart) event.widget.getData(OWNING_ME);
			if (part != null) {
				try {
					partToActivate = part;
					activate(partToActivate);
				} finally {
					partToActivate = null;
				}
			}
		}
	};

	public Object createWidget(final MUIElement element, Object parent) {
		if (!(element instanceof MPart) || !(parent instanceof Composite))
			return null;

		Widget parentWidget = (Widget) parent;
		Widget newWidget = null;
		final MPart part = (MPart) element;

		
		final Composite newComposite = new Composite((Composite) parentWidget, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		newComposite.setLayout(layout);
		final PGroup groupWidget = new PGroup(newComposite,
				SWT.SMOOTH) {

			/**
			 * Field to determine whether we are currently in the midst of
			 * granting focus to the part.
			 */
			private boolean beingFocused = false;

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.widgets.Composite#setFocus()
			 */
			@Override
			public boolean setFocus() {
				if (!beingFocused) {
					try {
						// we are currently asking the part to take focus
						beingFocused = true;
						// delegate an attempt to set the focus here to the
						// part's implementation (if there is one)
						Object object = part.getObject();
						if (object != null) {
							ContextInjectionFactory.invoke(object, Focus.class,
									part.getContext(), null);
							return true;
						}
						return super.setFocus();
					} finally {
						// we are done, unset our flag
						beingFocused = false;
					}
				}

				if (logger != null) {
					String id = part.getElementId();
					if (id == null) {
						logger.warn(new IllegalStateException(),
								"Blocked recursive attempt to activate part " //$NON-NLS-1$
										+ id);
					} else {
						logger.warn(new IllegalStateException(),
								"Blocked recursive attempt to activate part"); //$NON-NLS-1$
					}
				}

				// already being focused, likely some strange recursive call,
				// just return
				return true;
			}
		};

		groupWidget.setStrategy(new RectangleGroupStrategy() {
			@Override
			public boolean isToggleLocation(int x, int y) {
				return false;
			}
		});
		groupWidget.setToggleRenderer(null);
		groupWidget.setText(part.getLocalizedLabel());
		groupWidget.setImagePosition(SWT.LEFT|SWT.TOP);
		groupWidget.setImage(getImage(part));
		groupWidget.setLayout(new FillLayout(SWT.VERTICAL));

		newWidget = newComposite;
		bindWidget(element, newWidget);

		// Create a context for this part
		IEclipseContext localContext = part.getContext();
		localContext.set(Composite.class.getName(), groupWidget);

		IContributionFactory contributionFactory = (IContributionFactory) localContext
				.get(IContributionFactory.class.getName());
		Object newPart = contributionFactory.create(part.getContributionURI(),
				localContext);
		part.setObject(newPart);
		
		setupToobar(part, groupWidget, contributionFactory,localContext);
		
		return newWidget;
	}

	private void setupToobar(MPart part, PGroup group, IContributionFactory contributionFactory, final IEclipseContext context) {
		if( part.getToolbar() != null ) {
			for(  MToolBarElement i : part.getToolbar().getChildren() ) {
				if( i instanceof MToolItem ) {
					MToolItem ti = (MToolItem) i;
					PGroupToolItem item = new PGroupToolItem(group, SWT.PUSH);
					item.setText(ti.getLocalizedLabel());
					item.setImage(getImage(ti));
					item.setToolTipText(ti.getLocalizedTooltip());
					
					final Runnable handlerRunnable;
					if( i instanceof MDirectToolItem ) {
						MDirectToolItem di = (MDirectToolItem) i;
						final Object handler = contributionFactory.create(di.getContributionURI(), context);	
						handlerRunnable = new Runnable() {
							
							@Override
							public void run() {
								ContextInjectionFactory.invoke(handler, Execute.class, context);
							}
						};
					} else {
						// Handle handled items
						handlerRunnable = null;
					}
					
					
					item.addSelectionListener(new SelectionAdapter() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							handlerRunnable.run();
						}						
					});
				}
			}
		}
	}
	
	@Override
	protected boolean requiresFocus(MPart element) {
		if (element == partToActivate) {
			return true;
		}
		return super.requiresFocus(element);
	}

	@Override
	public void hookControllerLogic(final MUIElement me) {
		super.hookControllerLogic(me);
		if (!(me instanceof MPart)) {
			return;
		}
		Widget widget = (Widget) me.getWidget();
		if (widget instanceof Composite) {
			widget.addListener(SWT.Activate, activationListener);
		}

	}
	
	@Override
	public Object getUIContainer(MUIElement element) {
		if (element instanceof MToolBar) {
			MUIElement container = (MUIElement) ((EObject) element)
					.eContainer();
			MUIElement parent = container.getParent();
			if (parent == null) {
				MPlaceholder placeholder = container.getCurSharedRef();
				if (placeholder != null) {
					return placeholder.getParent().getWidget();
				}
			} else {
				return parent.getWidget();
			}
		}
		return super.getUIContainer(element);
	}

	@Override
	public void disposeWidget(MUIElement element) {
		if (element instanceof MPart) {
			MPart part = (MPart) element;
			MToolBar toolBar = part.getToolbar();
			if (toolBar != null) {
				Widget widget = (Widget) toolBar.getWidget();
				if (widget != null) {
					unbindWidget(toolBar);
					widget.dispose();
				}
			}

			for (MMenu menu : part.getMenus()) {
				engine.removeGui(menu);
			}
		}
		super.disposeWidget(element);
	}
	
}
