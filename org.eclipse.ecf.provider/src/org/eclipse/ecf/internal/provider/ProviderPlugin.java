/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/

package org.eclipse.ecf.internal.provider;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.util.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 */
public class ProviderPlugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.ecf.provider"; //$NON-NLS-1$

	//The shared instance.
	private static ProviderPlugin plugin;

	public static final String NAMESPACE_IDENTIFIER = org.eclipse.ecf.core.identity.StringID.class.getName();

	private BundleContext context = null;

	private ServiceTracker logServiceTracker = null;

	private ServiceTracker adapterManagerTracker = null;

	private ServiceTracker sslServerSocketFactoryTracker;
	private ServiceTracker sslSocketFactoryTracker;

	public IAdapterManager getAdapterManager() {
		if (context == null)
			return null;
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

	/**
	 * The constructor.
	 */
	public ProviderPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context1) throws Exception {
		this.context = context1;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context1) throws Exception {
		if (logServiceTracker != null) {
			logServiceTracker.close();
			logServiceTracker = null;
		}
		if (adapterManagerTracker != null) {
			adapterManagerTracker.close();
			adapterManagerTracker = null;
		}

		if (sslServerSocketFactoryTracker != null) {
			sslServerSocketFactoryTracker.close();
			sslServerSocketFactoryTracker = null;
		}
		if (sslSocketFactoryTracker != null) {
			sslSocketFactoryTracker.close();
			sslSocketFactoryTracker = null;
		}
		this.context = null;
	}

	private LogService systemLogService;

	protected LogService getLogService() {
		if (context == null) {
			if (systemLogService == null)
				systemLogService = new SystemLogService(PLUGIN_ID);
			return systemLogService;
		}
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
	 * Returns the shared instance.
	 */
	public synchronized static ProviderPlugin getDefault() {
		if (plugin == null) {
			plugin = new ProviderPlugin();
		}
		return plugin;
	}

	public String getNamespaceIdentifier() {
		return NAMESPACE_IDENTIFIER;
	}

	public SSLServerSocketFactory getSSLServerSocketFactory() {
		if (context == null)
			return null;
		if (sslServerSocketFactoryTracker == null) {
			sslServerSocketFactoryTracker = new ServiceTracker(this.context, SSLServerSocketFactory.class.getName(), null);
			sslServerSocketFactoryTracker.open();
		}
		return (SSLServerSocketFactory) sslServerSocketFactoryTracker.getService();
	}

	public SSLSocketFactory getSSLSocketFactory() {
		if (context == null)
			return null;
		if (sslSocketFactoryTracker == null) {
			sslSocketFactoryTracker = new ServiceTracker(this.context, SSLSocketFactory.class.getName(), null);
			sslSocketFactoryTracker.open();
		}
		return (SSLSocketFactory) sslSocketFactoryTracker.getService();
	}

}