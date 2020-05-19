package org.meveo.commons.persistence;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * @author M.ELAZZOUZI
 *
 */
public class MeveoSQLDialect extends PostgreSQL94Dialect {
	public MeveoSQLDialect() {
		super();
		registerFunction("string_agg", new StandardSQLFunction("string_agg", new org.hibernate.type.StringType()));
		registerFunction("numericFromJson", new DoublePostgreSQLJsonSearchFunction());
		registerFunction("varcharFromJson", new PostgreSQLJsonSearchFunction());
		registerFunction("bigIntFromJson", new LongPostgreSQLJsonSearchFunction());
		registerFunction("timestampFromJson", new DatePostgreSQLJsonSearchFunction());
		registerFunction("booleanFromJson", new BooleanPostgreSQLJsonSearchFunction());
		registerFunction("entityFromJson", new EntityReferencePostgreSQLJsonSearchFunction());
	}
}
