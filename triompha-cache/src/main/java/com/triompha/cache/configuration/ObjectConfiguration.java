package com.triompha.cache.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class ObjectConfiguration {
	
	public static Logger logger = LoggerFactory.getLogger(ObjectConfiguration.class);
	
	public static Map<String, ObjectConfiguration> cachedObjectConfiguration = new ConcurrentHashMap<String, ObjectConfiguration>();
	
	private String cacheName;
	
	private Class clazz;
	
	private String keyProperty;
	
	private String keyGetMethod;
	

	public String getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(String keyProperty) {
		this.keyProperty = keyProperty;
	}

	public String getKeyGetMethod() {
		return keyGetMethod;
	}

	public void setKeyGetMethod(String keyGetMethod) {
		this.keyGetMethod = keyGetMethod;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public String toString() {
		return "ObjectConfiguration [cacheName=" + cacheName + ", clazz="
				+ clazz + ", keyProperty=" + keyProperty + ", keyGetMethod="
				+ keyGetMethod + "]";
	}
	
	public static ObjectConfiguration getObjectConfiguration(Class clazz){
		
		ObjectConfiguration configuration = cachedObjectConfiguration.get(clazz.getSimpleName());
		if(configuration == null){
			configuration = buildObjectConfiguration(clazz);
		}
		return configuration;
	}
	
	
	public static ObjectConfiguration buildObjectConfiguration(Class clazz){
		
		
		String keyMethod = null;
		String keyProperty = null;
		
		Method[] methods =	clazz.getMethods();
		
		for(Method method : methods){
			if(method.isAnnotationPresent(Id.class)){
				keyMethod = method.getName();
				keyProperty = method.getName().indexOf("get")>=0?StringUtils.substringAfterLast(method.getName(), "get"):StringUtils.substringAfterLast(method.getName(), "is");
				
				keyProperty = StringUtils.uncapitalise(keyProperty);
				
				break;
			}
		}
		
		if(keyMethod == null){
			Field[] fields =  clazz.getDeclaredFields();
			for(Field field : fields){
				if(field.isAnnotationPresent(Id.class)){
					keyProperty = field.getName();
					String tmpKeyProperty = StringUtils.capitalise(keyProperty);
					try {
						keyMethod = clazz.getMethod("get"+tmpKeyProperty)!=null? ("get"+tmpKeyProperty ):("is"+tmpKeyProperty );
					} catch (SecurityException e) {
						logger.error("error when build buildObjectConfiguration-->clazz:?",clazz,e);
					} catch (NoSuchMethodException e) {
						logger.error("error when build buildObjectConfiguration-->clazz:?",clazz,e);
					}
				}
			}
			
		}
		
		ObjectConfiguration configuration = new ObjectConfiguration();
		configuration.setCacheName(clazz.getSimpleName());
		configuration.setClazz(clazz);
		configuration.setKeyGetMethod(keyMethod);
		configuration.setKeyProperty(keyProperty);
		
		if(configuration == null){
			cachedObjectConfiguration.put(clazz.getSimpleName(), configuration);
		}
		
		return configuration;
	}
	

}
