package japp.model.repository.date;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import japp.model.repository.query.QuerySplitter;
import japp.util.DateHelper;

public class DateParser {

    private final QuerySplitter querySplitter;

    public DateParser(final QuerySplitter querySplitter) {
        this.querySplitter = querySplitter;
    }
    
    protected QuerySplitter getQuerySplitter() {
        return querySplitter;
    }

    public Date parseStartDate(final String query) {
        return getSmallest(parseMultiple(query, "T00:00:00", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"));
    }

    public Date parseEndDate(final String query) {
        return getBiggest(parseMultiple(query, "T23:59:59", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"));
    }

    public Date parseStartDateTime(final String query) {
        return getSmallest(parseMultiple(query, ":00", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"));
    }

    public Date parseEndDateTime(final String query) {
        return getBiggest(parseMultiple(query, ":59", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"));
    }

    public Date parseDateTime(final String query) {
        return DateHelper.parseDate(query, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd");
    }

    public Date parseDate(final String query) {
        return DateHelper.parseDate(query, "yyyy-MM-dd");
    }

    protected Date getSmallest(final Set<Date> dates) {
        return dates.stream()
                .reduce((x, y) -> x == null || y.compareTo(x) <= 0 ? y : x)
                .orElse(null);
    }

    protected Date getBiggest(final Set<Date> dates) {
        return dates.stream()
                .reduce((x, y) -> x == null || y.compareTo(x) >= 0 ? y : x)
                .orElse(null);
    }

    protected Set<Date> parseMultiple(final String query, final String complement, final String... patterns) {
        return getQueryDateFragments(query)
                .stream()
                .map(qdf -> parseSingle(qdf, complement, patterns))
                .filter(d -> d != null)
                .collect(Collectors.toSet());
    }

    protected Date parseSingle(final String queryDateFragment, final String complement, final String... patterns) {
        return DateHelper.parseDate(queryDateFragment + complement, patterns);
    }

    protected Set<String> getQueryDateFragments(final String query) {
        return Arrays.stream(querySplitter.split(query))
                .filter(qf -> parseDateTime(qf) != null || parseDate(qf) != null)
                .collect(Collectors.toSet());
    }
}
