package org.meveo.commons.persistence;



import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

public class LongPostgreSQLJsonSearchFunction extends PostgreSQLJsonSearchFunction {

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
      return LongType.INSTANCE;
   }

}