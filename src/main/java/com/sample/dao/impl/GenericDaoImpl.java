package com.sample.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Repository;

import com.sample.dao.api.GenericDao;
import com.sample.dao.exception.GenericDaoException;



/**
 * @author ms99658 Utility Class for DB operations.
 * @param <T>
 */
@Configurable
@Repository
public class GenericDaoImpl<T extends Serializable> implements GenericDao<T> {

    private static final Logger LOGGER = Logger.getLogger(GenericDaoImpl.class);

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    public void init() {
        checkEntityManager();
    }

    private void checkEntityManager() {

        EntityManager em = entityManager;
        if (em == null) {
            LOGGER.error("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        }
    }

    @Override
    public void persist(T entity) {
        try {
            this.entityManager.persist(entity);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void persist(Set<T> objects) {
        try {
            for (T object : objects) {
                this.entityManager.persist(object);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public T merge(T entity) {
        try {
            return this.entityManager.merge(entity);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public T get(Class<T> clazz, Serializable id) {
        try {
            return entityManager.find(clazz, id);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getSingleResult(String ejbQl, Map<?, ?> params) {
        try {
            Query query = applyNamedParameters(entityManager.createQuery(ejbQl), params);
            return (T) query.getSingleResult();
        }
        catch (NoResultException ignore) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executeQuery(String ejbQl, Map<?, ?> params) {

        List<?> results;
        try {
            Query query = applyNamedParameters(entityManager.createQuery(ejbQl), params);

            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executeNamedQuery(String name, List<?> params) {

        try {
            List<?> results;

            Query query = entityManager.createNamedQuery(name);
            int i = 0;
            for (Object obj : params) {
                String parameter;
                i++;

                if (obj != null) {
                    parameter = (String) obj;
                    query.setParameter(i, parameter);
                }
            }

            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executeNamedQuery(String name, Map<?, ?> params) {

        try {
            List<?> results;

            Query query = applyNamedParameters(entityManager.createNamedQuery(name), params);
            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> executeNamedQuery(String name, Map<?, ?> params, Class<T> resultClass) {

        try {
            List<?> results;

            Query query = applyNamedParameters(entityManager.createNamedQuery(name, resultClass), params);
            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executeNativeQuery(String sql, Map<String, Object> params) {

        try {
            List<T> results;

            Query query = applyNamedParameters(entityManager.createNativeQuery(sql), params);

            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executeNativeQuery(String sql, Map<String, Object> params, String resultSetMapping) {

        try {
            List<T> results;

            Query query = applyNamedParameters(entityManager.createNativeQuery(sql, resultSetMapping), params);
            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void remove(T object) {
        try {
            entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void removeById(long id, Class<T> objectClass) {
        try {
            Object rootEntity = entityManager.getReference(objectClass, id);
            entityManager.remove(rootEntity);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void persist(List<T> objects) {
        try {
            for (T object : objects) {
                this.entityManager.persist(object);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    /**
     * Get the next sequence number.
     * 
     * @param sequenceName
     *            Sequence Name
     * @return Sequence number @
     */
    @Override
    public Long getNextSequenceNumber(String sequenceName) {
        try {
            StringBuilder sequenceSql = new StringBuilder();
            sequenceSql.append("select ").append(sequenceName).append(".nextval from dual");
            Query query = entityManager.createNativeQuery(sequenceSql.toString());
            List<?> list = query.getResultList();
            BigDecimal queryOutput = (BigDecimal) list.get(0);
            return queryOutput.longValue();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public boolean contains(T entity) {
        try {
            return entityManager.contains(entity);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getResultCount(String name, Map<?, ?> params) {
        try {
            Query query = applyNamedParameters(entityManager.createNamedQuery(name), params);
            return (T) query.getSingleResult();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getById(Class<?> entityClass, Long id) {
        try {
            return (T) this.entityManager.find(entityClass, id);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public int executeUpdate(String ejbQl, Map<?, ?> params) {
        try {
            Query query = applyNamedParameters(entityManager.createQuery(ejbQl), params);
            return query.executeUpdate();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public List<T> getAll(Class<T> clazz) {
        return null;
    }

    @Override
    public int executeUpdateNativeQuery(String sql, Map<String, Object> params) {
        try {
            Query query = applyNamedParameters(entityManager.createNativeQuery(sql), params);
            return query.executeUpdate();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void merge(Set<T> objects) {
        try {
            for (T object : objects) {
                this.entityManager.merge(object);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void refresh(T object) {
        try {
            this.entityManager.refresh(object);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void refresh(Set<T> objects) {
        try {
            for (T object : objects) {
                this.entityManager.refresh(object);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }

    }

    @Override
    public void refresh(List<T> objects) {
        try {
            for (T object : objects) {
                this.entityManager.refresh(object);
            }
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executePaginatedNamedQuery(String name, Map<?, ?> params, int pageNumber, int pageSize) {

        try {
            List<?> results;

            Query query = applyNamedParameters(entityManager.createNamedQuery(name), params);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executePaginatedQuery(String ejbQl, Map<?, ?> params, int pageNumber, int pageSize) {

        try {
            List<?> results;

            Query query = applyNamedParameters(entityManager.createQuery(ejbQl), params);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return (List<T>) results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> executePaginatedNativeQuery(String sql, Map<String, Object> params, String resultSetMapping, int pageNumber, int pageSize) {

        try {
            List<T> results;

            Query query = applyNamedParameters(entityManager.createNativeQuery(sql, resultSetMapping), params);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            results = query.getResultList();

            if (results == null || results.isEmpty()) {
                return Collections.emptyList();
            }
            return results;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getSingleResultWithNamedQuery(String name, Map<?, ?> params) {
        try {
            Query query = applyNamedParameters(entityManager.createNamedQuery(name), params);
            return (T) query.getSingleResult();
        }
        catch (NoResultException ignore) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    private Query applyNamedParameters(Query query, Map<?, ?> params) {
        if (params != null) {
            for (Object obj : params.keySet()) {
                String key = (String) obj;
                query.setParameter(key, params.get(key));
            }
        }
        return query;
    }

    @Override
    public List<T> executeNamedQuery(String name) {
        try {
            return this.executeNamedQuery(name, new HashMap<String, Object>());
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @Override
    public void removeById(Serializable id, Class<T> objectClass) {
        try {
            Object rootEntity = entityManager.getReference(objectClass, id);
            entityManager.remove(rootEntity);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public TypedQuery<T> produceTypedQuery(String query, Class clazz) {
        try {
            return entityManager.createQuery(query, clazz);
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int executeUpdateNamedQuery(String name, Map params) {
        try {
            Query query = this.entityManager.createNamedQuery(name);
            if (params != null) {
                for (Object obj : params.keySet()) {
                    String key = (String) obj;
                    query.setParameter(key, params.get(key));
                }
            }
            return query.executeUpdate();
        }
        catch (Exception e) {
            LOGGER.error(e);
            throw new GenericDaoException(e);
        }
    }
}