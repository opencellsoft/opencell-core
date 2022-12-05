package org.meveo.commons.persistence.postgresql;

import org.hibernate.dialect.function.SqlFunction;

/**
 * A search function for a Custom field of type Entity reference
 */
public class EntityReferencePostgreSQLJsonSearchFunction extends SqlFunction {

//    @Override
//    public String render(Type firstArgumentType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {
//
//        if (args.size() < 2) {
//            throw new IllegalArgumentException("The function parseJson for Entity ty CF field requires at least 2 arguments");
//        }
//        String entityColumnName = (String) args.get(0);
//        String customFieldName = (String) args.get(1);
//        String customFieldValueProperty = getValuePropertyName();
//        if (args.size() > 2) {
//            customFieldValueProperty = (String) args.get(2);
//        }
//        String fragment = " (((" + entityColumnName + "::json->>'" + customFieldName + "')::json->0->>'" + customFieldValueProperty + "')::json->>'code')";
//        return fragment;
//    }
//
//    @Override
//    public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
//        return StringType.INSTANCE;
//    }
//
//    @Override
//    public boolean hasArguments() {
//        return true;
//    }
//
//    @Override
//    public boolean hasParenthesesIfNoArguments() {
//        return false;
//    }
//
//    public String getValuePropertyName() {
//        return "entity";
//    }
}