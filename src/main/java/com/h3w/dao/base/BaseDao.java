
package com.h3w.dao.base;

import com.h3w.utils.Page;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:xietianjiao@h3w.com.cn">xietj</a>
 * @version 1.0
 * 2016-5-16 上午10:23:24
 */
public abstract class BaseDao<T, ID extends Serializable> implements IBaseDao<T, ID> {
    @Autowired //hibernate4需引入SessionFactory.....
    private SessionFactory sessionFactory;
    public Session getSession() {
        //需要开启事物，才能得到CurrentSession
        return sessionFactory.getCurrentSession();
    	//return sessionFactory.openSession();
    }
    /*hibernate5需要引入EntityManagerFactory而不是seesionFactory。spring boot EntityManagerFactory must not be null
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    public Session getSession() {
        //需要开启事物，才能得到CurrentSession
        Session session = entityManagerFactory.unwrap(SessionFactory.class).getCurrentSession();
        //session.getTransaction().begin();
        return session;
    }*/

    protected Class<T> entityClass;
 
    public BaseDao() {
 
    }
 
    protected Class<T> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return entityClass;
    }
 
    /**
     * <保存实体>
     * <完整保存实体>
     * @param t 实体参数
     * @see com.itv.launcher.util.IBaseDao#save(Object)
     */
    public ID save(T t) {
    	return (ID)this.getSession().save(t);

    }


    public ID save(T t, Session session) {
    	return (ID)session.save(t);

    }

    /**
     * <保存或者更新实体>
     * @param t 实体
     * @see com.itv.launcher.util.IBaseDao#saveOrUpdate(Object)
     */
    public void saveOrUpdate(T t) {
        this.getSession().saveOrUpdate(t);
    }
    public void saveOrUpdate(T t, Session session) {
    	session.saveOrUpdate(t);
    }

    /**
     * <load>
     * <加载实体的load方法>
     * @param id 实体的id
     * @return 查询出来的实体
     * @see com.itv.launcher.util.IBaseDao#load(Serializable)
     */
    public T load(ID id) {
        T load = (T) this.getSession().load(getEntityClass(), id);
        return load;
    }

    /**
     * <get>
     * <查找的get方法>
     * @param id 实体的id
     * @return 查询出来的实体
     * @see com.itv.launcher.util.IBaseDao#get(Serializable)
     */
    public T get(ID id) {
        T load = (T) this.getSession().get(getEntityClass(), id);
        return load;
    }
    public T get(ID id, Session session) {
        T load = (T) session.get(getEntityClass(), id);
        return load;
    }

    /**
     * <contains>
     * @param t 实体
     * @return 是否包含
     * @see com.itv.launcher.util.IBaseDao#contains(Object)
     */
    public boolean contains(T t) {
        return this.getSession().contains(t);
    }

    /**
     * <delete>
     * <删除表中的t数据>
     * @param t 实体
     * @see com.itv.launcher.util.IBaseDao#delete(Object)
     */
    public void delete(T t) {
        this.getSession().delete(t);
    }

    /**
     * <根据ID删除数据>
     * @param Id 实体id
     * @return 是否删除成功
     * @see com.itv.launcher.util.IBaseDao#deleteById(Serializable)
     */
    public boolean deleteById(ID Id) {
         T t = get(Id);
         if(t == null){
             return false;
         }
         delete(t);
        return true;
    }

    /**
     * <删除所有>
     * @param entities 实体的Collection集合
     * @see com.itv.launcher.util.IBaseDao#deleteAll(Collection)
     */
    public void deleteAll(Collection<T> entities) {
        for(Object entity : entities) {
            this.getSession().delete(entity);
        }
    }

    /**
     * <执行Hql语句>
     * @param hqlString hql
     * @param values 不定参数数组
     * @see com.itv.launcher.util.IBaseDao#queryHql(String, Object[])
     */
    public int queryHql(String hqlString, Object... values) {
        Query query = this.getSession().createQuery(hqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return query.executeUpdate();
    }
    public int queryHql(String hqlString, Session session, Object... values) {
        Query query = session.createQuery(hqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return query.executeUpdate();
    }



    /**
     * <执行Sql语句>
     * @param sqlString sql
     * @param values 不定参数数组
     * @see com.itv.launcher.util.IBaseDao#querySql(String, Object[])
     */
    public void querySql(String sqlString, Object... values) {
        Query query = this.getSession().createSQLQuery(sqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        query.executeUpdate();
    }

    /**
     * <根据HQL语句查找唯一实体>
     * @param hqlString HQL语句
     * @param values 不定参数的Object数组
     * @return 查询实体
     * @see com.itv.launcher.util.IBaseDao#getByHQL(String, Object[])
     */
    public T getByHQL(String hqlString, Object... values) {
        Query query = this.getSession().createQuery(hqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return (T) query.uniqueResult();
    }

    /**
     * <根据SQL语句查找唯一实体>
     * @param sqlString SQL语句
     * @param values 不定参数的Object数组
     * @return 查询实体
     * @see com.itv.launcher.util.IBaseDao#getBySQL(String, Object[])
     */
    public T getBySQL(String sqlString, Object... values) {
        Query query = this.getSession().createSQLQuery(sqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return (T) query.uniqueResult();
    }

    /**
     * <根据HQL语句，得到对应的list>
     * @param hqlString HQL语句
     * @param values 不定参数的Object数组
     * @return 查询多个实体的List集合
     * @see com.itv.launcher.util.IBaseDao#getListByHQL(String, Object[])
     */
    public List<T> getListByHQL(String hqlString, Object... values) {
        Query query = this.getSession().createQuery(hqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return query.list();
    }

    public List<T> getListByHQLNum(String hqlString,int first,int num, Object... values) {
        Query query = this.getSession().createQuery(hqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        query.setFirstResult(first);
        query.setMaxResults(num);
        return query.list();
    }
    /**
     * <根据SQL语句，得到对应的list>
     * @param sqlString HQL语句
     * @param values 不定参数的Object数组
     * @return 查询多个实体的List集合
     * @see com.itv.launcher.util.IBaseDao#getListBySQL(String, Object[])
     */
    public List<Object> getListBySQL(String sqlString, Object... values ) {
        Query query = this.getSession().createSQLQuery(sqlString);
        if (values != null)
        {
            for (int i = 0; i < values.length; i++)
            {
                query.setParameter(i+1, values[i]);
            }
        }
        return query.list();
    }

    /**
     * 由sql语句得到List
     * @param sql
     * @param map
     * @param values
     * @return List
     * @see com.itv.launcher.util.IBaseDao#findListBySql(String, com.itv.launcher.util.RowMapper, Object[])
     */
    public List findListBySql(final String sql, final RowMapper map, final Object... values) {
        final List list = new ArrayList();
        // 执行JDBC的数据批量保存
        Work jdbcWork = new Work()
        {
            public void execute(Connection connection)
                throws SQLException
            {

                PreparedStatement ps = null;
                ResultSet rs = null;
                try
                {
                    ps = connection.prepareStatement(sql);
                    for (int i = 0; i < values.length; i++)
                    {
                        setParameter(ps, i+1, values[i]);

                    }

                    rs = ps.executeQuery();
                    int index = 0;
                    while (rs.next())
                    {
                        Object obj = map.mapRow(rs, index++);
                        list.add(obj);

                    }
                }
                finally
                {
                    if (rs != null)
                    {
                        rs.close();

                    }
                    if (ps != null)
                    {
                        ps.close();
                    }
                }
            }
        };
        this.getSession().doWork(jdbcWork);
        return list;
    }

    /**
     * <refresh>
     * @param t 实体
     * @see com.itv.launcher.util.IBaseDao#refresh(Object)
     */
    public void refresh(T t) {
        this.getSession().refresh(t);
    }

    /**
     * <update>
     * @param t 实体
     * @see com.itv.launcher.util.IBaseDao#update(Object)
     */
    public void update(T t) {
        this.getSession().update(t);
    }
    public void update(T t, Session session) {
        session.update(t);
    }

    /**
     * <根据HQL得到记录数>
     * @param hql HQL语句
     * @param values 不定参数的Object数组
     * @return 记录总数
     * @see com.itv.launcher.util.IBaseDao#countByHql(String, Object[])
     */
    public Long countByHql(String hql, Object... values) {
        Query query = this.getSession().createQuery(hql);
        if(values != null){
            for(int i = 0; i < values.length; i++) {
                query.setParameter(i+1, values[i]);
            }
        }
        return (Long) query.uniqueResult();
    }

    /**
     * 根据sql统计
     * @param sql
     * @param values
     * @return
     */
    public Long countBySql(String sql, Object... values) {
        Query query = this.getSession().createSQLQuery(sql);
        if(values != null){
            for(int i = 0; i < values.length; i++) {
                query.setParameter(i+1, values[i]);
            }
        }
        Object result = query.uniqueResult();
        if(result==null)return 0L;
        else return Long.valueOf(result.toString());
    }

    /**
     * <HQL分页查询>
     * @param hql HQL语句
     * @param countHql 查询记录条数的HQL语句
     * @param pageNo 下一页
     * @param pageSize 一页总条数
     * @param values 不定Object数组参数
     * @return PageResults的封装类，里面包含了页码的信息以及查询的数据List集合
     * @see com.itv.launcher.util.IBaseDao#findPageByFetchedHql(String, String, int, int, Object[])
     */
    public Page<T> findPageByFetchedHql(String hql,Page<T> page,Object... values) {
        Query query = this.getSession().createQuery(hql);
        if(values != null){
            for(int i = 0; i < values.length; i++) {
                query.setParameter(i+1, values[i]);
            }
        }
        //当前页数
        int currentPage = page.getPlainPageNum();
        //每页记录数
        int pageSize = page.getNumPerPage();
        //查询总记录数
        String countHql = null;
        if(hql.startsWith("from")){
        	countHql = "select count(*) "+hql;
        }else{
        	countHql = "select count(*) "+hql.substring(hql.indexOf("from"));
        }
        //总记录数
        Long count = countByHql(countHql, values);
        page.setTotalCount(count.intValue());
        //总页数
        page.setTotalPage(count%pageSize>0?count.intValue()/pageSize+1:count.intValue()/pageSize);
        List<T> itemList = query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize).list();
        if (itemList == null)
        {
            itemList = new ArrayList<T>();
        }
        page.setResults(itemList);

        return page;
    }


    /**
     * <HQL分页查询>
     * @param hql HQL语句
     * @param countHql 查询记录条数的HQL语句
     * @param pageNo 下一页
     * @param pageSize 一页总条数
     * @param values 不定Object数组参数
     * @return PageResults的封装类，里面包含了页码的信息以及查询的数据List集合
     * @see com.itv.launcher.util.IBaseDao#findPageByFetchedHql(String, String, int, int, Object[])
     */
    public Page<T> findPageByFetchedHql2(String hql,Page<T> page,Object[] values) {
        Query query = this.getSession().createQuery(hql);
        if(values != null){
            for(int i = 0; i < values.length; i++) {
                query.setParameter(i+1, values[i]);
            }
        }
        //当前页数
        int currentPage = page.getPlainPageNum();
        //每页记录数
        int pageSize = page.getNumPerPage();
        //查询总记录数
        String countHql = null;
        if(hql.startsWith("from")){
        	countHql = "select count(*) "+hql;
        }else{
        	countHql = "select count(*) "+hql.substring(hql.indexOf("from"));
        }
        Long count = countByHql(countHql, values);
        page.setTotalCount(count.intValue());
        //总页数
        page.setTotalPage(count%pageSize>0?count.intValue()/pageSize+1:count.intValue()/pageSize);
        List<T> itemList = query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize).list();
        if (itemList == null)
        {
            itemList = new ArrayList<T>();
        }
        page.setResults(itemList);
         
        return page;
    }


    public Page<Object> findPageByFetchedSql(String sql,Page<Object> page,Object... values) {
        Query query = this.getSession().createSQLQuery(sql);
        if(values != null){
            for(int i = 0; i < values.length; i++) {
                query.setParameter(i+1, values[i]);
            }
        }
        int currentPage = page.getPlainPageNum();
        int pageSize = page.getNumPerPage();
        String countSql = "select count(*) "+sql.substring(sql.indexOf("from"));

        Long count = countBySql(countSql, values);
        page.setTotalCount(count.intValue());
        page.setTotalPage(count%pageSize>0?count.intValue()/pageSize+1:count.intValue()/pageSize);
        List<Object> itemList = query.setFirstResult((currentPage - 1) * pageSize).setMaxResults(pageSize).list();
        if (itemList == null)
        {
            itemList = new ArrayList<Object>();
        }
        page.setResults(itemList);

        return page;
    }
    /**
     * 
     * 设置每行批处理参数
     * 
     * @param ps
     * @param pos ?占位符索引，从0开始
     * @param data
     * @throws SQLException
     * @see [类、类#方法、类#成员]
     */
    private void setParameter(PreparedStatement ps, int pos, Object data)
        throws SQLException
    {
        if (data == null)
        {
            ps.setNull(pos + 1, Types.VARCHAR);
            return;
        }
        Class dataCls = data.getClass();
        if (String.class.equals(dataCls))
        {
            ps.setString(pos + 1, (String)data);
        }
        else if (boolean.class.equals(dataCls))
        {
            ps.setBoolean(pos + 1, ((Boolean)data));
        }
        else if (int.class.equals(dataCls))
        {
            ps.setInt(pos + 1, (Integer)data);
        }
        else if (double.class.equals(dataCls))
        {
            ps.setDouble(pos + 1, (Double)data);
        }
        else if (Date.class.equals(dataCls))
        {
            Date val = (Date)data;
            ps.setTimestamp(pos + 1, new Timestamp(val.getTime()));
        }
        else if (BigDecimal.class.equals(dataCls))
        {
            ps.setBigDecimal(pos + 1, (BigDecimal)data);
        }
        else
        {
            // 未知类型
            ps.setObject(pos + 1, data);
        }
         
    }
    
    

}

