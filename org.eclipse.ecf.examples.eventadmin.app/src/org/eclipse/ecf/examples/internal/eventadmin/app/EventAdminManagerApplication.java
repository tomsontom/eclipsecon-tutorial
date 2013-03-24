/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.examples.internal.eventadmin.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.equinox.app.IApplication;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import service.TodoDataService.TodoItem;

public class EventAdminManagerApplication extends AbstractEventAdminApplication
		implements IApplication {

	private static final String DEFAULT_CONTAINER_TYPE = "ecf.jms.activemq.tcp.manager";
	public static final String DEFAULT_CONTAINER_ID = "tcp://localhost:61616/exampleTopic";

	private static final String KEY_EVENT_TYPE = "eventType";
	
	private static final String EVENT_TYPE_REGISTER = "NEW_CLIENT_REGISTERED";
	private static final String EVENT_TYPE_ALL_DATA = "ALL_DATA";
	private static final String EVENT_TYPE_DELETE_ITEM = "DELETE_ITEM";
	private static final String EVENT_TYPE_MODIFIED_ITEM = "MODIFIED_ITEM";
	
	private ServiceRegistration testEventHandlerRegistration;

	private Vector<TodoItem> items = new Vector<>();
	
	public EventAdminManagerApplication() {
		try {
			items.add(new TodoItem("e4 Tutorial","2013-03-25 13:00"));
			items.add(new TodoItem("e(fx)clipse BoF","2013-03-25 16:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected Object run() {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(EventConstants.EVENT_TOPIC, "*");
		testEventHandlerRegistration = bundleContext.registerService(
				EventHandler.class.getName(), new EventHandler() {
					
					public void handleEvent(Event event) {
						Object type = event.getProperty(KEY_EVENT_TYPE);
						if( EVENT_TYPE_REGISTER.equals(type) ) {
							Map<String, Object> properties = new HashMap<>();
							properties.put(KEY_EVENT_TYPE, EVENT_TYPE_ALL_DATA);
							
							try {
								ByteArrayOutputStream r = new ByteArrayOutputStream();
								ObjectOutputStream out = new ObjectOutputStream(r);
								out.writeObject(items);
								properties.put("DATA", r.toByteArray());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Event evt = new Event(DEFAULT_TOPIC, properties);
							eventAdminImpl.postEvent(evt);
						}
					}
				}, props);
		
		waitForDone();

		return IApplication.EXIT_OK;
	}

	protected void shutdown() {
		if (testEventHandlerRegistration != null) {
			testEventHandlerRegistration.unregister();
			testEventHandlerRegistration = null;
		}
		super.shutdown();
	}

	protected String usageApplicationId() {
		return "org.eclipse.ecf.examples.eventadmin.app.EventAdminManager";
	}

	protected String usageParameters() {
		StringBuffer buf = new StringBuffer("\n\t-containerType <default:"
				+ DEFAULT_CONTAINER_TYPE + ">");
		buf.append("\n\t-containerId <default:" + DEFAULT_CONTAINER_ID + ">");
		buf.append("\n\t-topic <default:" + DEFAULT_TOPIC + ">");
		return buf.toString();
	}

	protected void processArgs(String[] args) {
		containerType = DEFAULT_CONTAINER_TYPE;
		containerId = DEFAULT_CONTAINER_ID;
		targetId = null;
		topic = DEFAULT_TOPIC;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-containerType")) {
				containerType = args[i + 1];
				i++;
			} else if (args[i].equals("-containerId")) {
				containerId = args[i + 1];
				i++;
			} else if (args[i].equals("-topic")) {
				topic = args[i + 1];
				i++;
			}
		}

	}

}
