package org.meveo.service.payments.impl;

import static java.util.Arrays.stream;

import org.meveo.admin.exception.BusinessException;

public enum PolicyConditionOperatorEnum {

    EQUALS("="),
    NOT_EQUALS("<>"),
    HIGHER_THAN(">"),
    HIGHER_THAN_OR_EQUAL(">="),
    LOWER_THAN("<"),
    LOWER_THAN_OR_EQUAL("<=");

    private String operator;

    PolicyConditionOperatorEnum(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public static PolicyConditionOperatorEnum fromValue(String value) {
        return stream(values())
                        .filter(operator -> operator.operator.equalsIgnoreCase(value))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("No operator " + value + " found"));
    }
}