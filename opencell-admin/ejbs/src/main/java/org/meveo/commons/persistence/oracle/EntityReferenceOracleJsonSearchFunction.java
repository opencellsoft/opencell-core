package org.meveo.commons.persistence.oracle;

import org.hibernate.dialect.function.SqlFunction;

/**
 * A search function for a Custom field of type Entity reference
 */
public class EntityReferenceOracleJsonSearchFunction extends SqlFunction {

//    @Override
//    public String render(Type firstArgumentType, @SuppressWarnings("rawtypes") List args, SessionFactoryImplementor factory) throws QueryException {
//
//        if (args.size() < 2) {
//            throw new IllegalArgumentException("The function parseJson for Entity CF field requires at least 2 arguments");
//        }
//        String entityColumnName = (String) args.get(0);
//        String customFieldName = (String) args.get(1);
//        String customFieldValueProperty = getValuePropertyName();
//        if (args.size() > 2) {
//            customFieldValueProperty = (String) args.get(2);
//        }
//        // use JSON_VALUE Ex. SELECT JSON_VALUE('{"ANIMALS":"1","DOG":"D1","CAT":"C1"}', '$.CAT') AS value FROM dual
//        return " JSON_VALUE("+ entityColumnName + ", '$." + customFieldName + "[0]." + customFieldValueProperty + ".code')";
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