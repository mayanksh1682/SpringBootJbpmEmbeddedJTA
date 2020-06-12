package com.sample.dao.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sample.dao.exception.GenericDaoException;

/**
 * @author ms99658 Interface represents implementation of generic DAO.
 * @param <T>
 *            object's type, it must extend at least {@link BaseModel}
 */

public interface GenericDao<T> {

    /**
     * Make an instance managed and persistent.
     * 
     * @param entity
     * @throws GenericDaoException
     */
    void persist(T entity) throws GenericDaoException;

    /**
     * @param entity
     * @return
     * @throws GenericDaoException
     */
    T merge(T entity) throws GenericDaoException;

    /**
     * @param clazz
     * @param id
     * @return
     * @throws GenericDaoException
     */
    T get(Class<T> clazz, Serializable id) throws GenericDaoException;

    /**
     * @param name
     * @param params
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNamedQuery(String name, List<?> params) throws GenericDaoException;

    /**
     * @param name
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNamedQuery(String name) throws GenericDaoException;

    /**
     * @param name
     * @param params
     * @return
     * @throws GenericDaoException
     */
    T getResultCount(String name, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param name
     * @param params
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNamedQuery(String name, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param name
     * @param params
     * @param resultClass
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNamedQuery(String name, Map<?, ?> params, Class<T> resultClass) throws GenericDaoException;

    /**
     * @param entityClass
     * @param id
     * @return
     * @throws GenericDaoException
     */
    T getById(Class<?> entityClass, Long id) throws GenericDaoException;

    /**
     * @param objects
     * @throws GenericDaoException
     */
    void persist(Set<T> objects) throws GenericDaoException;

    /**
     * Not implemented
     * 
     * @param clazz
     * @return
     * @throws GenericDaoException
     */
    List<T> getAll(Class<T> clazz) throws GenericDaoException;

    /**
     * @param objects
     * @throws GenericDaoException
     */
    void persist(List<T> objects) throws GenericDaoException;

    /**
     * @param ejbQl
     * @param params
     * @return
     * @throws GenericDaoException
     */
    List<T> executeQuery(String ejbQl, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param ejbQl
     * @param params
     * @return
     * @throws GenericDaoException
     */
    T getSingleResult(String ejbQl, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param name
     * @param params
     * @return
     * @throws GenericDaoException
     */
    T getSingleResultWithNamedQuery(String name, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param sequenceName
     * @return
     * @throws GenericDaoException
     */
    Long getNextSequenceNumber(String sequenceName) throws GenericDaoException;

    /**
     * @param entity
     * @return
     * @throws GenericDaoException
     */
    boolean contains(T entity) throws GenericDaoException;

    /**
     * @param ejbQl
     * @param params
     * @return
     * @throws GenericDaoException
     */
    int executeUpdate(String ejbQl, Map<?, ?> params) throws GenericDaoException;

    /**
     * @param sql
     * @param params
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNativeQuery(String sql, Map<String, Object> params) throws GenericDaoException;

    /**
     * @param sql
     * @param params
     * @param resultSetMapping
     * @return
     * @throws GenericDaoException
     */
    List<T> executeNativeQuery(String sql, Map<String, Object> params, String resultSetMapping) throws GenericDaoException;

    /**
     * @param object
     * @throws GenericDaoException
     */
    void remove(T object) throws GenericDaoException;

    /**
     * @param sql
     * @param params
     * @return
     * @throws GenericDaoException
     */
    int executeUpdateNativeQuery(String sql, Map<String, Object> params) throws GenericDaoException;

    /**
     * @param objects
     * @throws GenericDaoException
     */
    void merge(Set<T> objects) throws GenericDaoException;

    /**
     * Remove entity by Id
     * 
     * @param id
     * @param objectClass
     * @throws GenericDaoException
     */
    void removeById(long id, Class<T> objectClass) throws GenericDaoException;

    /**
     * Remove a entity by serialized id
     * 
     * @param id
     * @param objectClass
     * @throws GenericDaoException
     */
    void removeById(Serializable id, Class<T> objectClass) throws GenericDaoException;

    /**
     * Refresh teh entity from database
     * 
     * @param object
     * @throws GenericDaoException
     */
    void refresh(T object) throws GenericDaoException;

    /**
     * Refresh the set of entities from database.
     * 
     * @param objects
     * @throws GenericDaoException
     */
    void refresh(Set<T> objects) throws GenericDaoException;

    /**
     * Refresh list of entities from database.
     * 
     * @param objects
     * @throws GenericDaoException
     */
    void refresh(List<T> objects) throws GenericDaoException;

    /**
     * Executes paginated named query.
     * 
     * @param name
     * @param params
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws GenericDaoException
     */
    List<T> executePaginatedNamedQuery(String name, Map<?, ?> params, int pageNumber, int pageSize) throws GenericDaoException;

    /**
     * Executes paginated ejbQl.
     * 
     * @param ejbQl
     * @param params
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws GenericDaoException
     */
    List<T> executePaginatedQuery(String ejbQl, Map<?, ?> params, int pageNumber, int pageSize) throws GenericDaoException;

    /**
     * Executes paginated native query.
     * 
     * @param sql
     * @param params
     * @param resultSetMapping
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws GenericDaoException
     */
    List<T> executePaginatedNativeQuery(String sql, Map<String, Object> params, String resultSetMapping, int pageNumber, int pageSize) throws GenericDaoException;

    /**
     * Gets handle to entity manager
     * 
     * @return
     * @throws GenericDaoException
     */
    EntityManager getEntityManager() throws GenericDaoException;

    /**
     * Produces a typed query.
     * 
     * @param query
     * @param clazz
     * @return
     * @throws GenericDaoException
     */
    @SuppressWarnings("rawtypes")
    TypedQuery<T> produceTypedQuery(String query, Class clazz) throws GenericDaoException;

    /**
     * Named query to update database.
     * 
     * @param name
     * @param params
     * @return
     * @throws GenericDaoException
     */
    @SuppressWarnings("rawtypes")
    int executeUpdateNamedQuery(String name, Map params) throws GenericDaoException;

}
