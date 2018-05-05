package japp.model.repository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import japp.model.entity.Entity;
import japp.model.repository.date.DateParser;
import japp.model.repository.query.QuerySplitter;
import japp.model.repository.search.Searcher;
import japp.util.JAppRuntimeException;
import japp.util.Reference;
import japp.util.Singletonable;

public abstract class Repository<T extends Entity, U> implements Singletonable {

    private final Class<T> domainClass;
    private final EntityManager entityManager;
    private final DateParser dateParser;
    private final QuerySplitter querySplitter;
    private final Searcher<T, U> searcher;

    protected Repository(final Class<T> domainClass, final EntityManager entityManager) {
        this.domainClass = domainClass;
        this.entityManager = entityManager;
        this.querySplitter = new QuerySplitter();
        this.dateParser = new DateParser(getQuerySplitter());
        this.searcher = new Searcher<>(this, getQuerySplitter());
    }

    protected Class<T> getDomainClass() {
        return domainClass;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected DateParser getDateParser() {
        return dateParser;
    }

    protected QuerySplitter getQuerySplitter() {
        return querySplitter;
    }

    protected Searcher<T, U> getSearcher() {
        return searcher;
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    public CriteriaQuery<T> createCriteriaQuery() {
        return createCriteriaQuery(getDomainClass());
    }

    public CriteriaQuery<T> createCriteriaQuery(final Reference<Root<T>> returnRoot) {
        return createCriteriaQuery(getDomainClass(), returnRoot);
    }

    @SuppressWarnings("hiding")
    public <T> CriteriaQuery<T> createCriteriaQuery(final Class<T> domainClass) {
        return createCriteriaQuery(domainClass, null);
    }

    @SuppressWarnings("hiding")
    public <T> CriteriaQuery<T> createCriteriaQuery(final Class<T> domainClass,
            final Reference<Root<T>> returnRoot) {
        final CriteriaQuery<T> criteriaQuery = getCriteriaBuilder().createQuery(domainClass);
        final Root<T> root = criteriaQuery.from(domainClass);

        if (returnRoot != null) {
            returnRoot.set(root);
        }

        return criteriaQuery.select(root);
    }

    public TypedQuery<T> createTypedQuery() {
        return createTypedQuery(createCriteriaQuery());
    }

    @SuppressWarnings("hiding")
    public <T> TypedQuery<T> createTypedQuery(final CriteriaQuery<T> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    public TypedQuery<T> createTypedQuery(final String queryString, final Object... parameters) {
        return createTypedQuery(getDomainClass(), queryString, parameters);
    }

    @SuppressWarnings("hiding")
    public <T> TypedQuery<T> createTypedQuery(final Class<T> domainClass, final String queryString,
            final Object... parameters) {
        final TypedQuery<T> typedQuery = getEntityManager().createQuery(queryString, domainClass);

        setQueryParameters(typedQuery, parameters);

        return typedQuery;
    }

    public Query createQuery(final String queryString, final Object... parameters) {
        final Query query = getEntityManager().createQuery(queryString);

        setQueryParameters(query, parameters);

        return query;
    }

    public Query createNativeQuery(final String queryString, final Object... parameters) {
        final Query query = getEntityManager().createNativeQuery(queryString, getDomainClass());

        setQueryParameters(query, parameters);

        return query;
    }

    protected void setQueryParameters(final Query query, final Object... parameters) {
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i, parameters[i]);
        }
    }

    @SuppressWarnings("unchecked")
    protected <V> V getSingleResult(final Query query) {
        return (V) getSingleResult(query.getResultList());
    }

    protected <V> V getSingleResult(final List<V> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    protected TypedQuery<Long> getCountTypedQuery(final Predicate predicate) {
        final CriteriaBuilder criteriaBuilder = getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<T> root = criteriaQuery.from(getDomainClass());

        if (predicate != null) {
            criteriaQuery.where(predicate);
        }

        if (criteriaQuery.isDistinct()) {
            criteriaQuery.select(criteriaBuilder.countDistinct(root));
        } else {
            criteriaQuery.select(criteriaBuilder.count(root));
        }

        return createTypedQuery(criteriaQuery);
    }

    public Long count() {
        return count(null);
    }

    public Long count(final Predicate predicate) {
        final TypedQuery<Long> typedQuery = getCountTypedQuery(predicate);
        final List<Long> totals = typedQuery.getResultList();

        Long count = 0L;

        for (final Long total : totals) {
            count += total == null ? 0 : total;
        }

        return count;
    }

    @SuppressWarnings("unchecked")
    public U getId(final T entity) {
        return (U) getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    public T save(final T entity) {
        final U id = getId(entity);

        if (id == null) {
            persist(entity);

            return entity;
        } else {
            final T foundEntity = get(id);

            if (foundEntity == null) {
                throw new JAppRuntimeException("ENTITY_NOT_FOUND");
            }

            foundEntity.merge(entity);

            return merge(foundEntity);
        }
    }

    public void persist(final T entity) {
        getEntityManager().persist(entity);
    }

    public T merge(final T entity) {
        return getEntityManager().merge(entity);
    }

    public void delete(final T entity) {
        final U id = getId(entity);

        delete(id);
    }

    public void delete(final U id) {
        T foundEntity = get(id);

        if (foundEntity != null) {
            getEntityManager().remove(foundEntity);
        }
    }

    public T get(final U id) {
        return getEntityManager().find(getDomainClass(), id);
    }

    public T get(final U id, final LockModeType lockModeType) {
        return getEntityManager().find(getDomainClass(), id, lockModeType);
    }

    public T find(final Predicate predicate) {
        return getEntityManager().createQuery(createCriteriaQuery().where(predicate)).getSingleResult();
    }

    public T findAny() {
        final long count = count();
        final long randomFirstResult = ThreadLocalRandom.current().nextLong(0, count);

        final TypedQuery<T> typedQuery = createTypedQuery();
        typedQuery.setFirstResult((int) randomFirstResult);
        typedQuery.setMaxResults(1);

        return getSingleResult(typedQuery);
    }

    public T findFirst() {
        final TypedQuery<T> typedQuery = createTypedQuery();
        typedQuery.setMaxResults(1);

        return getSingleResult(typedQuery);
    }

    public List<T> findAll() {
        return getEntityManager().createQuery(createCriteriaQuery()).getResultList();
    }

    public List<T> findAll(final Predicate predicate) {
        return getEntityManager().createQuery(createCriteriaQuery().where(predicate)).getResultList();
    }

    public int executeUpdate(final String queryString, final Object... parameters) {
        return createQuery(queryString, parameters).executeUpdate();
    }

    public T getSingleResult(final String queryString, final Object... parameters) {
        return getSingleResult(getResultList(queryString, parameters));
    }

    public <V> V getSingleResult(final Class<V> domainClass, final String queryString, final Object... parameters) {
        return getSingleResult(getResultList(domainClass, queryString, parameters));
    }

    public List<T> getResultList(final String queryString, final Object... parameters) {
        return createTypedQuery(queryString, parameters).getResultList();
    }

    public <V> List<V> getResultList(final Class<V> domainClass, final String queryString, final Object... parameters) {
        return createTypedQuery(domainClass, queryString, parameters).getResultList();
    }

    public T getNativeSingleResult(final String queryString, final Object... parameters) {
        return (T) getSingleResult(getNativeResultList(queryString, parameters));
    }

    @SuppressWarnings("unchecked")
    public List<T> getNativeResultList(final String queryString, final Object... parameters) {
        return (List<T>) createNativeQuery(queryString, parameters).getResultList();
    }

    protected String getQueryGlue(final String query) {
        final String[] splittedQuery = querySplitter.split(query);
        
        final int counter = Arrays.stream(splittedQuery)
                .filter(qf -> getDateParser().parseDateTime(qf) != null || getDateParser().parseDate(qf) != null)
                .map(qf -> 1)
                .reduce(splittedQuery.length, (x, y) -> x--);

        return counter > 0 && counter < splittedQuery.length ? "AND" : "OR";
    }
}
