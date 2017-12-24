/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.commons.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
}
