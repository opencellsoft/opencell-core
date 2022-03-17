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

package org.meveo.commons.persistence;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A search function for a Custom field of type String with List storage type
 */
public class ListPostgreSQLJsonSearchFunction extends PostgreSQLJsonSearchFunction {

    @Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {

        if (args.size() < 2) {
            throw new IllegalArgumentException("The function parseJson requires at least 2 arguments");
        }
        String customFieldPosition = "0";
        String entityColumnName = (String) args.get(0);
        String customFieldName = (String) args.get(1);
        String customFieldValueProperty = getValuePropertyName();
        String value = null;
        if (args.size() > 4) {
            customFieldValueProperty = (String) args.get(2);
            customFieldPosition = (String) args.get(3);
            value = (String) args.get(4);

        } else if (args.size() > 2) {
            value = (String) args.get(2);
        }

        String fragment = entityColumnName + "::jsonb ->'" + customFieldName + "'->" + customFieldPosition + "->'" + customFieldValueProperty + "'";

        fragment = "(" + fragment + " ^| array[" + value + "])";

        return fragment;
    }

    @Override
    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
        return BooleanType.INSTANCE;
    }

    @Override
    public String getCastType() {
        return null;
    }

    @Override
    public String getValuePropertyName() {
        return "listString";
    }
}