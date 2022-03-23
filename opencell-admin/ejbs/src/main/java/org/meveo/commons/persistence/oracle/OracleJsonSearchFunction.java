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

package org.meveo.commons.persistence.oracle;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class OracleJsonSearchFunction implements SQLFunction {

    @Override
    public String render(Type firstArgumentType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {

        if (args.size() < 2) {
            throw new IllegalArgumentException("The function parseJson requires at least 2 arguments");
        }
        String entityColumnName = (String) args.get(0);
        String customFieldName = (String) args.get(1);
        String customFieldValueProperty = getValuePropertyName();
        if (args.size() > 2) {
            customFieldValueProperty = (String) args.get(2);
        }
//        // use JSON_VALUE Ex. SELECT JSON_VALUE('{"ANIMALS":"1","DOG":"D1","CAT":"C1"}', '$.CAT') AS value FROM dual => C1
//        // Ex. SELECT JSON_VALUE('{"ANIMALS":[{"DOG":"D1","CAT": "C1"}]}', '$.ANIMALS[0].DOG') AS value FROM dual; ==> D1
        String fragment = " JSON_VALUE ( "+ entityColumnName + ", '$." + customFieldName + "[0]." + customFieldValueProperty +"' )";

        if (args.size() > 3) {
            String castType = (String) args.get(3);
            fragment = "cast(" + fragment + " as " + castType + ")";

        } else if (getCastType() != null) {
            fragment = "cast(" + fragment + " as " + getCastType() + ")";
        }

        return fragment;

    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
        return StringType.INSTANCE;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    /**
     * Get data cast type
     * 
     * @return Cast type
     */
    public String getCastType() {
        return null;
    }

    /**
     * Get CustomFieldValue property name
     * 
     * @return CustomFieldValue property name
     */
    public String getValuePropertyName() {
        return "string";
    }
}