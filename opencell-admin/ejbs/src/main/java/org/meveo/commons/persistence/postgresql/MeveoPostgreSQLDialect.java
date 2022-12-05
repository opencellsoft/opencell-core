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

package org.meveo.commons.persistence.postgresql;

import java.sql.Types;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * JPA extensions for searching amount Custom field value fields
 * <p>
 * Use:
 * <ul>
 * <li>varcharFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in String/Picklist/Text area type custom field</li>
 * <li>numericFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in Double type custom field</li>
 * <li>bigIntFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in Long type custom field</li>
 * <li>timestampFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in Date type custom field</li>
 * <li>booleanFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in Boolean type custom field</li>
 * <li>entityFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;) - for search in Entity type custom field. Returns EntityReferenceWrapper.code field value.</li>
 * <li>listFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;,&lt;value to search for&gt;) - for search in String type custom field of List storage type</li>
 * <li>listFromJson(&lt;entity&gt;.cfValues,&lt;custom field name&gt;,&lt;property name&gt;,&lt;position&gt;,&lt;value to search for&gt;) - for search in String type custom field of List storage type</li>
 * </ul>
 * 
 * @author M.ELAZZOUZI
 *
 */
public class MeveoPostgreSQLDialect extends PostgreSQLDialect {
    public MeveoPostgreSQLDialect() {
        super(DatabaseVersion.make(15, 1));
//        registerColumnType(Types.BOOLEAN, "int4");
        

//        registerFunction("concat", new StandardSQLFunction("concat", StringType.INSTANCE));
//        registerFunction("string_agg", new StandardSQLFunction("string_agg", StringType.INSTANCE));
//        registerFunction("string_agg_long", new PostgreSQLStringAggLongFunction());
//        registerFunction("numericFromJson", new DoublePostgreSQLJsonSearchFunction());
//        registerFunction("varcharFromJson", new PostgreSQLJsonSearchFunction());
//        registerFunction("bigIntFromJson", new LongPostgreSQLJsonSearchFunction());
//        registerFunction("timestampFromJson", new DatePostgreSQLJsonSearchFunction());
//        registerFunction("booleanFromJson", new BooleanPostgreSQLJsonSearchFunction());
//        registerFunction("entityFromJson", new EntityReferencePostgreSQLJsonSearchFunction());
//        registerFunction("listFromJson", new ListPostgreSQLJsonSearchFunction());
    }
}
