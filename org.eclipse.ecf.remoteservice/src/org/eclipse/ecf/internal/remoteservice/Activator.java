/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.internal.remoteservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.SystemLogService;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxyCreator;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private BundleContext context;

	private ServiceTracker logServiceTracker = null;

	private LogService logService = null;

	private ServiceRegistration remoteServiceProxyCreator;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	public BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext c) throws Exception {
		// nothing to do
		this.context = c;
		// Register default IRemoteServiceProxyCreator
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		this.remoteServiceProxyCreator = this.context.registerService(new String[] {IRemoteServiceProxyCreator.class.getName()}, new IRemoteServiceProxyCreator() {
			public Object createProxy(ClassLoader classloader, Class[] interfaces, InvocationHandler handler) {
				return Proxy.newProxyInstance(classloader, interfaces, handler);
			}
		}, props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext c) throws Exception {
		if (this.remoteServiceProxyCreator != null) {
			this.remoteServiceProxyCreator.unregister();
			this.remoteServiceProxyCreator = null;
		}
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
			logService = null;
		}
		this.context = null;
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public synchronized static Activator getDefault() {
		if (plugin == null) {
			plugin = new Activator();
		}
		return plugin;
	}

	protected LogService getLogService() {
		if (logServiceTracker == null) {
			logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
			logServiceTracker.open();
		}
		logService = (LogService) logServiceTracker.getService();
		if (logService == null)
			logService = new SystemLogService(PLUGIN_ID);
		return logService;
	}

	public void log(IStatus status) {
		if (logService == null)
			logService = getLogService();
		if (logService != null)
			logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
	}

}
