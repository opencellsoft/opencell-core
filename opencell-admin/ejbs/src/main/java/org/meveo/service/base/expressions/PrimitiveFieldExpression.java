package org.meveo.service.base.expressions;

import org.meveo.commons.utils.QueryBuilder;

public class PrimitiveFieldExpression implements Expression {
    private final String tableNameAlias;
    private final String fieldName;
    private final Object value;
    private final boolean isNegate;
    private final boolean isOptional;

    public PrimitiveFieldExpression(String tableNameAlias, String fieldName, Object value, boolean isNegate, boolean isOptional) {
        this.tableNameAlias = tableNameAlias;
        this.fieldName = fieldName;
        this.value = value;
        this.isNegate = isNegate;
        this.isOptional = isOptional;
    }

    @Override
    public void addFilters(QueryBuilder queryBuilder) {
        queryBuilder.accept(this);
    }

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getValue() {
        return value;
    }

    public boolean isNegate() {
        return isNegate;
    }

    public boolean isOptional() {
        return isOptional;
    }
}
