package japp.model.repository.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import japp.model.entity.Entity;
import japp.model.repository.Repository;
import japp.model.repository.query.QuerySplitter;
import japp.util.JAppRuntimeException;
import japp.util.Reference;

public class Searcher<T extends Entity, U> {

    private final Repository<T, U> repository;
    private final QuerySplitter querySplitter;

    public Searcher(final Repository<T, U> repository, final QuerySplitter querySplitter) {
        this.repository = repository;
        this.querySplitter = querySplitter;
    }

    protected Repository<?, ?> getRepository() {
        return repository;
    }

    protected QuerySplitter getQuerySplitter() {
        return querySplitter;
    }

    public PageResult<Map<String, Object>> search(
            final List<String> selections,
            final String fromQueryString,
            final String sortQueryString,
            final int firstResult,
            final int maxResults,
            final Object... parameters) {

        return search(selections, fromQueryString, null, sortQueryString, firstResult, maxResults, parameters);
    }

    public PageResult<Map<String, Object>> search(
            final List<String> selections,
            final String fromQueryString,
            final String criteriaQuery,
            final String sortQueryString,
            final int firstResult,
            final int maxResults,
            final Object... parameters) {

        if (selections == null || selections.isEmpty()) {
            throw new JAppRuntimeException("SELECTIONS_SHOULD_NOT_BE_EMPTY");
        }

        final String selectionsQueryString = String.join(", ", selections);
        final String compiledCriteriaQueryString = (criteriaQuery == null || criteriaQuery.isEmpty() ? ""
                : " WHERE " + criteriaQuery);
        final String compiledSortQueryString = (sortQueryString == null || sortQueryString.isEmpty() ? ""
                : " ORDER BY " + sortQueryString);
        final String countQueryString = "SELECT COUNT(*) FROM " + fromQueryString + compiledCriteriaQueryString;
        final TypedQuery<Long> countTypedQuery = getRepository().createTypedQuery(Long.class, countQueryString,
                parameters);

        final TypedQuery<Object[]> searchTypedQuery = getRepository().createTypedQuery(
                Object[].class,
                "SELECT "
                        + selectionsQueryString
                        + " FROM "
                        + fromQueryString + compiledCriteriaQueryString + compiledSortQueryString,
                parameters);
        
        searchTypedQuery.setFirstResult(firstResult);
        searchTypedQuery.setMaxResults(maxResults);

        final Long total = getRepository().count();
        final Long totalFiltered = countTypedQuery.getSingleResult();
        final List<Object[]> resultList = searchTypedQuery.getResultList();

        return new PageResult<>(convertToMappedResultList(selections, resultList), total, totalFiltered, firstResult,
                (long) maxResults);
    }

    @SuppressWarnings("unchecked")
    public PageResult<Map<String, Object>> search(
            final List<SelectionWrapper<?>> selectionWrappers,
            final Predicate predicate,
            final List<Order> orders,
            final int firstResult,
            final int maxResults) {

        if (selectionWrappers == null || selectionWrappers.isEmpty()) {
            throw new JAppRuntimeException("SELECTION_WRAPPERS_SHOULD_NOT_BE_EMPTY");
        }

        final CriteriaQuery<Object[]> criteriaQuery = getRepository().createCriteriaQuery(Object[].class);
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

        final TypedQuery<?> typedQuery = getRepository().createTypedQuery(criteriaQuery);
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(maxResults);

        final Long total = getRepository().count();
        final Long totalFiltered = predicate == null ? total : getRepository().count(predicate);
        final List<Object[]> resultList = (List<Object[]>) typedQuery.getResultList();

        return new PageResult<>(convertToMappedResultList(aliases, resultList), total, totalFiltered, firstResult,
                (long) maxResults);
    }

    public PageResult<Map<String, Object>> deepSearch(
            final List<String> selections,
            final String fromQueryString,
            final String criteriaQuery,
            final String sortQueryString,
            final int firstResult,
            final int maxResults,
            final Object... parameters) {

        return deepSearch(selections, fromQueryString, parseQuery(criteriaQuery), sortQueryString, firstResult,
                maxResults, parameters);
    }

    public PageResult<Map<String, Object>> deepSearch(
            final List<String> selections,
            final String fromQueryString,
            final List<String> criteriaQuery,
            final String sortQueryString,
            final int firstResult,
            final int maxResults,
            final Object... parameters) {

        final List<String> newCriteriaQuery = new ArrayList<>();
        final List<Object> newParameters = new ArrayList<>();
        final List<Object> newStringParameters = new ArrayList<>();
        final Map<Integer, String> replaceStringParameters = new HashMap<>();
        final Reference<Integer> criteriaQueryFragmentStart = new Reference<>(0);

        for (int i = 0; i < parameters.length; i++) {
            final Object parameter = parameters[i];

            newParameters.add(parameter);

            if (parameter != null && parameter instanceof String) {
                final String[] parameterFragments = getQuerySplitter().deepSplit((String) parameter);
                final List<String> duplicatedCriteriaQueryFragment = new ArrayList<>();
                final List<String> criteriaQueryFragmentComplement = new ArrayList<>();
                final String criteriaQueryFragment = getQueryFragment(criteriaQuery, criteriaQueryFragmentStart, i,
                        criteriaQueryFragmentComplement);

                duplicatedCriteriaQueryFragment.add("(");
                duplicatedCriteriaQueryFragment.add(criteriaQueryFragment);

                newCriteriaQuery.addAll(criteriaQueryFragmentComplement);

                for (int j = 1; j < parameterFragments.length; j++) {
                    duplicatedCriteriaQueryFragment.add(" OR ");
                    duplicatedCriteriaQueryFragment.add(criteriaQueryFragment.replace("?" + i,
                            "?" + (parameters.length + newStringParameters.size() + (j - 1))));
                }

                duplicatedCriteriaQueryFragment.add(")");

                Collections.addAll(newCriteriaQuery,
                        duplicatedCriteriaQueryFragment.toArray(new String[duplicatedCriteriaQueryFragment.size()]));

                if (parameterFragments.length > 0) {
                    replaceStringParameters.put(i, fixParameterFragment(parameterFragments, parameterFragments[0]));

                    for (int j = 1; j < parameterFragments.length; j++) {
                        newStringParameters.add(fixParameterFragment(parameterFragments, parameterFragments[j]));
                    }
                }
            } else {
                final List<String> criteriaQueryFragmentComplement = new ArrayList<>();
                final String criteriaQueryFragment = getQueryFragment(criteriaQuery, criteriaQueryFragmentStart, i,
                        criteriaQueryFragmentComplement);

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

        return search(selections, fromQueryString, String.join(" ", newCriteriaQuery), sortQueryString, firstResult,
                maxResults, newParameters.toArray());
    }

    protected List<Map<String, Object>> convertToMappedResultList(
            final List<String> aliases,
            final List<Object[]> resultList) {

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
    protected Map<String, Object> populate(
            final Map<String, Object> map,
            final String alias,
            final Object value) {

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

    private String getQueryFragment(
            final List<String> query,
            final Reference<Integer> start,
            final int index,
            final List<String> complement) {

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
