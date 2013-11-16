package com.triompha.cache.configuration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;


public class ListConfiguration {
	
	
	public static Logger logger = LoggerFactory.getLogger(ListConfiguration.class);
	
	public static Map<String, ListConfiguration> cachedListConfigurations = new ConcurrentHashMap<String, ListConfiguration>();
	
	public static Map<String, List<String>> objListMap = new HashMap<String, List<String>>();
	
	public static CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	
	public static List<String> getMapedList(String beanName){
		return objListMap.get(beanName);
	}
	
	public static ListConfiguration getListConfigurationMD5(String hqlmd5){
		return cachedListConfigurations.get(hqlmd5);
	}
	
	public static ListConfiguration getListConfiguration(String sql , HibernateDaoSupport daoSupport){
		
		ListConfiguration con =   cachedListConfigurations.get(DigestUtils.md5Hex(sql));
		if(con == null){
			try {
				con = buildListConfiguration(sql,daoSupport);
			} catch (JSQLParserException e) {
				logger.error("error happend when build ListConfiguration , sql -->?", sql ,e);
			}
		}
		return con;
	}
	
	public static ListConfiguration buildListConfiguration(String sql , HibernateDaoSupport daoSupport) throws JSQLParserException{
		
		
		List<String> params = new ArrayList<String>();
		
		//设置成可被组件转化的SQL
		Select select =  (Select) parserManager.parse(new StringReader( (sql.toLowerCase().indexOf("select")>=0) ? sql :("select 1 " + sql)));
		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
		
		//递归 设置参数列表
		Expression expression = ( (PlainSelect)select.getSelectBody()).getWhere();
		addParams(expression ,params);
		 
		ListConfiguration configuration = new ListConfiguration(sql);
		//设置参数列表
		configuration.setParams(params);
		//设置转化之后的HQL 的MD5码
		configuration.setHqlMD5(DigestUtils.md5Hex(sql));
		//设置表名
		configuration.setBeanName(plainSelect.getFromItem().toString());
		
		if(daoSupport!=null){
			configuration.setObjectConfiguration(getObjectConfiguration(configuration.getBeanName(), daoSupport));
			configuration.setClazz(configuration.getObjectConfiguration().getClazz());
			configuration.setKeysHql("select " + configuration.getObjectConfiguration().getKeyProperty()+" "+ sql);
		}
		configuration.setCountHql("select count(*) "+sql);
		cachedListConfigurations.put(configuration.getHqlMD5(), configuration);
		
		//加入表与表下面所有列表对应的HQL md5
		List<String> lists = objListMap.get(configuration.getBeanName());
		if(lists==null){
			lists = new ArrayList<String>();
			objListMap.put(configuration.getBeanName(), lists);
		}
		lists.add(configuration.hqlMD5);
		
		
		logger.info("build ListConfiguration -->" + configuration);
		System.out.println(("build ListConfiguration -->" + configuration));
		
		
		return configuration;
		
	}
	
	private static ObjectConfiguration getObjectConfiguration(String name, HibernateDaoSupport daoSupport){
		String classPath = null;
		Set<String> allPersistClass =   daoSupport.getSessionFactory().getAllClassMetadata().keySet();
		for(String clazzP : allPersistClass){
			if(clazzP.endsWith("."+name)){
				classPath = clazzP;
				break;
			}
		}
		Class clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			logger.error("error when ObjectConfiguration -->name:?",name,e);
		}
		return ObjectConfiguration.getObjectConfiguration(clazz);
	}
	
	
	
	private static void addParams(Expression expression , List<String> params){
		if ((expression instanceof AndExpression ) || (expression instanceof OrExpression ) ||  (expression instanceof Parenthesis )) {
			
			if((expression instanceof Parenthesis )){
				addParams( (((Parenthesis) expression).getExpression()) ,params);
				return;
			}
			
			addParams(((BinaryExpression) expression).getLeftExpression() ,params);
			addParams(((BinaryExpression) expression).getRightExpression() ,params);
			
		} else if("?".equals(((BinaryExpression) expression).getRightExpression().toString())) {
			params.add(((BinaryExpression) expression).getLeftExpression().toString());
		}
	
		
	}
	
	
	
    private String hql;
    private String hqlMD5;
    private String beanName;
    private String keysHql;
    private String countHql;
    private ObjectConfiguration objectConfiguration;
    private List<String> params;
    private Class<?> clazz;
    
    public ListConfiguration(){};
    
    
    
    public String getCountHql() {
		return countHql;
	}

	public void setCountHql(String countHql) {
		this.countHql = countHql;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public ListConfiguration(String hql){
    	this.hql = hql;
    }
    
	public String getHql() {
		return hql;
	}
	public void setHql(String hql) {
		this.hql = hql;
	}
	public String getHqlMD5() {
		return hqlMD5;
	}
	public void setHqlMD5(String hqlMD5) {
		this.hqlMD5 = hqlMD5;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	

	public String getKeysHql() {
		return keysHql;
	}

	public void setKeysHql(String keysHql) {
		this.keysHql = keysHql;
	}
	
	

	@Override
	public String toString() {
		return "ListConfiguration [hql=" + hql + ", hqlMD5=" + hqlMD5
				+ ", beanName=" + beanName + ", keysHql=" + keysHql
				+ ", countHql=" + countHql + ", objectConfiguration="
				+ objectConfiguration + ", params=" + params + ", clazz="
				+ clazz + "]";
	}

	public ObjectConfiguration getObjectConfiguration() {
		return objectConfiguration;
	}

	public void setObjectConfiguration(ObjectConfiguration objectConfiguration) {
		this.objectConfiguration = objectConfiguration;
	}
    
    public static void main(String[] args) {
    	String statement = "select cs1 FROM mytable WHERE CClssMsd = 9 and (col2=? or col33<?) and CClssMsd=? or coxx like ? order by col3 desc";
    	
    	
    	
    	ListConfiguration.getListConfiguration(statement,null);
    	
    	ListConfiguration.getListConfiguration("select entry_id from entry where user_id = ? and status!=-1",null);
    	
    	ListConfiguration.getListConfiguration("select entry_id from entry where status=? and user_id = ?",null);
    	
    	System.out.println(ListConfiguration.cachedListConfigurations);
    	
    	
	}

	
    
    
}
