package com.triompha.cache.util;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/***
 * 获得 实体key
 * 	   列表key
 * 	   count key
 * 的基础操作类
 * 
 * @author triompha
 *
 */
public class CacheKeyUtil {
	
    public static  String getObjectCacheKey(Class<?> clazz , Serializable id){
        return clazz.getSimpleName()+"_"+String.valueOf(id);
    }
    
    public static  String getObjectCacheKey(String simpleName , Serializable id){
        return simpleName+"_"+String.valueOf(id);
    }
    
    public static String getObjectCacheKey(Object entity){
    	Object id = InvokeUtil.invokeKeyGetMethod(entity);
    	if(id==null){
    		return null;
    	}
        return getObjectCacheKey(entity.getClass(), (Serializable) id);
    }

    public static String getListCountKey(String sqlEncode , Object[] param){
        return sqlEncode + "@"+StringUtils.join(param, "_")+"@C";
    }

    public static String getListRegionKey(String sqlEncode , Object[] param){
        return sqlEncode + "@"+StringUtils.join(param, "_")+"@R";
    }

}
