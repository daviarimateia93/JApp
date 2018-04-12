package japp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FormDataHelper {

    protected FormDataHelper() {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> parse(final String queryString) {
        final Map<String, Object> formData = new HashMap<>();

        for (final String nameAndValue : queryString.split("\\&")) {
            final String[] splittedNameAndValue = nameAndValue.split("\\=");
            final String name = splittedNameAndValue[0];
            final String value = splittedNameAndValue.length > 0 ? StringHelper.urlDecode(splittedNameAndValue[1])
                    : null;
            final String[] pathFragments = name.split("\\.");

            // begin: control variables
            Object formDataNode = formData;
            Integer index = null;
            boolean lastIsList = false;
            Integer lastIndex = null;
            String lastKey = null;
            // end: control variables

            // begin: path parser
            for (int i = 0; i < pathFragments.length; i++) {
                final String pathFragment = pathFragments[i];
                final boolean isList = isList(pathFragment);

                if (isList) {
                    index = getIndex(pathFragment);

                    if (lastIndex == null) {
                        if (formDataNode instanceof Map) {
                            final String formDataNodeKey = pathFragment.substring(0, pathFragment.indexOf('['));
                            final Map<String, Object> lastKeyMap = new HashMap<>();

                            if (lastKey != null) {
                                ((Map) formDataNode).put(lastKey, lastKeyMap);
                                formDataNode = lastKeyMap;
                                lastKey = null;
                            }

                            if (((Map) formDataNode).containsKey(formDataNodeKey)) {
                                formDataNode = ((List) ((Map) formDataNode).get(formDataNodeKey));
                            } else {
                                final List<Object> formDataNodeValue = new ArrayList<>();
                                ((Map) formDataNode).put(formDataNodeKey, formDataNodeValue);
                                formDataNode = formDataNodeValue;
                            }
                        } else {
                            break;
                        }
                    } else {
                        if (formDataNode instanceof List) {
                            if (((List) formDataNode).get(lastIndex) != null) {
                                formDataNode = ((List) formDataNode).get(lastIndex);
                            } else {
                                final List<Object> formDataNodeValue = new ArrayList<>();
                                ((List) formDataNode).add(index, formDataNodeValue);
                                formDataNode = formDataNodeValue;
                            }
                        } else {
                            break;
                        }
                    }

                    lastIndex = index;
                } else {
                    if (lastIndex == null) {
                        if (lastIsList) {
                            break;
                        }

                        if (formDataNode instanceof Map) {
                            if (i == pathFragments.length - 1) {
                                break;
                            }

                            final Map<String, Object> formDataNodeValue = new HashMap<>();

                            if (!((Map) formDataNode).containsKey(pathFragment)) {
                                ((Map) formDataNode).put(pathFragment, formDataNodeValue);
                            }

                            formDataNode = ((Map) formDataNode).get(pathFragment);
                        } else {
                            break;
                        }
                    } else {
                        if (formDataNode instanceof List) {
                            final Map<String, Object> formDataNodeValue = new HashMap<>();

                            if (((List) formDataNode).size() <= lastIndex
                                    || ((List) formDataNode).get(lastIndex) == null) {
                                ((List) formDataNode).add(lastIndex, formDataNodeValue);
                            }

                            formDataNode = ((List) formDataNode).get(lastIndex);

                            if (i != pathFragments.length - 1) {
                                lastKey = pathFragment;
                            }
                        } else {
                            break;
                        }
                    }

                    lastIndex = null;
                }

                lastIsList = isList;
            }
            // end: path parser

            if (formDataNode instanceof List) {
                if (lastIndex == null) {
                    ((List) formDataNode).add(value);
                } else {
                    ((List) formDataNode).add(lastIndex, value);
                }
            } else if (formDataNode instanceof Map) {
                ((Map) formDataNode).put(getLastPathFragment(name), value);
            }
        }

        return formData;
    }

    protected static String getLastPathFragment(final String path) {
        return path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
    }

    protected static boolean isList(final String pathFragment) {
        return pathFragment.matches(".*?\\[([0-9]*?)\\]$");
    }

    protected static Integer getIndex(final String pathFragment) {
        final Matcher matcher = Pattern.compile("\\[([0-9]+?)\\]").matcher(pathFragment);

        return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
    }
}
