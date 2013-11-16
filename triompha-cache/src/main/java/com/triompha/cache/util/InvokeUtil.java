package com.triompha.cache.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.triompha.cache.configuration.ListConfiguration;
import com.triompha.cache.configuration.ObjectConfiguration;
import com.triompha.cache.exception.CacheException;


/**
 * 反射功能的基础类
 * 用于
 * 		获得实体的ID
 * 		注入实体的ID
 * 		搜索两个实体件 差异的属性
 * 		获得实体的参数集
 * 
 * @author triompha
 *
 */
public class InvokeUtil {

    public static final Logger logger = LoggerFactory.getLogger(InvokeUtil.class);

    /***
     **使用反射，获取注解@ID的JPA的ID的值
     * @param clazz
     * @return
     * @throws CacheException
     */
    public static Object invokeKeyGetMethod(Object entity){
    	Object id = null;
        try {
        	id =PropertyUtils.getProperty(entity, ObjectConfiguration.getObjectConfiguration(entity.getClass()).getKeyProperty());
        } catch (Exception e) {
            logger.error("invokeKeyMethod ,obj is"+entity,e);
        }
        return id;
    }
    
    public static Object invokeKeySetyMethod(Object entity , Serializable id){
        try {
        	PropertyUtils.setProperty(entity, ObjectConfiguration.getObjectConfiguration(entity.getClass()).getKeyProperty(),id);
        } catch (Exception e) {
            logger.error("invokeKeyMethod ,obj is"+entity,e);
        }
        return id;
    }
    
    @SuppressWarnings("unchecked")
	public static List<String> screenChangedPros(Object oldEntity , Object newEntity){
    	Map<String,Object> oldProMap = new HashMap<String,Object>();
    	Map<String,Object> newProMap = new HashMap<String,Object>();
        try {
        	oldProMap = PropertyUtils.describe(oldEntity);
        	newProMap = PropertyUtils.describe(newEntity);
		} catch (Exception e) {
			logger.error("screenChangedPros ,old:? , new:?",oldEntity.toString(),newEntity.toString());
			logger.error("Exception is ", e);
		}
        
        List<String> changedPro = new ArrayList<String>(10);
        
        if(oldProMap ==null || newProMap==null){
        	return changedPro;
        }
        
        for(String pro : oldProMap.keySet()){
        	if(oldProMap.get(pro)==null){
        		if(newProMap.get(pro)!=null){
        			changedPro.add(pro);
        		}
        	}else if(!oldProMap.get(pro).equals(newProMap.get(pro))){
        		changedPro.add(pro);
			}
        }
        return changedPro;
    }



    public static Object[] invokeParamsMethod(String cacheKey , Object obj) throws CacheException{
    	
    	ListConfiguration configuration = ListConfiguration.getListConfigurationMD5(cacheKey);
    	
        if(configuration==null)
            return null;
        List<String> paramsPros = configuration.getParams();
        if(paramsPros==null)
            return null;
        Object[] params = new Object[paramsPros.size()];
        Object param = null;
        for(int i=0;i<paramsPros.size();i++){
            try {
                param = PropertyUtils.getProperty(obj,  paramsPros.get(i));
            } catch (IllegalAccessException e) {
                logger.error("invoke getObjParams error1 " ,e);
            } catch (InvocationTargetException e) {
                logger.error("invoke getObjParams error2 " ,e);
            } catch (NoSuchMethodException e) {
                logger.error("invoke getObjParams error3 " ,e);
            }
            if(param==null)
                throw new CacheException("null obj param");

            params[i] = param;
        }

        return params;
    }

}
