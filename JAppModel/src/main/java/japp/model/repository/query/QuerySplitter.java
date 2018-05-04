package japp.model.repository.query;

import java.util.ArrayList;
import java.util.List;

public class QuerySplitter {

    public String[] split(final String query) {
        return query.trim().split(" +");
    }

    protected char getDeepSplitEscape() {
        return '"';
    }

    public String[] deepSplit(final String query) {
        final List<String> splitted = new ArrayList<>();
        final String newQuery = query.trim().replaceAll(" +", " ");
        final StringBuilder stringBuilder = new StringBuilder();
        final char escape = getDeepSplitEscape();
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
}
