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

package org.meveo.model.persistence;

import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.usertype.DynamicParameterizedType;

/**
 * JSON type field mapping that adapts both for Oracle's Clob and Postgresql's JsonB type field based on a system parameter.<br/>
 * A value of -Dopencell.json.db.type=clob will implement Oracle's Clob and a missing or any other value will assume Postgresql JsonB implementation.
 * 
 * @author Andrius Karpavicius
 */
public class JsonType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    private static final long serialVersionUID = 2098848330584585140L;

    /**
     * Json field type in DB. To distinquish what Hibernate data type mapper should be used to interpret the value and convert from json to an object. <br/>
     * Currently supported value is "clob" for oracle implementation. Any other or missing value will use a default value of "jsonb" for postgresql.
     */
    private static String JSON_DB_TYPE = "opencell.json.db.type";

    public static boolean IS_CLOB = "clob".equalsIgnoreCase(System.getProperty(JSON_DB_TYPE));

    public JsonType() {
        super(IS_CLOB ? ClobTypeDescriptor.DEFAULT : JsonBinarySqlTypeDescriptor.INSTANCE, new JsonTypeDescriptor());
    }

    public String getName() {
        return "json";
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((JsonTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}