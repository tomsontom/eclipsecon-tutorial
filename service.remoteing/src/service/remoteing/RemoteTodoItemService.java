package service.remoteing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

public class RemoteTodoItemService implements TodoDataService {
	private Vector<TodoItem> items = new Vector<>();
	
	private List<Callback<TodoItem>> removeCallback = new ArrayList<>();
	private List<Callback<TodoItem>> modifiedCallback = new ArrayList<>();
	private Callback<List<TodoItem>> loadCallback;
	
	private static final String DEFAULT_CONTAINER_TYPE = "ecf.generic.client";
	public static final String DEFAULT_TOPIC = "defaultTopic";
	private static final String DEFAULT_CONTAINER_TARGET = "ecftcp://localhost:3787/server";
	
	
	private static final String KEY_EVENT_TYPE = "eventType";
	private static final String KEY_DATA = "data";
	
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
					public synchronized void handleEvent(Event event) {
						System.err.println(event.getProperty(KEY_EVENT_TYPE) + " => " + EVENT_TYPE_MODIFIED_ITEM);
						Object type = event.getProperty(KEY_EVENT_TYPE);
						if( loadCallback != null && EVENT_TYPE_ALL_DATA.equals(type) ) {
							byte[] b = (byte[]) event.getProperty(KEY_DATA);
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
							loadCallback = null;	
						} else if( EVENT_TYPE_MODIFIED_ITEM.equals(type) 
								|| EVENT_TYPE_DELETE_ITEM.equals(type)) {
							System.err.println("EXTRACTING");
							
							TodoItem item = extractItem(event);
							
							if( item != null ) {
								List<Callback<TodoItem>> list = null;
								if( EVENT_TYPE_MODIFIED_ITEM.equals(type) ) {
									list = modifiedCallback;
								} else {
									list = removeCallback;
								}
								for( Callback<TodoItem> modified : list ) {
									modified.call(item);	
								}
							}
						}
					}
				}, props);
		eventAdminImpl.start();
	}
	
	private TodoItem extractItem(Event event) {
		try {
			byte[] b = (byte[]) event.getProperty(KEY_DATA);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
			return (TodoItem) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] serializeItem(TodoItem item) {
		try {
			ByteArrayOutputStream r = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(r);
			out.writeObject(item);
			return r.toByteArray();	
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
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
		removeCallback.add(callback);
	}

	@Override
	public void addItemModifiedCallback(Callback<TodoItem> callback) {
		modifiedCallback.add(callback);
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
	public void saveItem(TodoItem item) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(KEY_EVENT_TYPE, EVENT_TYPE_MODIFIED_ITEM);
		properties.put(KEY_DATA, serializeItem(item));
		Event evt = new Event(DEFAULT_TOPIC, properties);
		eventAdminImpl.postEvent(evt);
	}

	@Override
	public void deleteItem(TodoItem item) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(KEY_EVENT_TYPE, EVENT_TYPE_DELETE_ITEM);
		properties.put(KEY_DATA, serializeItem(item));
		Event evt = new Event(DEFAULT_TOPIC, properties);
		eventAdminImpl.postEvent(evt);
	}

}
