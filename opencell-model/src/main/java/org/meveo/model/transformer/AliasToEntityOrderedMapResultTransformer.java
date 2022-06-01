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

package org.meveo.model.transformer;

import java.io.BufferedReader;
import java.sql.Clob;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

/**
 * Sorts the dataset column base on the sql query.
 *
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 30 Mar 2018
 **/
public class AliasToEntityOrderedMapResultTransformer extends AliasedTupleSubsetResultTransformer {

    private static final long serialVersionUID = -4573333062530289943L;

    public static final AliasToEntityOrderedMapResultTransformer INSTANCE = new AliasToEntityOrderedMapResultTransformer();

    /**
     * Disallow instantiation of AliasToEntityOrderedMapResultTransformer .
     */
    private AliasToEntityOrderedMapResultTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object transformTuple(Object[] tuple, String[] aliases) {
        /* please note here LinkedHashMap is used so hopefully u ll get ordered key */
        Map result = new LinkedHashMap(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                Object data = tuple[i];
                if (data instanceof Clob) {
                    result.put(alias.toLowerCase(), clobToString((Clob) data));
                } else
                    result.put(alias.toLowerCase(), data);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    /**
     * Serialization hook for ensuring singleton uniqueing.
     *
     * @return The singleton instance : {@link #INSTANCE}
     */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * transform clob to string
     *
     * @param clob a clob data
     * @return a string
     */
    public static Object clobToString(Clob clob) {
        try {
            BufferedReader stringReader = new BufferedReader(clob.getCharacterStream());
            String singleLine = null;
            StringBuilder strBuff = new StringBuilder();
            while ((singleLine = stringReader.readLine()) != null) {
                strBuff.append(singleLine);
            }
            return strBuff.toString();
        } catch (Exception e) {
            return clob;
        }
    }

}
