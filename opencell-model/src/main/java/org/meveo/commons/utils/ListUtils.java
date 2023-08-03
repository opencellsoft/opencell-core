/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.commons.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Utilities class for working with Lists.
 * 
 * @author Ignas Lelys
 */
public final class ListUtils {

    /**
     * No need to create it.
     */
    private ListUtils() {

    }

    /**
     * Checks if collection is empty. If collection is null it is also considered empty.
     * 
     * @param collection Collection to check.
     * @return True if collection is empty.
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmtyCollection(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * Sort map by it's values.
     * 
     * @param <K> key
     * @param <V> value
     * @param map Map to sort
     * @return A sorted map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (o1.getValue() instanceof String) {
                    return ((String) o1.getValue()).compareToIgnoreCase((String) o2.getValue());
                } else {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Get a an empty collection if null.
     *
     * @param <T> the generic type
     * @param collection the collection
     * @return the collection
     */
    public static <T> Collection<T> safe(Collection<T> collection) {
        return collection == null ? Collections.EMPTY_LIST : collection;
    }

    /**
     * Recursively inspect a map to match key. Go deeper if map value is of a map type.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @return A matched value
     */
    public static Object matchMapKeyRecursively(Map<String, ?> mapToInspect, String keyToMatch, boolean fullMatch) {

        if (fullMatch && mapToInspect.containsKey(keyToMatch)) {
            return mapToInspect.get(keyToMatch);

        } else {
            for (Entry<String, ?> entry : mapToInspect.entrySet()) {
                if (!fullMatch && entry.getKey().startsWith(keyToMatch)) {
                    return entry.getValue();
                } else if (entry.getValue() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Object matched = matchMapKeyRecursively((Map<String, ?>) entry.getValue(), keyToMatch, fullMatch);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recursively inspect a map to match key. Go deeper if map value is of a map type.
     * 
     * @param mapToInspect Map to inspect
     * @param keyToMatch A key to match
     * @param fullMatch Shall a full or partial (starts with) key match should be performed. True for a full match.
     * @return A removed value or null if nothing was removed
     */
    public static Object removeMapKeyRecursively(Map<String, Object> mapToInspect, String keyToMatch, boolean fullMatch) {

        if (fullMatch && mapToInspect.containsKey(keyToMatch)) {
            return mapToInspect.remove(keyToMatch);

        } else {
            for (Entry<String, Object> entry : mapToInspect.entrySet()) {
                if (!fullMatch && entry.getKey().startsWith(keyToMatch)) {
                    return mapToInspect.remove(entry.getKey());
                } else if (entry.getValue() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Object matched = removeMapKeyRecursively((Map<String, Object>) entry.getValue(), keyToMatch, fullMatch);
                    if (matched != null) {
                        return matched;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recursively inspect a map to remove keys with no values
     * 
     * @param mapToInspect Map to inspect
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void removeEmptyMapValuesRecursively(Map<String, Object> mapToInspect) {

        Set<String> keys = new HashSet<String>(mapToInspect.keySet());

        for (String key : keys) {
            Object value = mapToInspect.get(key);
            if (value instanceof Map) {
                if (!((Map<String, Object>) value).isEmpty()) {
                    removeEmptyMapValuesRecursively((Map<String, Object>) value);
                }
                if (((Map) value).isEmpty()) {
                    mapToInspect.remove(key);
                }
            }
        }
    }
}