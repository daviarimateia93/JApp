package japp.model.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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
import japp.util.DateHelper;
import japp.util.JAppRuntimeException;
import japp.util.Reference;
import japp.util.Singletonable;

public abstract class Repository<T extends Entity, U> implements Singletonable {
	
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
	
	protected CriteriaQuery<T> createCriteriaQuery(final Reference<Root<T>> returnRoot) {
		return createCriteriaQuery(domainClass, returnRoot);
	}
	
	@SuppressWarnings("hiding")
	protected <T> CriteriaQuery<T> createCriteriaQuery(final Class<T> domainClass) {
		return createCriteriaQuery(domainClass, null);
	}
	
	@SuppressWarnings("hiding")
	protected <T> CriteriaQuery<T> createCriteriaQuery(final Class<T> domainClass, final Reference<Root<T>> returnRoot) {
		final CriteriaQuery<T> criteriaQuery = getCriteriaBuilder().createQuery(domainClass);
		final Root<T> root = criteriaQuery.from(domainClass);
		
		if (returnRoot != null) {
			returnRoot.set(root);
		}
		
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
			return resultList.get(0);
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
			
			return merge(foundEntity);
		}
	}
	
	public void persist(final T entity) {
		entityManager.persist(entity);
	}
	
	public T merge(final T entity) {
		return entityManager.merge(entity);
	}
	
	public void delete(final T entity) {
		final U id = getId(entity);
		
		delete(id);
	}
	
	public void delete(final U id) {
		T foundEntity = get(id);
		
		if (foundEntity != null) {
			entityManager.remove(foundEntity);
		}
	}
	
	public T get(final U id) {
		return entityManager.find(domainClass, id);
	}
	
	public T get(final U id, final LockModeType lockModeType) {
		return entityManager.find(domainClass, id, lockModeType);
	}
	
	public T find(final Predicate predicate) {
		return entityManager.createQuery(createCriteriaQuery().where(predicate)).getSingleResult();
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
	
	public PageResult<Map<String, Object>> search(final List<String> selections, final String fromQueryString, final String criteriaQuery, final String sortQueryString, final int firstResult, final int maxResults, final Object... parameters) {
		if (selections == null || selections.isEmpty()) {
			throw new JAppRuntimeException("SELECTIONS_SHOULD_NOT_BE_EMPTY");
		}
		
		final String selectionsQueryString = String.join(", ", selections);
		final String compiledCriteriaQueryString = (criteriaQuery == null || criteriaQuery.isEmpty() ? "" : " WHERE " + criteriaQuery);
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
	
	public PageResult<Map<String, Object>> deepSearch(final List<String> selections, final String fromQueryString, final String criteriaQuery, final String sortQueryString, final int firstResult, final int maxResults, final Object... parameters) {
		return deepSearch(selections, fromQueryString, parseQuery(criteriaQuery), sortQueryString, firstResult, maxResults, parameters);
	}
	
	public PageResult<Map<String, Object>> deepSearch(final List<String> selections, final String fromQueryString, final List<String> criteriaQuery, final String sortQueryString, final int firstResult, final int maxResults, final Object... parameters) {
		final List<String> newCriteriaQuery = new ArrayList<>();
		final List<Object> newParameters = new ArrayList<>();
		final List<Object> newStringParameters = new ArrayList<>();
		final Map<Integer, String> replaceStringParameters = new HashMap<>();
		final Reference<Integer> criteriaQueryFragmentStart = new Reference<>(0);
		
		for (int i = 0; i < parameters.length; i++) {
			final Object parameter = parameters[i];
			
			newParameters.add(parameter);
			
			if (parameter != null && parameter instanceof String) {
				final String[] parameterFragments = deepSplitQuery((String) parameter);
				final List<String> duplicatedCriteriaQueryFragment = new ArrayList<>();
				final List<String> criteriaQueryFragmentComplement = new ArrayList<>();
				final String criteriaQueryFragment = getQueryFragment(criteriaQuery, criteriaQueryFragmentStart, i, criteriaQueryFragmentComplement);
				
				duplicatedCriteriaQueryFragment.add("(");
				duplicatedCriteriaQueryFragment.add(criteriaQueryFragment);
				
				newCriteriaQuery.addAll(criteriaQueryFragmentComplement);
				
				for (int j = 1; j < parameterFragments.length; j++) {
					duplicatedCriteriaQueryFragment.add(" OR ");
					duplicatedCriteriaQueryFragment.add(criteriaQueryFragment.replace("?" + i, "?" + (parameters.length + newStringParameters.size() + (j - 1))));
				}
				
				duplicatedCriteriaQueryFragment.add(")");
				
				Collections.addAll(newCriteriaQuery, duplicatedCriteriaQueryFragment.toArray(new String[duplicatedCriteriaQueryFragment.size()]));
				
				if (parameterFragments.length > 0) {
					replaceStringParameters.put(i, fixParameterFragment(parameterFragments, parameterFragments[0]));
					
					for (int j = 1; j < parameterFragments.length; j++) {
						newStringParameters.add(fixParameterFragment(parameterFragments, parameterFragments[j]));
					}
				}
			} else {
				final List<String> criteriaQueryFragmentComplement = new ArrayList<>();
				final String criteriaQueryFragment = getQueryFragment(criteriaQuery, criteriaQueryFragmentStart, i, criteriaQueryFragmentComplement);
				
				newCriteriaQuery.addAll(criteriaQueryFragmentComplement);
				newCriteriaQuery.add(criteriaQueryFragment);
			}
		}
		
		for (int i = criteriaQueryFragmentStart.get(); i < criteriaQuery.size(); i++) {
			newCriteriaQuery.add(criteriaQuery.get(i));
		}
		
		newParameters.addAll(newStringParameters);
		
		for (final Map.Entry<Integer, String> entry : replaceStringParameters.entrySet()) {
			newParameters.set(entry.getKey(), entry.getValue());
		}
		
		return search(selections, fromQueryString, String.join(" ", newCriteriaQuery), sortQueryString, firstResult, maxResults, newParameters.toArray());
	}
	
	protected List<Map<String, Object>> convertToMappedResultList(final List<String> aliases, final List<Object[]> resultList) {
		final List<Map<String, Object>> mappedResultList = new ArrayList<>();
		
		if (resultList != null) {
			for (final Object[] result : resultList) {
				final Map<String, Object> map = new HashMap<>();
				
				for (int i = 0; i < result.length; i++) {
					final String alias = aliases.get(i).split("_")[0];
					
					if (alias.contains(".")) {
						populate(map, alias.substring(alias.indexOf('.') + 1), result[i]);
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
	
	protected Date parseStartDate(final String query) {
		return getSmallestDate(parseDates(query, " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
	}
	
	protected Date parseEndDate(final String query) {
		return getBiggestDate(parseDates(query, " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
	}
	
	protected Date parseStartDateTime(final String query) {
		return getSmallestDate(parseDates(query, ":00", "yyyy-MM-dd HH:mm:ss"));
	}
	
	protected Date parseEndDateTime(final String query) {
		return getBiggestDate(parseDates(query, ":59", "yyyy-MM-dd HH:mm:ss"));
	}
	
	protected Date parseDateTime(final String query) {
		return DateHelper.parseDate(query, "yyyy-MM-dd HH:mm:ss");
	}
	
	protected Date parseDate(final String query) {
		return DateHelper.parseDate(query, "yyyy-MM-dd");
	}
	
	private Date getSmallestDate(final Date[] dates) {
		Date smallestDate = null;
		
		for (final Date date : dates) {
			if (smallestDate == null || date.compareTo(smallestDate) <= 0) {
				smallestDate = date;
			}
		}
		
		return smallestDate;
	}
	
	private Date getBiggestDate(final Date[] dates) {
		Date biggestDate = null;
		
		for (final Date date : dates) {
			if (biggestDate == null || date.compareTo(biggestDate) >= 0) {
				biggestDate = date;
			}
		}
		
		return biggestDate;
	}
	
	private Date[] parseDates(final String query, final String complement, final String pattern) {
		final Set<Date> dates = new HashSet<>();
		final String[] queryDateFragments = getQueryDateFragments(query);
		
		for (final String queryDateFragment : queryDateFragments) {
			final Date date = parseDate(queryDateFragment, complement, pattern);
			
			if (date != null) {
				dates.add(date);
			}
		}
		
		return dates.toArray(new Date[dates.size()]);
	}
	
	private Date parseDate(final String queryDateFragment, final String complement, final String pattern) {
		return DateHelper.parseDate(queryDateFragment + complement, pattern);
	}
	
	protected String getQueryGlue(final String query) {
		final String[] splittedQuery = splitQuery(query);
		int counter = splittedQuery.length;
		
		for (final String queryFragment : splittedQuery) {
			final Date dateTime = parseDateTime(queryFragment);
			final Date date = parseDate(queryFragment);
			
			if (dateTime != null || date != null) {
				counter--;
			}
		}
		
		return counter > 0 && counter < splittedQuery.length ? "AND" : "OR";
	}
	
	private String[] getQueryDateFragments(final String query) {
		final Set<String> queryDateFragments = new HashSet<>();
		final String[] splittedQuery = splitQuery(query);
		
		for (final String queryFragment : splittedQuery) {
			final Date dateTime = parseDateTime(queryFragment);
			final Date date = parseDate(queryFragment);
			
			if (dateTime != null || date != null) {
				queryDateFragments.add(queryFragment);
			}
		}
		
		return queryDateFragments.toArray(new String[queryDateFragments.size()]);
	}
	
	protected char getDeepSplitQueryEscape() {
		return '"';
	}
	
	private String[] splitQuery(final String query) {
		return query.trim().split(" +");
	}
	
	private String[] deepSplitQuery(final String query) {
		final List<String> splitted = new ArrayList<>();
		final String newQuery = query.trim().replaceAll(" +", " ");
		final StringBuilder stringBuilder = new StringBuilder();
		final char escape = getDeepSplitQueryEscape();
		boolean foundEscape = false;
		
		for (int i = 0; i < newQuery.length(); i++) {
			final char character = newQuery.charAt(i);
			
			if (character == escape && !foundEscape) {
				addAndEmptyStringBuilder(splitted, stringBuilder);
				
				foundEscape = true;
				continue;
			}
			
			if (character == escape && foundEscape) {
				addAndEmptyStringBuilder(splitted, stringBuilder);
				
				foundEscape = false;
				continue;
			}
			
			if (foundEscape) {
				stringBuilder.append(character);
			}
			
			if (character != ' ' && !foundEscape) {
				stringBuilder.append(character);
			}
			
			if (character == ' ' && !foundEscape) {
				addAndEmptyStringBuilder(splitted, stringBuilder);
			}
		}
		
		addAndEmptyStringBuilder(splitted, stringBuilder);
		
		if (splitted.size() > 1 && splitted.get(0).equals("%")) {
			splitted.remove(0);
			splitted.set(0, "%" + splitted.get(0));
		}
		
		if (splitted.size() > 1 && splitted.get(splitted.size() - 1).equals("%")) {
			splitted.remove(splitted.size() - 1);
			splitted.set(splitted.size() - 1, splitted.get(splitted.size() - 1) + "%");
		}
		
		return splitted.toArray(new String[splitted.size()]);
	}
	
	private void addAndEmptyStringBuilder(final List<String> strings, final StringBuilder stringBuilder) {
		if (!stringBuilder.toString().isEmpty()) {
			strings.add(stringBuilder.toString());
			stringBuilder.setLength(0);
		}
	}
	
	private String fixParameterFragment(final String[] parameterFragments, final String parameterFragment) {
		final String firstParameterFragment = parameterFragments[0];
		final String lastParameterFragment = parameterFragments[parameterFragments.length - 1];
		final StringBuilder stringBuilder = new StringBuilder();
		
		if (firstParameterFragment.startsWith("%") && !parameterFragment.startsWith("%")) {
			stringBuilder.append('%');
		}
		
		stringBuilder.append(parameterFragment);
		
		if (lastParameterFragment.endsWith("%") && !parameterFragment.endsWith("%")) {
			stringBuilder.append('%');
		}
		
		return stringBuilder.toString();
	}
	
	private String getQueryFragment(final List<String> query, final Reference<Integer> start, final int index, final List<String> complement) {
		for (int i = start.get(); i < query.size(); start.set(++i)) {
			final String queryFragment = query.get(i);
			
			if (queryFragment.contains("?" + index)) {
				start.set(++i);
				return queryFragment;
			} else {
				complement.add(queryFragment);
			}
		}
		
		return "";
	}
	
	private List<String> parseQuery(final String query) {
		final List<String> queryFragments = new ArrayList<>();
		final StringBuilder stringBuilder = new StringBuilder();
		
		boolean endStatement = true;
		boolean endString = true;
		
		for (int i = 0; i < query.length(); i++) {
			char previousCharacter = i > 0 ? query.charAt(i - 1) : '\u0000';
			char character = query.charAt(i);
			boolean skip = false;
			
			if (endString && character == '[') {
				endStatement = false;
				skip = true;
				addQueryFragment(queryFragments, stringBuilder);
			} else if (!endStatement && endString && character == ']') {
				endStatement = true;
				skip = true;
				addQueryFragment(queryFragments, stringBuilder);
			} else if (!endStatement && endString && character == '\'') {
				endString = false;
			} else if (!endStatement && !endString && character == '\'' && previousCharacter != '\\') {
				endString = true;
			}
			
			if (!skip) {
				stringBuilder.append(character);
			}
		}
		
		addQueryFragment(queryFragments, stringBuilder);
		
		return queryFragments;
	}
	
	private void addQueryFragment(final List<String> queryFragments, final StringBuilder stringBuilder) {
		if (stringBuilder.length() > 0) {
			queryFragments.add(stringBuilder.toString());
			stringBuilder.setLength(0);
		}
	}
}
