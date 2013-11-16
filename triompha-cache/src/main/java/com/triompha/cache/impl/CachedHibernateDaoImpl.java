package com.triompha.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.triompha.cache.Cache;
import com.triompha.cache.configuration.ListConfiguration;
import com.triompha.cache.exception.CacheException;
import com.triompha.cache.util.CacheKeyUtil;
import com.triompha.cache.util.InvokeUtil;
import com.triompha.cache.util.RegionUtil;
import com.triompha.dao.BaseDao;
import com.triompha.dao.impl.HibernateDaoImpl;

public class CachedHibernateDaoImpl extends HibernateDaoImpl {

	private Cache cache;

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public static final Logger logger = LoggerFactory.getLogger(CachedHibernateDaoImpl.class);

	@Override
	public <T> T get(Class<T> clazz, Serializable id) {
		String cacheKey = CacheKeyUtil.getObjectCacheKey(clazz, id);
		@SuppressWarnings("unchecked")
		T t = (T) cache.get(cacheKey);
		if (t == null) {
			t = super.get(clazz, id);
			if (t != null) {
				cache.put(cacheKey, (Serializable) t);
			}
		}
		return t;
	}
	
	@Override
	public void update(Object entity) {
		
		//如果主键都找不到，直接返回
		Object id = InvokeUtil.invokeKeyGetMethod(entity);
		if (id == null)
			return;

		Object oldEntity = this.get(entity.getClass(), (Serializable) id);
		List<String> changedPro = InvokeUtil.screenChangedPros(oldEntity,entity);
		
		//如果没有任何属性更新则直接返回。
		if (changedPro.size() == 0) {
			return;
		}
		// 更新数据库数据
		super.update(entity);
		// 删除对象缓存
		cache.remove(CacheKeyUtil.getObjectCacheKey(entity));

		// 删除原对象的列表
		this.deleteListCache(oldEntity, changedPro);
		this.deleteListCache(entity, changedPro);
	}
	

    @Override
    public Serializable save(Object entity) {
        Serializable rstId =  super.save(entity);
        entity = this.get(entity.getClass(), rstId);
        if(rstId!=null){
            this.deleteListCache(entity,null);
        }
        return rstId;
    }
    
    @Override
    public void delete(Object entity) {
        if(entity==null){
            return ;
        }
        //删除缓存数据
        String cacheKey = CacheKeyUtil.getObjectCacheKey(entity);
        if(cacheKey!=null){
            cache.remove(cacheKey);
        }
        //删除数据库数据
        super.delete(entity);
        //删除列表数据
        this.deleteListCache(entity,null);
    }
    @Override
    public long getCount(String sql, Object... values) {
    	ListConfiguration listCfg = ListConfiguration.getListConfiguration(sql,this);
        Long count = 0L;
        String countKey = CacheKeyUtil.getListCountKey(listCfg.getHqlMD5(), values);
        count = (Long) cache.get(countKey);
        if(count == null){
            List<?> list = list(listCfg.getCountHql(), values);
            if(list!=null && list.size()>0){
                count = (Long) list.get(0);
                cache.put(countKey, count);
            }
        }
        return count==null?0L:count;
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> listPage(String sql, int start, int size,Object... values) {
		
		ListConfiguration listCfg = ListConfiguration.getListConfiguration(sql,this);
		String regionKey = CacheKeyUtil.getListRegionKey(listCfg.getHqlMD5(), values);
		int[] regions = RegionUtil.getRegion(start, size);
		List ids = new ArrayList();

		Map<Integer, List> region = (Map<Integer, List>) cache.get(regionKey);
		
		//设置region的目的就是分片加载IDS，而不是一次性将所有的都加载过来
		if (region == null)
			region = new HashMap<Integer, List>();
		boolean reloadFlag = false;
		for (int i : regions) {
			if (region.get(i) == null) {
				region.put(i, super.listPage(listCfg.getKeysHql(),  i*RegionUtil.regionSize, RegionUtil.regionSize, values));
				reloadFlag = true;
			}
			ids.addAll(region.get(i));
		}
		if (reloadFlag)
			cache.put(regionKey, (Serializable) region);

		// 考虑是否以多线程方式实现
		List realIds = ids.subList(start,(start + size) > ids.size() ? ids.size() : (start + size));
		List<String> realKeys = new ArrayList<String>();
		if (realIds != null) {
			for (Object id : realIds) {
				realKeys.add(CacheKeyUtil.getObjectCacheKey(listCfg.getClazz(),  (Serializable) id));
			}
		}

		List<T> result = cache.getMulti(realKeys);
		if (result == null) {
			result = new ArrayList<T>(realIds.size());
			for (int i = 0; i < realIds.size(); i++) {
				// 看效率,之后可以考虑使用union
				result.add(i,(T) this.get(listCfg.getClazz(), (Serializable) realIds.get(i)));
			}
		} else {
			for (int i = 0; i < realIds.size(); i++) {
				T t = result.get(i);
				if (t == null) {
					result.set(i,(T) this.get(listCfg.getClazz(), (Serializable) realIds.get(i)));
				}
			}
		}

		return result;
	}
	
	//changedPros 表示新增或删除实体时，无须检查直接删除对应列表信息
	//			  changedPros.size()==0 时表示没有更改，无须删除列表信息
	//            changedPro.size()>0 时 ，检查更改的属性是否包含在参数信息列表之中。
	private void deleteListCache(Object entity , List<String> changedPros){
		if(changedPros!=null && changedPros.size()==0)
			return;
		
		List<String> cachedList = ListConfiguration.getMapedList(entity.getClass().getSimpleName());
		if(cachedList==null){
			return ;
		}
		String listCcountKey = "";
        String listRegionKey = "";
        Object[] params = null;
        boolean delList = false;
		for(String key : cachedList){
			delList = false;
            try {
            	
            	//检查是否有参数属性更新，如果有，才更新对应的里表信息，否则直接跳过，不做更新
            	if(changedPros!=null && changedPros.size()>0){
            		List<String> paramPros =  ListConfiguration.getListConfigurationMD5(key).getParams();
            		for(String pro : changedPros){
            			if(paramPros.contains(pro)){
            				delList = true;
            				break;
            			}
            		}
            	}
            	if(!delList){
            		continue;
            	}
            	
                params = InvokeUtil.invokeParamsMethod(key, entity);
                listCcountKey = CacheKeyUtil.getListCountKey(key, params);
                listRegionKey = CacheKeyUtil.getListRegionKey(key, params);
                cache.remove(listCcountKey);
                cache.remove(listRegionKey);
            } catch (CacheException e) {
                logger.error("error when invoke delete(Object entity) if InvokeUtil.getObjParams(name, entity) ",e);
            }
        }
    }

}
