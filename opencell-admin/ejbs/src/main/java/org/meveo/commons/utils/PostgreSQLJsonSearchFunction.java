package org.meveo.commons.utils;



import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class PostgreSQLJsonSearchFunction implements SQLFunction {

	@Override
	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {

      if (args.size() != 4) {
         throw new IllegalArgumentException( "The function parseJson must be passed 4 arguments");
      }
      String columnName = (String) args.get(0);
      String fieldName = (String) args.get(1);
      String type = (String) args.get(2);
      String castType = (String) args.get(3);
      
      
      String fragment = "("+columnName+"::json ->'" + fieldName + "'->0->>'"+type+"')::"+castType;
      return fragment;

   }
	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
      return StringType.INSTANCE;
   }

   @Override
   public boolean hasArguments() {
      return true;
   }

   @Override
   public boolean hasParenthesesIfNoArguments() {
      return false;
   }



}