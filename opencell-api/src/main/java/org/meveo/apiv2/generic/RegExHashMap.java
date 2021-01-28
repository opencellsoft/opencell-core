package org.meveo.apiv2.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author Thang Nguyen
 *
 * @param <K> : Key of the map item.
 * @param <V> : Value of the map item.
 */

public class RegExHashMap<K,V> extends HashMap<K,V> {
    // list of regular expression patterns
    private ArrayList<Pattern> regExPatterns = new ArrayList<Pattern>();
    // list of regular expression values which match patterns
    private ArrayList<V> regExValues = new ArrayList<V>();

    /**
     * Compile regular expression and add it to the regexp list as key.
     */
    @Override
    public V put(K aKey, V aValue) {
        if ( aKey instanceof Pattern ) {
            regExPatterns.add( (Pattern) aKey );
            regExValues.add( aValue );
        }

        return aValue;
    }

    /**
     * If requested value matches with a regular expression,
     * returns it from regexp lists.
     */
    @Override
    public V get(Object aKey) {
        CharSequence cs = aKey.toString();

        for (int i = 0; i < regExPatterns.size(); i++) {
            if (regExPatterns.get(i).matcher(cs).matches()) {
                return regExValues.get(i);
            }
        }
        return null;
    }

    /**
     * Check if map contains a key
     */
    @Override
    public boolean containsKey(Object aKey) {
        CharSequence cs = aKey.toString();

        for (int i = 0; i < regExPatterns.size(); i++) {
            if (regExPatterns.get(i).matcher(cs).matches()) {
                return true;
            }
        }

        return false;
    }
}
