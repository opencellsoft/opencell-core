package org.meveo.commons.utils;



import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

public class DatePostgreSQLJsonSearchFunction extends PostgreSQLJsonSearchFunction {

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
      return DateType.INSTANCE;
   }

}