triompha
========

工作中软件集的整理

triompha-dao是在spring基础上对dao的简单封装。HibernateDaoImpl是在hibernate基础上的封装。
1、工程中需加入hibernate和spring的依赖。
2、spring配置文件中须加入对应属性的配置。
   <bean id="hibernateTemplate" class="org.springframework.orm.hibernate3.HibernateTemplate">
		<property name="sessionFactory" ref="sessionFactory" />
	 </bean>
	 <bean id="baseDao" class="com.triompha.dao.impl.HibernateDaoImpl">
		<property name="hibernateTemplate" ref="hibernateTemplate" />
	 </bean>
