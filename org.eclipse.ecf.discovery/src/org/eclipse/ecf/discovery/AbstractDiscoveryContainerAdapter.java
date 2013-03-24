/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.internal.discovery.*;
import org.eclipse.equinox.concurrent.future.*;

public abstract class AbstractDiscoveryContainerAdapter extends
		AbstractContainer implements IDiscoveryLocator, IDiscoveryAdvertiser {

	/**
	 * Collection of service listeners i.e. Collection<IServiceListener>. NOTE:
	 * Access to this collection is synchronized, so subclasses should take this
	 * into account.
	 */
	protected final Set allServiceListeners;

	private DiscoveryContainerConfig config;

	/**
	 * Map of service type to collection of service listeners i.e.
	 * <String,Collection<IServiceListener>>. NOTE: Access to this map is
	 * synchronized, so subclasses should take this into account.
	 */
	protected final Map serviceListeners;
	protected final String servicesNamespaceName;
	/**
	 * Collection of service type listeners i.e.
	 * Collection<IServiceTypeListener>. NOTE: Access to this collection is
	 * synchronized, so subclasses should take this into account.
	 */
	protected final Collection serviceTypeListeners;

	private DiscoveryServiceListener discoveryServiceListener;
	private DiscoveryServiceListener discoveryServiceTypeListener;
	private ServiceTypeComparator discoveryServiceListenerComparator;

	/**
	 * @param aNamespaceName
	 * @param aConfig
	 */
	public AbstractDiscoveryContainerAdapter(String aNamespaceName,
			DiscoveryContainerConfig aConfig) {
		servicesNamespaceName = aNamespaceName;
		Assert.isNotNull(servicesNamespaceName);
		config = aConfig;
		Assert.isNotNull(config);
		serviceTypeListeners = Collections.synchronizedSet(new HashSet());
		serviceListeners = Collections.synchronizedMap(new HashMap());
		allServiceListeners = Collections.synchronizedSet(new HashSet());

		discoveryServiceListener = new DiscoveryServiceListener(this,
				IServiceListener.class);
		discoveryServiceTypeListener = new DiscoveryServiceListener(this,
				IServiceTypeListener.class);

		discoveryServiceListenerComparator = new ServiceTypeComparator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener
	 * (org.eclipse.ecf.discovery.IServiceListener)
	 */
	public void addServiceListener(IServiceListener aListener) {
		Assert.isNotNull(aListener);
		allServiceListeners.add(aListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener
	 * (org.eclipse.ecf.discovery.identity.IServiceTypeID,
	 * org.eclipse.ecf.discovery.IServiceListener)
	 */
	public void addServiceListener(IServiceTypeID aType,
			IServiceListener aListener) {
		Assert.isNotNull(aListener);
		Assert.isNotNull(aType);
		synchronized (serviceListeners) { // put-if-absent idiom race condition
			Collection v = (Collection) serviceListeners.get(aType);
			if (v == null) {
				v = Collections.synchronizedSet(new HashSet());
				serviceListeners.put(aType, v);
			}
			v.add(aListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener
	 * (org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
	public void addServiceTypeListener(IServiceTypeListener aListener) {
		Assert.isNotNull(aListener);
		serviceTypeListeners.add(aListener);
	}

	protected void clearListeners() {
		serviceListeners.clear();
		serviceTypeListeners.clear();
		allServiceListeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.AbstractContainer#dispose()
	 */
	public void dispose() {
		disconnect();
		clearListeners();
		config = null;
		discoveryServiceListener.dispose();
		discoveryServiceTypeListener.dispose();
		super.dispose();
	}

	/**
	 * Calls {@link IServiceListener#serviceDiscovered(IServiceEvent)} for all
	 * registered {@link IServiceListener}
	 * 
	 * @param aServiceEvent
	 *            The {@link IServiceEvent} to send along the call
	 */
	protected void fireServiceDiscovered(IServiceEvent aServiceEvent) {
		Assert.isNotNull(aServiceEvent);
		final Collection listeners = getListeners(aServiceEvent
				.getServiceInfo().getServiceID().getServiceTypeID());
		if (listeners != null) {
			for (final Iterator i = listeners.iterator(); i.hasNext();) {
				final IServiceListener l = (IServiceListener) i.next();
				l.serviceDiscovered(aServiceEvent);
				Trace.trace(DiscoveryPlugin.PLUGIN_ID,
						DiscoveryDebugOption.METHODS_TRACING, this.getClass(),
						"fireServiceDiscovered", aServiceEvent.toString()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Calls
	 * {@link IServiceTypeListener#serviceTypeDiscovered(IServiceTypeEvent)} for
	 * all registered {@link IServiceTypeListener}
	 * 
	 * @param aServiceTypeEvent
	 *            The {@link IServiceTypeEvent} to send along the call
	 */
	protected void fireServiceTypeDiscovered(IServiceTypeEvent aServiceTypeEvent) {
		Assert.isNotNull(aServiceTypeEvent);
		List notify = null;
		synchronized (serviceTypeListeners) {
			notify = new ArrayList(serviceTypeListeners);
		}
		for (final Iterator i = notify.iterator(); i.hasNext();) {
			final IServiceTypeListener l = (IServiceTypeListener) i.next();
			l.serviceTypeDiscovered(aServiceTypeEvent);
			Trace.trace(DiscoveryPlugin.PLUGIN_ID,
					DiscoveryDebugOption.METHODS_TRACING, this.getClass(),
					"fireServiceTypeDiscovered", aServiceTypeEvent.toString()); //$NON-NLS-1$
		}
	}

	/**
	 * Calls {@link IServiceListener#serviceUndiscovered(IServiceEvent)} for all
	 * registered {@link IServiceListener}
	 * 
	 * @param aServiceEvent
	 *            The {@link IServiceEvent} to send along the call
	 */
	protected void fireServiceUndiscovered(IServiceEvent aServiceEvent) {
		Assert.isNotNull(aServiceEvent);
		final Collection listeners = getListeners(aServiceEvent
				.getServiceInfo().getServiceID().getServiceTypeID());
		if (listeners != null) {
			for (final Iterator i = listeners.iterator(); i.hasNext();) {
				final IServiceListener l = (IServiceListener) i.next();
				l.serviceUndiscovered(aServiceEvent);
				Trace.trace(DiscoveryPlugin.PLUGIN_ID,
						DiscoveryDebugOption.METHODS_TRACING, this.getClass(),
						"fireServiceUndiscovered", aServiceEvent.toString()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @return The {@link DiscoveryContainerConfig} of this
	 *         {@link IDiscoveryContainerAdapter}
	 */
	protected DiscoveryContainerConfig getConfig() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.IContainer#getConnectNamespace()
	 */
	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(servicesNamespaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.IIdentifiable#getID()
	 */
	public ID getID() {
		if (config != null) {
			return config.getID();
		}
		return null;
	}

	/**
	 * @return The name of this discovery container
	 * @since 4.0
	 */
	public abstract String getContainerName();

	// merges the allServiceListener with the serviceListeners for the given
	// type

	/**
	 * Joins the {@link Collection} of {@link IServiceListener}s interested in
	 * any {@link IServiceTypeID} with the {@link Collection} of the
	 * {@link IServiceListener} registered for the given {@link IServiceTypeID}
	 * 
	 * @param aServiceType
	 *            The {@link IServiceTypeID} for which the
	 *            {@link IServiceListener}s are returned
	 * @return All {@link IServiceListener}s interested in the given
	 *         {@link IServiceTypeID}
	 */
	protected Collection getListeners(IServiceTypeID aServiceType) {
		Assert.isNotNull(aServiceType);
		Collection listeners = new HashSet();
		synchronized (serviceListeners) {
			for (Iterator itr = serviceListeners.keySet().iterator(); itr
					.hasNext();) {
				final IServiceTypeID typeID = (IServiceTypeID) itr.next();
				int compare = discoveryServiceListenerComparator.compare(
						aServiceType, typeID);
				if (compare == 0) {
					Collection collection = (Collection) serviceListeners
							.get(typeID);
					if (collection != null) {
						listeners.addAll(collection);
					}
				}
			}
		}
		synchronized (allServiceListeners) {
			listeners.addAll(allServiceListeners);
		}
		return Collections.unmodifiableCollection(listeners);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServicesNamespace
	 * ()
	 */
	public Namespace getServicesNamespace() {
		return IDFactory.getDefault().getNamespaceByName(servicesNamespaceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener
	 * (org.eclipse.ecf.discovery.IServiceListener)
	 */
	public void removeServiceListener(IServiceListener aListener) {
		Assert.isNotNull(aListener);
		allServiceListeners.remove(aListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener
	 * (org.eclipse.ecf.discovery.identity.IServiceTypeID,
	 * org.eclipse.ecf.discovery.IServiceListener)
	 */
	public void removeServiceListener(IServiceTypeID aType,
			IServiceListener aListener) {
		Assert.isNotNull(aListener);
		Assert.isNotNull(aType);
		synchronized (serviceListeners) {
			final Collection v = (Collection) serviceListeners.get(aType);
			if (v != null) {
				v.remove(aListener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#
	 * removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)
	 */
	public void removeServiceTypeListener(IServiceTypeListener aListener) {
		Assert.isNotNull(aListener);
		serviceTypeListeners.remove(aListener);
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryAdvertiser#unregisterAllServices()
	 * @since 3.0
	 */
	public void unregisterAllServices() {
		throw new UnsupportedOperationException("Not yet implemeted"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#purgeCache()
	 * @since 3.0
	 */
	public IServiceInfo[] purgeCache() {
		return new IServiceInfo[] {};
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)
	 * @since 3.0
	 */
	public IFuture getAsyncServiceInfo(final IServiceID service) {
		IExecutor executor = new ThreadsExecutor();
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getServiceInfo(service);
			}
		}, null);
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServiceTypes()
	 * @since 3.0
	 */
	public IFuture getAsyncServiceTypes() {
		IExecutor executor = new ThreadsExecutor();
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getServiceTypes();
			}
		}, null);
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServices()
	 * @since 3.0
	 */
	public IFuture getAsyncServices() {
		IExecutor executor = new ThreadsExecutor();
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getServices();
			}
		}, null);
	}

	/**
	 * @see org.eclipse.ecf.discovery.IDiscoveryLocator#getAsyncServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)
	 * @since 3.0
	 */
	public IFuture getAsyncServices(final IServiceTypeID type) {
		IExecutor executor = new ThreadsExecutor();
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getServices(type);
			}
		}, null);
	}
}
