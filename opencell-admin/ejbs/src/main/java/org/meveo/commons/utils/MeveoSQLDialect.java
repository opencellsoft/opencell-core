package org.meveo.commons.utils;

import org.hibernate.dialect.PostgreSQL94Dialect;

/**
 * @author Mohammed
 *
 */
public class MeveoSQLDialect extends PostgreSQL94Dialect {
	public MeveoSQLDialect() {
		registerFunction("numericFromJson", new DoublePostgreSQLJsonSearchFunction());
		registerFunction("varcharFromJson", new PostgreSQLJsonSearchFunction());
		registerFunction("bigIntFromJson", new LongPostgreSQLJsonSearchFunction());
		registerFunction("datetimeFromJson", new DatePostgreSQLJsonSearchFunction());
	}
}
