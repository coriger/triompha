package com.triompha.dao.test;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sohu.spaces.enterprise.model.Enterprise;
import com.triompha.dao.BaseDao;

public class TestConnection {
	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext-dal.xml"});
		BaseDao basedao = (BaseDao) context.getBean("baseDao");
		//get
		Enterprise enterprise =	basedao.get(Enterprise.class, 36630435L);
		System.out.println(enterprise);

		//update
		//		enterprise.setBgaligncode(21);
		//		basedao.update(enterprise);

		//save
		//		enterprise.setApplies(null);
		//		enterprise.setUid(36630435L);
		//		basedao.save(enterprise);

		//delete
		//basedao.delete(Enterprise.class, 36630435L);

		//list by class
		//		List<Enterprise> list = basedao.list(Enterprise.class);
		//		System.out.println(list.size());
		//list by Hql
		//		List<Enterprise> list = basedao.list("from Enterprise where stat=?", 1);
		//		System.out.println(list.size());

		//list by limit
		//		List<Enterprise> list = basedao.listLimit("from Enterprise where stat=?", 0, 2, 1);
		//		System.out.println(list.size());
		//		basedao.execute("update Enterprise set bgaligncode=? where uid=?", 1,36630434L);

		//		List<Object[]> list = 	basedao.listLimit("select uid,stat from Enterprise where stat=? and typecode = ? order by updatetime desc", 0, 10, 1,1);
		//		for(Object[] i : list){
		//			System.out.println(i[0]);
		//			System.out.println(i[1]);
		//		}

		//		List<Object[]> list = 	basedao.list("select uid,stat from Enterprise where stat=? and typecode = ? order by updatetime desc",  1,1);
		//		for(Object[] i : list){
		//			System.out.println(i[0]);
		//			System.out.println(i[1]);
		//		}
		//		List<Enterprise> list = 	basedao.list("select uid,stat from Enterprise where stat=? and typecode = ? order by updatetime desc",  1,1);
		//		for(Enterprise i : list){
		//			System.out.println( i);
		//		}
	}

}
