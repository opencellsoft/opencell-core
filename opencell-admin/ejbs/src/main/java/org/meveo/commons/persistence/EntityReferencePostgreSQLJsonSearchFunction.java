package org.meveo.commons.persistence;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

/**
 * A search function for a Custom field of type Entity reference
 */
public class EntityReferencePostgreSQLJsonSearchFunction implements SQLFunction {

    @Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory) throws QueryException {

        if (args.size() != 3) {
            throw new IllegalArgumentException("The function parseJson for Entity requires 3 arguments");
        }
        String entityColumnName = (String) args.get(0);
        String customFieldName = (String) args.get(1);
        String customFieldValueProperty = (String) args.get(2);
        String fragment = " (((" + entityColumnName + "::json->>'" + customFieldName + "')::json->0->>'" + customFieldValueProperty + "')::json->>'code')";
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