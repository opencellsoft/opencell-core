package org.meveo.commons.persistence;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class EntityReferencePostgreSQLJsonSearchFunction implements SQLFunction {

	@Override
	public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {

		if (args.size() < 3) {
			throw new IllegalArgumentException("The function parseJson for Entity must be passed 3 argument");
		}
		String columnName = (String) args.get(0);
		String fieldName = (String) args.get(1);
		String type = (String) args.get(2);
		String fragment = " (((" + columnName + "::json->>'" + fieldName + "')::json->0->>'" + type + "')::json->>'code')";
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