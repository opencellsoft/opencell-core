package org.meveo.commons.persistence.oracle;

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
public class EntityReferenceOracleJsonSearchFunction implements SQLFunction {

    @Override
    public String render(Type firstArgumentType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {

        if (args.size() < 2) {
            throw new IllegalArgumentException("The function parseJson for Entity CF field requires at least 2 arguments");
        }
        String entityColumnName = (String) args.get(0);
        String customFieldName = (String) args.get(1);
        String customFieldValueProperty = getValuePropertyName();
        if (args.size() > 2) {
            customFieldValueProperty = (String) args.get(2);
        }
        String fragment = " (" + entityColumnName + "." + customFieldName + "[0]." + customFieldValueProperty + ".code)";
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

    public String getValuePropertyName() {
        return "entity";
    }
}