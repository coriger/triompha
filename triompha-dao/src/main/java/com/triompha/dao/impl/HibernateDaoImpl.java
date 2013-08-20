package com.triompha.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.triompha.dao.BaseDao;



public class HibernateDaoImpl implements BaseDao {

	private HibernateTemplate  hibernateTemplate ;

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}


	String messageOut = "Too Many results !  return the first";
	//common logger
	public static final Logger logger =  LoggerFactory.getLogger(HibernateDaoImpl.class);

	@Override
	public void delete(Object entity) {
		hibernateTemplate.delete(entity);
	}

	@Override
	public <T> void delete(Class<T> clazz, Serializable id) {
		hibernateTemplate.bulkUpdate("delete from "+clazz.getSimpleName()+" where id = ?",id);
	}

	@Override
	public <T> void deleteAll(Collection<T> entities) {
		hibernateTemplate.deleteAll(entities);
	}

	@Override
	public int execute(String sql, Object... values) {
		return hibernateTemplate.bulkUpdate(sql, values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Class<T> clazz, Serializable id) {
		return (T) hibernateTemplate.get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String sql, Object... values) {
		List<T> list = hibernateTemplate.find(sql, values);
		if(list == null || list.size()==0 ){
			return null ;
		}else {
			if (list.size()>1) {
				logger.warn(messageOut+"(......in get function "+sql+"---"+values+")");
			}
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByExample(T entity) {
		List<T> list = hibernateTemplate.findByExample(entity);
		if(list == null || list.size()==0 ){
			return null ;
		}else {
			if (list.size()>1) {
				logger.warn(messageOut+"(......in getByExample function "+entity+")");
			}
			return list.get(0);
		}
	}

	@Override
	public <T> long getCount(Class<T> clazz) {
		String	sql = "select count(*) from "+ clazz.getSimpleName();
		return (Long) hibernateTemplate.find(sql).get(0) ;
	}

	@Override
	public long getCount(String sql, Object... values) {
		sql = "select count(*) from "+ StringUtils.substringAfter(sql, "from");
		return (Long) hibernateTemplate.find(sql, values).get(0) ;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(Class<T> clazz) {
		return hibernateTemplate.find("from " + clazz.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(String sql, Object... values) {
		return hibernateTemplate.find(sql,values);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> listLimit(final String sql, final int start, final int size, final Object... values) {
		return (List<T>) hibernateTemplate.execute(new HibernateCallback() {

			public Object doInHibernate(final Session session) throws HibernateException, SQLException {

				final Query query = session.createQuery(sql);
				if(values != null){
					for(int i =0 ; i<values.length ;i++)
						query.setParameter(i, values[i]);
				}
				query.setFirstResult(start);
				query.setMaxResults(size);
				return query.list();

			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> listByExample(T entity) {
		return hibernateTemplate.findByExample(entity);
	}

	@Override
	public Serializable save(Object entity) {
		return hibernateTemplate.save(entity);
	}

	@Override
	public void saveOrUpdate(Object entity) {
		hibernateTemplate.saveOrUpdate(entity);
	}

	@Override
	public <T> void saveOrUpdateAll(Collection<T> entities) {
		hibernateTemplate.saveOrUpdateAll(entities);
	}

	@Override
	public void update(Object entity) {
		hibernateTemplate.update(entity);
	}

}
