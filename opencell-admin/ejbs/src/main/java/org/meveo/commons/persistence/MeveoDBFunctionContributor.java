package org.meveo.commons.persistence;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.meveo.model.persistence.CustomFieldJsonDataType;

public class MeveoDBFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {

        // Oracle additional functions
        if (CustomFieldJsonDataType.IS_CLOB) {
            functionContributions.getFunctionRegistry().registerAlternateKey("string_agg", "LISTAGG");
            functionContributions.getFunctionRegistry().registerAlternateKey("string_agg_long", "LISTAGG_CLOB");
        
//          registerFunction( "concat", new StandardSQLFunction( "concat", StringType.INSTANCE));
//          registerFunction("string_agg", new StandardSQLFunction("LISTAGG", StringType.INSTANCE));
//          registerFunction("string_agg_long", new StandardSQLFunction("LISTAGG_CLOB", ClobType.INSTANCE));
//          registerFunction("numericFromJson", new DoubleOracleJsonSearchFunction());
//          registerFunction("varcharFromJson", new OracleJsonSearchFunction());
//          registerFunction("bigIntFromJson", new LongOracleJsonSearchFunction());
//          registerFunction("timestampFromJson", new DateOracleJsonSearchFunction());
//          registerFunction("booleanFromJson", new BooleanOracleJsonSearchFunction());
//          registerFunction("entityFromJson", new EntityReferenceOracleJsonSearchFunction());
            
        // Postgresql additional functions
        } else {
//          registerFunction("concat", new StandardSQLFunction("concat", StringType.INSTANCE));
//          registerFunction("string_agg", new StandardSQLFunction("string_agg", StringType.INSTANCE));
//          registerFunction("string_agg_long", new PostgreSQLStringAggLongFunction());
//          registerFunction("numericFromJson", new DoublePostgreSQLJsonSearchFunction());
//          registerFunction("varcharFromJson", new PostgreSQLJsonSearchFunction());
//          registerFunction("bigIntFromJson", new LongPostgreSQLJsonSearchFunction());
//          registerFunction("timestampFromJson", new DatePostgreSQLJsonSearchFunction());
//          registerFunction("booleanFromJson", new BooleanPostgreSQLJsonSearchFunction());
//          registerFunction("entityFromJson", new EntityReferencePostgreSQLJsonSearchFunction());
//          registerFunction("listFromJson", new ListPostgreSQLJsonSearchFunction());
        }
    }
}