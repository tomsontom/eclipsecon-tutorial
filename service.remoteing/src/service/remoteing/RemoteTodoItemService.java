package service.remoteing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.remoteservice.eventadmin.DistributedEventAdmin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import service.TodoDataService;
import service.TodoDataService.Callback;
import service.TodoDataService.TodoItem;

public class RemoteTodoItemService implements TodoDataService {
	private Vector<TodoItem> items = new Vector<>();
	
	private Callback<TodoItem> removeCallback;
	private Callback<TodoItem> addCallback;
	private Callback<TodoItem> modifiedCallback;
	private Callback<List<TodoItem>> loadCallback;
	
	private static final String DEFAULT_CONTAINER_TYPE = "ecf.generic.client";
	public static final String DEFAULT_TOPIC = "defaultTopic";
	private static final String DEFAULT_CONTAINER_TARGET = "ecftcp://localhost:3787/server";
	
	
	private static final String KEY_EVENT_TYPE = "eventType";
	
	private static final String EVENT_TYPE_REGISTER = "NEW_CLIENT_REGISTERED";
	private static final String EVENT_TYPE_ALL_DATA = "ALL_DATA";
	private static final String EVENT_TYPE_DELETE_ITEM = "DELETE_ITEM";
	private static final String EVENT_TYPE_MODIFIED_ITEM = "MODIFIED_ITEM";
	
	protected ServiceTracker<IContainerManager,IContainerManager> containerManagerTracker;

	private DistributedEventAdmin eventAdminImpl;

	private BundleContext bundleContext;

	public RemoteTodoItemService() {
		try {
			setupRemoteEventAdmin();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setupRemoteEventAdmin() throws Exception {
		bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		eventAdminImpl = new DistributedEventAdmin(bundleContext);
		
		// get container factory and create container
		IContainerFactory containerFactory = getContainerManager(bundleContext).getContainerFactory();
		IContainer container = containerFactory.createContainer(DEFAULT_CONTAINER_TYPE);

		// Get socontainer
		ISharedObjectContainer soContainer = (ISharedObjectContainer) container
						.getAdapter(ISharedObjectContainer.class);
		
		// Add to soContainer, with topic as name
		soContainer.getSharedObjectManager().addSharedObject(IDFactory.getDefault().createStringID(DEFAULT_TOPIC), eventAdminImpl,null);

		// then connect to target Id
		container.connect(IDFactory.getDefault().createID(
							container.getConnectNamespace(), DEFAULT_CONTAINER_TARGET), null);
		
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(EventConstants.EVENT_TOPIC, "*");
		
		bundleContext.registerService(
				EventHandler.class.getName(), new EventHandler() {
					
					@Override
					public void handleEvent(Event event) {
						Object type = event.getProperty(KEY_EVENT_TYPE);
						if( loadCallback != null && EVENT_TYPE_ALL_DATA.equals(type) ) {
							byte[] b = (byte[]) event.getProperty("DATA");
							try {
								ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
								loadCallback.call((List<TodoItem>) in.readObject());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							synchronized (loadCallback) {
								loadCallback = null;	
							}
						}
					}
				}, props);
		eventAdminImpl.start();
	}
	
	protected IContainerManager getContainerManager(BundleContext bundleContext) {
		if (containerManagerTracker == null) {
			containerManagerTracker = new ServiceTracker<IContainerManager,IContainerManager>(bundleContext,IContainerManager.class.getName(), null);
			containerManagerTracker.open();
		}
		return containerManagerTracker.getService();
	}
	
	@Override
	public void addItemRemoved(Callback<TodoItem> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addItemModifiedCallback(Callback<TodoItem> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadItems(Callback<List<TodoItem>> callback) {
		this.loadCallback = callback;
		
		Map<String, Object> properties = new HashMap<>();
		properties.put(KEY_EVENT_TYPE, EVENT_TYPE_REGISTER);
		Event evt = new Event(DEFAULT_TOPIC, properties);
		eventAdminImpl.postEvent(evt);
	}

	@Override
	public void saveItem(TodoItem item, Callback<Void> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteItem(TodoItem item, Callback<Void> callback) {
		// TODO Auto-generated method stub
		
	}

}
