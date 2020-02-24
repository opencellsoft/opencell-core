package org.meveo.commons.persistence;



import org.hibernate.QueryException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

public class BooleanPostgreSQLJsonSearchFunction extends PostgreSQLJsonSearchFunction {

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
      return BooleanType.INSTANCE;
   }

}