package org.meveo.commons.persistence;

import org.hibernate.dialect.PostgreSQL94Dialect;

/**
 * @author M.ELAZZOUZI
 *
 */
public class MeveoSQLDialect extends PostgreSQL94Dialect {
	public MeveoSQLDialect() {
		registerFunction("numericFromJson", new DoublePostgreSQLJsonSearchFunction());
		registerFunction("varcharFromJson", new PostgreSQLJsonSearchFunction());
		registerFunction("bigIntFromJson", new LongPostgreSQLJsonSearchFunction());
		registerFunction("timestampFromJson", new DatePostgreSQLJsonSearchFunction());
		registerFunction("booleanFromJson", new BooleanPostgreSQLJsonSearchFunction());
	}
}
