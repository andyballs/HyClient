package rage.pitclient.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import rage.pitclient.eventbus.event.Event;
import rage.pitclient.eventbus.event.EventData;
import rage.pitclient.eventbus.event.EventPriority;
import rage.pitclient.eventbus.event.SubscribeEvent;

public class EventBus {
private static final Map<Class<? extends Event>, ArrayList<EventData>> REGISTRY_MAP = new HashMap<Class<? extends Event>, ArrayList<EventData>>();
	
	private static void sortListValue(final Class<? extends Event> clazz) {
		
		final ArrayList<EventData> flexableArray = new ArrayList<EventData>();
		
		for(final byte b : EventPriority.VALUE_ARRAY) {
			for(EventData methodData : EventBus.REGISTRY_MAP.get(clazz)) {
				if(methodData.priority == b) {
					flexableArray.add(methodData);
				}
			}
		}
		
		EventBus.REGISTRY_MAP.put(clazz, flexableArray);
		
	}
	
	public boolean post(Event event) {
		final ArrayList<EventData> dataList = get(event.getClass());
		
		if (dataList != null) {
			for (EventData d : dataList) {
				try {
					d.target.invoke(d.source, event);
				} catch(Exception e) {
					
					
					System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.err.println("~~~~~~~~~~~~~EVENT BUS~~~~~~~~~~~~~~");
					
					System.err.println("ERROR EXECUTING " + d.target.toString() );
					Thread.currentThread().dumpStack();
					System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					
					Minecraft.getMinecraft().shutdown();
				}
			}
		}
		return (event.isCancelable() ? event.isCanceled() : false);
	}
	
	private static boolean isMethodBad(final Method method) {
		return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(SubscribeEvent.class);
	}
	
	private static boolean isMethodBad(final Method method, final Class<? extends Event> clazz) {
		return isMethodBad(method) || method.getParameterTypes()[0].equals(clazz);
	}
	
	public static ArrayList<EventData> get(final Class<? extends Event> clazz){
		return REGISTRY_MAP.get(clazz);
	}
	
	public static void cleanMap(final boolean removeOnlyEmptyValues) {
		
		final Iterator<Map.Entry<Class<? extends Event>, ArrayList<EventData>>> iterator = EventBus.REGISTRY_MAP.entrySet().iterator();
		
		while(iterator.hasNext()) {
			if(!removeOnlyEmptyValues || iterator.next().getValue().isEmpty()) {
				iterator.remove();
			}
		}
	}
	
	public static void unregister(final Object o, final Class<? extends Event> clazz) {
		
		if(REGISTRY_MAP.containsKey(clazz)) {
			for(final EventData methodData : REGISTRY_MAP.get(clazz)) {
				if(methodData.source.equals(o)) {
					REGISTRY_MAP.get(clazz).remove(methodData);
				}
			}
		}
		
		cleanMap(true);
		
	}
	
	public static void unregister(final Object o) {
		
		for(ArrayList<EventData> flexableArray : REGISTRY_MAP.values()) {
			
			for(int i = flexableArray.size() -1; i >= 0; i--) {
				
				if(flexableArray.get(i).source.equals(o)) {
					flexableArray.remove(i);
				}
				
			}
			
		}
		
		cleanMap(true);
		
	}
	
	public static void register(final Method method, final Object o) {
		
		final Class<?> clazz = method.getParameterTypes()[0];
		
		final EventData methodData = new EventData(o, method, method.getAnnotation(SubscribeEvent.class).priority());
		
		if(!methodData.target.isAccessible()) {
			methodData.target.setAccessible(true);
		}
		
		if(REGISTRY_MAP.containsKey(clazz)) {
			
			if(!REGISTRY_MAP.get(clazz).contains(methodData)) {
				REGISTRY_MAP.get(clazz).add(methodData);
				sortListValue((Class<? extends Event>) clazz);
			}
			
		}
		else {
			REGISTRY_MAP.put((Class<? extends Event>) clazz, new ArrayList<EventData>() {
				
				{
					this.add(methodData);
				}
				
			});
		}
		
	}
	
	public static void register(final Object o, final Class<? extends Event> clazz) {
		
		for(final Method method : o.getClass().getMethods()) {
			if(!isMethodBad(method, clazz)) {
				register(method, o);
			}
		}
		
	}
	
	public static void register(Object o) {
		for(final Method method : o.getClass().getMethods()) {
			if(!isMethodBad(method)) {
				register(method, o);
			}
		}
	}
}
