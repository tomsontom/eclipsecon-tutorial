/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/

package org.eclipse.ecf.internal.discovery;

import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.LogHelper;
import org.eclipse.ecf.core.util.PlatformHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class DiscoveryPlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.ecf.discovery"; //$NON-NLS-1$

	// The shared instance.
	private static DiscoveryPlugin plugin;

	private BundleContext context;

	private ServiceTracker adapterManagerTracker;
	private ServiceTracker logServiceTracker = null;

	/**
	 * The constructor.
	 */
	public DiscoveryPlugin() {
		super();
		plugin = this;
	}

	public IAdapterManager getAdapterManager() {
		// First, try to get the adapter manager via
		if (adapterManagerTracker == null) {
			adapterManagerTracker = new ServiceTracker(this.context, IAdapterManager.class.getName(), null);
			adapterManagerTracker.open();
		}
		IAdapterManager adapterManager = (IAdapterManager) adapterManagerTracker.getService();
		// Then, if the service isn't there, try to get from Platform class via
		// PlatformHelper class
		if (adapterManager == null)
			adapterManager = PlatformHelper.getPlatformAdapterManager();
		if (adapterManager == null)
			getDefault().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Cannot get adapter manager", null)); //$NON-NLS-1$
		return adapterManager;
	}

	public LogService getLogService() {
		if (logServiceTracker == null) {
			logServiceTracker = new ServiceTracker(this.context, LogService.class.getName(), null);
			logServiceTracker.open();
		}
		return (LogService) logServiceTracker.getService();
	}

	public void log(IStatus status) {
		LogService logService = getLogService();
		if (logService != null) {
			logService.log(LogHelper.getLogCode(status), LogHelper.getLogMessage(status), status.getException());
		}
	}

	/**
	 * This method is called upon plug-in activation
	 * @param ctxt the bundle context 
	 * @throws Exception 
	 */
	public void start(BundleContext ctxt) throws Exception {
		this.context = ctxt;
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param ctxt the bundle context 
	 * @throws Exception 
	 */
	public void stop(BundleContext ctxt) throws Exception {
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}
		if (adapterManagerTracker != null) {
			adapterManagerTracker.close();
			adapterManagerTracker = null;
		}
		plugin = null;
		this.context = null;
	}

	/**
	 * Returns the shared instance.
	 * @return default discovery plugin instance.
	 */
	public synchronized static DiscoveryPlugin getDefault() {
		if (plugin == null) {
			plugin = new DiscoveryPlugin();
		}
		return plugin;
	}

	public static boolean isStopped() {
		return plugin == null;
	}
	
	public BundleContext getBundleContext() {
		return context;
	}
}
