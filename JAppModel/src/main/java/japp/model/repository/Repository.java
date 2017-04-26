package japp.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import japp.model.entity.Entity;
import japp.model.repository.search.PageResult;
import japp.model.repository.search.SelectionWrapper;
import japp.util.JAppRuntimeException;

public abstract class Repository<T extends Entity, U> {
	
	private final Class<T> domainClass;
	private final EntityManager entityManager;
	
	protected Repository(final Class<T> domainClass, final EntityManager entityManager) {
		this.domainClass = domainClass;
		this.entityManager = entityManager;
	}
	
	protected Class<T> getDomainClass() {
		return domainClass;
	}
	
	protected EntityManager getEntityManager() {
		return entityManager;
	}
	
	protected CriteriaBuilder getCriteriaBuilder() {
		return entityManager.getCriteriaBuilder();
	}
	
	protected CriteriaQuery<T> createCriteriaQuery() {
		return createCriteriaQuery(domainClass);
	}
	
	@SuppressWarnings("hiding")
	protected <T> CriteriaQuery<T> createCriteriaQuery(final Class<T> domainClass) {
		final CriteriaQuery<T> criteriaQuery = getCriteriaBuilder().createQuery(domainClass);
		final Root<T> root = criteriaQuery.from(domainClass);
		
		return criteriaQuery.select(root);
	}
	
	protected TypedQuery<T> createTypedQuery() {
		return createTypedQuery(createCriteriaQuery());
	}
	
	@SuppressWarnings("hiding")
	protected <T> TypedQuery<T> createTypedQuery(final CriteriaQuery<T> criteriaQuery) {
		return entityManager.createQuery(criteriaQuery);
	}
	
	protected TypedQuery<T> createTypedQuery(final String queryString, final Object... parameters) {
		return createTypedQuery(domainClass, queryString, parameters);
	}
	
	@SuppressWarnings("hiding")
	protected <T> TypedQuery<T> createTypedQuery(final Class<T> domainClass, final String queryString, final Object... parameters) {
		final TypedQuery<T> typedQuery = entityManager.createQuery(queryString, domainClass);
		
		setQueryParameters(typedQuery, parameters);
		
		return typedQuery;
	}
	
	protected Query createQuery(final String queryString, final Object... parameters) {
		final Query query = entityManager.createQuery(queryString);
		
		setQueryParameters(query, parameters);
		
		return query;
	}
	
	protected Query createNativeQuery(final String queryString, final Object... parameters) {
		final Query query = entityManager.createNativeQuery(queryString, domainClass);
		
		setQueryParameters(query, parameters);
		
		return query;
	}
	
	protected void setQueryParameters(final Query query, final Object... parameters) {
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				query.setParameter(i, parameters[i]);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected T getSingleResult(final Query query) {
		return (T) getSingleResult(query.getResultList());
	}
	
	protected T getSingleResult(final List<T> resultList) {
		if (resultList == null || resultList.isEmpty()) {
			return null;
		} else {
			return (T) resultList.get(0);
		}
	}
	
	protected TypedQuery<Long> getCountTypedQuery(final Predicate predicate) {
		final CriteriaBuilder criteriaBuilder = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		final Root<T> root = criteriaQuery.from(domainClass);
		
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
		return (U) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
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
			
			final T mergedEntity = merge(foundEntity);
			
			return mergedEntity;
		}
	}
	
	public void persist(final T entity) {
		entityManager.persist(entity);
		
		flush();
	}
	
	public T merge(final T entity) {
		final T mergedEntity = entityManager.merge(entity);
		
		flush();
		
		return mergedEntity;
	}
	
	public void flush() {
		entityManager.flush();
	}
	
	public void delete(final T entity) {
		final U id = getId(entity);
		
		delete(id);
	}
	
	public void delete(final U id) {
		T foundEntity = get(id);
		
		if (foundEntity != null) {
			entityManager.remove(foundEntity);
			entityManager.flush();
		}
	}
	
	public T get(final U id) {
		return entityManager.find(domainClass, id);
	}
	
	public T find(final Predicate predicate) {
		return entityManager.createQuery(createCriteriaQuery().where(predicate)).getSingleResult();
	}
	
	public T findFirst() {
		final TypedQuery<T> typedQuery = createTypedQuery();
		typedQuery.setMaxResults(1);
		
		return getSingleResult(typedQuery);
	}
	
	public List<T> findAll() {
		return entityManager.createQuery(createCriteriaQuery()).getResultList();
	}
	
	public List<T> findAll(final Predicate predicate) {
		return entityManager.createQuery(createCriteriaQuery().where(predicate)).getResultList();
	}
	
	public int executeUpdate(final String queryString, final Object... parameters) {
		return createQuery(queryString, parameters).executeUpdate();
	}
	
	public T getSingleResult(final String queryString, final Object... parameters) {
		return getSingleResult(getResultList(queryString, parameters));
	}
	
	public List<T> getResultList(final String queryString, final Object... parameters) {
		return createTypedQuery(queryString, parameters).getResultList();
	}
	
	public T getNativeSingleResult(final String queryString, final Object... parameters) {
		return (T) getSingleResult(getNativeResultList(queryString, parameters));
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getNativeResultList(final String queryString, final Object... parameters) {
		return (List<T>) createNativeQuery(queryString, parameters).getResultList();
	}
	
	public PageResult<Map<String, Object>> search(final List<String> selections, final String fromQueryString, final String sortQueryString, final int firstResult, final int maxResults, final Object... parameters) {
		return search(selections, fromQueryString, null, sortQueryString, firstResult, maxResults, parameters);
	}
	
	public PageResult<Map<String, Object>> search(final List<String> selections, final String fromQueryString, final List<String> criteriaQuery, final String sortQueryString, final int firstResult, final int maxResults, final Object... parameters) {
		if (selections == null || selections.isEmpty()) {
			throw new JAppRuntimeException("SELECTIONS_SHOULD_NOT_BE_EMPTY");
		}
		
		final String selectionsQueryString = String.join(", ", selections);
		final String compiledCriteriaQueryString = (criteriaQuery == null || criteriaQuery.isEmpty() ? "" : " WHERE " + String.join(" ", criteriaQuery));
		final String compiledSortQueryString = (sortQueryString == null || sortQueryString.isEmpty() ? "" : " ORDER BY " + sortQueryString);
		final String countQueryString = "SELECT COUNT(*) FROM " + fromQueryString + compiledCriteriaQueryString;
		final TypedQuery<Long> countTypedQuery = createTypedQuery(Long.class, countQueryString, parameters);
		
		final TypedQuery<Object[]> searchTypedQuery = createTypedQuery(Object[].class, "SELECT " + selectionsQueryString + " FROM " + fromQueryString + compiledCriteriaQueryString + compiledSortQueryString, parameters);
		searchTypedQuery.setFirstResult(firstResult);
		searchTypedQuery.setMaxResults(maxResults);
		
		final Long total = count();
		final Long totalFiltered = countTypedQuery.getSingleResult();
		final List<Object[]> resultList = searchTypedQuery.getResultList();
		
		return new PageResult<>(convertToMappedResultList(selections, resultList), total, totalFiltered, firstResult, (long) maxResults);
	}
	
	@SuppressWarnings("unchecked")
	public PageResult<Map<String, Object>> search(final List<SelectionWrapper<?>> selectionWrappers, final Predicate predicate, final List<Order> orders, final int firstResult, final int maxResults) {
		if (selectionWrappers == null || selectionWrappers.isEmpty()) {
			throw new JAppRuntimeException("SELECTION_WRAPPERS_SHOULD_NOT_BE_EMPTY");
		}
		
		final CriteriaQuery<Object[]> criteriaQuery = createCriteriaQuery(Object[].class);
		final List<String> aliases = new ArrayList<>();
		final List<Selection<?>> selections = new ArrayList<>();
		
		for (final SelectionWrapper<?> selectionWrapper : selectionWrappers) {
			aliases.add(selectionWrapper.getAlias());
			selections.add(selectionWrapper.getSelection());
		}
		
		criteriaQuery.multiselect(selections);
		
		if (predicate != null) {
			criteriaQuery.where(predicate);
		}
		
		if (orders != null && !orders.isEmpty()) {
			criteriaQuery.orderBy(orders);
		}
		
		final TypedQuery<?> typedQuery = createTypedQuery(criteriaQuery);
		typedQuery.setFirstResult(firstResult);
		typedQuery.setMaxResults(maxResults);
		
		final Long total = count();
		final Long totalFiltered = predicate == null ? total : count(predicate);
		final List<Object[]> resultList = (List<Object[]>) typedQuery.getResultList();
		
		return new PageResult<>(convertToMappedResultList(aliases, resultList), total, totalFiltered, firstResult, (long) maxResults);
	}
	
	protected List<Map<String, Object>> convertToMappedResultList(final List<String> aliases, final List<Object[]> resultList) {
		final List<Map<String, Object>> mappedResultList = new ArrayList<>();
		
		if (resultList != null) {
			for (final Object[] result : resultList) {
				final Map<String, Object> map = new HashMap<>();
				
				for (int i = 0; i < result.length; i++) {
					final String alias = aliases.get(i).split("_")[0];
					
					if (alias.contains(".")) {
						populate(map, alias.substring(alias.indexOf(".") + 1), result[i]);
					} else {
						throw new JAppRuntimeException("INVALID_PATH");
					}
				}
				
				mappedResultList.add(map);
			}
		}
		
		return mappedResultList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object search(final Map<String, Object> map, final String alias) {
		Map<String, Object> auxMap = new HashMap<>();
		
		for (String fragment : alias.split("\\.")) {
			if (auxMap.containsKey(fragment)) {
				if (auxMap.get(fragment) instanceof Map) {
					auxMap = (Map) auxMap.get(fragment);
				} else {
					return auxMap.get(fragment);
				}
			} else {
				break;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map<String, Object> populate(final Map<String, Object> map, final String alias, final Object value) {
		final String[] fragments = alias.split("\\.");
		
		if (fragments.length == 1) {
			map.put(fragments[0], value);
		} else {
			Map<String, Object> auxMap = map;
			
			for (int k = 0; k < fragments.length; k++) {
				if (k == fragments.length - 1) {
					auxMap.put(fragments[k], value);
				} else {
					if (auxMap.containsKey(fragments[k])) {
						auxMap = (Map) auxMap.get(fragments[k]);
						continue;
					}
					
					final Map<String, Object> nestedAuxMap = new HashMap<>();
					auxMap.put(fragments[k], nestedAuxMap);
					auxMap = nestedAuxMap;
				}
			}
		}
		
		return map;
	}
}
