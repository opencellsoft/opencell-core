package org.meveo.commons.persistence;



import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.type.DoubleType;
import org.hibernate.type.Type;

public class DoublePostgreSQLJsonSearchFunction extends PostgreSQLJsonSearchFunction {

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
      return DoubleType.INSTANCE;
   }

}