/**
 * Copyright (c) 2014 Wteamfly.  All rights reserved. 网飞公司 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.helper;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

/**
 * 一个绑定到当前线程的实体管理器.
 *
 * @author 侯骏雄
 * @since 5.0.0
 */
public final class DemuxEntityManager implements EntityManager {
    /**
     * 绑定当前线程的实体管理器容器.
     */
    private final InheritableThreadLocal<EntityManager> threadEntityManager = new InheritableThreadLocal<EntityManager>();

    /**
     * 当前线程是否有绑定实体管理器.
     *
     * @return true-有绑定 false-未绑定
     */
    public Boolean isBind() {
        Boolean result = false;
        result = threadEntityManager.get() != null;
        return result;
    }

    /**
     * 将实体管理器绑定到当前线程.
     *
     * @param manager
     *            需绑定的实体管理器.
     * @return 当前线程被弃用的，先前的实体管理器
     */
    public EntityManager bindManager(final EntityManager manager) {
        final EntityManager oldValue = threadEntityManager.get();
        threadEntityManager.set(manager);
        return oldValue;
    }

    @Override
    public void persist(final Object entity) {
        threadEntityManager.get().persist(entity);
    }

    @Override
    public <T> T merge(final T entity) {
        return threadEntityManager.get().merge(entity);
    }

    @Override
    public void remove(final Object entity) {
        threadEntityManager.get().remove(entity);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return threadEntityManager.get().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey,
            final Map<String, Object> properties) {
        return threadEntityManager.get().find(entityClass, primaryKey,
                properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey,
            final LockModeType lockMode) {
        return threadEntityManager.get()
                .find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey,
            final LockModeType lockMode, final Map<String, Object> properties) {
        return threadEntityManager.get().find(entityClass, primaryKey,
                lockMode, properties);
    }

    @Override
    public <T> T getReference(final Class<T> entityClass,
            final Object primaryKey) {
        return threadEntityManager.get().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        threadEntityManager.get().flush();
    }

    @Override
    public void setFlushMode(final FlushModeType flushMode) {
        threadEntityManager.get().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return threadEntityManager.get().getFlushMode();
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode) {
        threadEntityManager.get().lock(entity, lockMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode,
            final Map<String, Object> properties) {
        threadEntityManager.get().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(final Object entity) {
        threadEntityManager.get().refresh(entity);
    }

    @Override
    public void refresh(final Object entity,
            final Map<String, Object> properties) {
        threadEntityManager.get().refresh(entity, properties);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode) {
        threadEntityManager.get().refresh(entity, lockMode);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode,
            final Map<String, Object> properties) {
        threadEntityManager.get().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        threadEntityManager.get().clear();
    }

    @Override
    public void detach(final Object entity) {
        threadEntityManager.get().detach(entity);
    }

    @Override
    public boolean contains(final Object entity) {
        return threadEntityManager.get().contains(entity);
    }

    @Override
    public LockModeType getLockMode(final Object entity) {
        return threadEntityManager.get().getLockMode(entity);
    }

    @Override
    public void setProperty(final String propertyName, final Object value) {
        threadEntityManager.get().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return threadEntityManager.get().getProperties();
    }

    @Override
    public Query createQuery(final String qlString) {
        return threadEntityManager.get().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return threadEntityManager.get().createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(final CriteriaUpdate updateQuery) {
        return threadEntityManager.get().createQuery(updateQuery);
    }

    @Override
    public Query createQuery(final CriteriaDelete deleteQuery) {
        return threadEntityManager.get().createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String qlString,
            final Class<T> resultClass) {
        return threadEntityManager.get().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(final String name) {
        return threadEntityManager.get().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String name,
            final Class<T> resultClass) {
        return threadEntityManager.get().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        return threadEntityManager.get().createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(final String sqlString,
            final Class resultClass) {
        return threadEntityManager.get().createNativeQuery(sqlString,
                resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString,
            final String resultSetMapping) {
        return threadEntityManager.get().createNativeQuery(sqlString,
                resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(
            final String name) {
        return threadEntityManager.get().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(
            final String procedureName) {
        return threadEntityManager.get().createStoredProcedureQuery(
                procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(
            final String procedureName, final Class... resultClasses) {
        return threadEntityManager.get().createStoredProcedureQuery(
                procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(
            final String procedureName, final String... resultSetMappings) {
        return threadEntityManager.get().createStoredProcedureQuery(
                procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        threadEntityManager.get().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return threadEntityManager.get().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(final Class<T> cls) {
        return threadEntityManager.get().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return threadEntityManager.get().getDelegate();
    }

    @Override
    public void close() {
        threadEntityManager.get().close();
    }

    @Override
    public boolean isOpen() {
        return threadEntityManager.get().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return threadEntityManager.get().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return threadEntityManager.get().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return threadEntityManager.get().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return threadEntityManager.get().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(final Class<T> rootType) {
        return threadEntityManager.get().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(final String graphName) {
        return threadEntityManager.get().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(final String graphName) {
        return threadEntityManager.get().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(
            final Class<T> entityClass) {
        return threadEntityManager.get().getEntityGraphs(entityClass);
    }

}
