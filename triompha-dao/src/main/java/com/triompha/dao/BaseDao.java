package com.triompha.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/*****
 * 
 * 最基本的数据库执行接口
 * @author triompha
 * @version 2.0
 * @param <T>
 * 
 * 
 */
public interface BaseDao {


	/********************************************************single get *******************************************************************/
	/**
	 * 根据表的对应类 和ID 查询数据库 获取单记录，并封装成对象
	 * @param clazz	类型
	 * @param id	ID
	 * @return		封装的对象
	 */
	public <T> T get(Class<T> clazz,Serializable id);

	/***
	 * 根据sql或hql 查询数据库 获取单记录，并封装成对象
	 * @param <T>
	 * @param sql
	 * @param values
	 * @return
	 */
	public <T> T get(String sql,Object...values);

	/***
	 * 根据传入对象的entity的值查询数据库，得出单对象
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> T getByExample(T entity);


	/********************************************************muliti get *******************************************************************/
	/***
	 * 根据表的对应类 查询数据库，并封装成对象列表
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz);

	/***
	 * 约定当查询的不是整个数据项时，而是单独的某个或某些字段时，返回的是Object[]数组，数组中的项与select的项一一对应。
	 * 
	 * 根据sql或hql 查询数据库获，并封装成对象列表
	 * @param <T>
	 * @param sql
	 * @param values
	 * @return
	 */
	public <T> List<T> list(String sql , Object...values);

	/***
	 * 约定当查询的不是整个数据项时，而是单独的某个或某些字段时，返回的是Object[]数组，数组中的项与select的项一一对应。
	 * 如List<Object[]> list = 	basedao.listLimit("select uid,stat from Enterprise where stat=? and typecode = ? order by updatetime desc", 0, 10, 1,1);
	 * 根据sql或hql 查询数据库 获取单记录，得出分页处理的数据，并封装成对象
	 * @param <T>
	 * @param sql
	 * @param start
	 * @param size
	 * @param values
	 * @return
	 */
	public <T> List<T> listLimit(final String sql , final int start , final int size, final Object... values);

	/***
	 * 根据传入对象的entity的值查询数据库，得出对象列表
	 * @param <T>
	 * @param entity
	 * @return
	 */
	public <T> List<T> listByExample(T entity);


	/********************************************************table count *******************************************************************/
	/***
	 * 根据传入对象泛型，查询数据库的数据数量，无论状态如何
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> long getCount(Class<T> clazz);

	/***
	 * 根据传入sql和对应的参数的值，查询出数据量
	 * @param hql
	 * @param values
	 * @return
	 */
	public long getCount(String sql, Object... values);


	/********************************************************save update delete*******************************************************************/
	/***
	 * 保存对象数据
	 * @param entity
	 * @return
	 */
	public Serializable save(Object entity);
	/***
	 * 更新对象数据
	 * @param entity
	 */
	public void update(Object entity);

	/***
	 * be attention! this will delete the object physical.
	 * if you only want to change the status of the object , please use update method
	 * 
	 * 删除对象数据
	 * @param entity
	 */
	public void delete(Object entity);
	/***
	 *  * if you only want to change the status of the object , please use update method
	 * 
	 * 根据ID删除对象数据
	 * 真实删除数据
	 * 使用此方法的前提是 表名为标准骆驼模式命名
	 * 并且ID项为id
	 * @param <T>
	 * @param clazz
	 * @param id
	 */
	public <T> void delete(Class<T> clazz , Serializable id);

	/***
	 * 添加或更新数据
	 * @param entity
	 */
	public void saveOrUpdate(Object entity);
	/****
	 * 删除集合内所有数据
	 * @param <T>
	 * @param entities
	 */
	public <T> void deleteAll(Collection<T> entities);
	/****
	 * 添加或更新集合内所有数据
	 * @param <T>
	 * @param entities
	 */
	public <T> void saveOrUpdateAll(Collection<T> entities);

	/********************************************************execute*******************************************************************/
	/****
	 * 执行sql语句
	 * @param sql
	 * @param values
	 */
	public int execute(String sql , Object...values);

}
