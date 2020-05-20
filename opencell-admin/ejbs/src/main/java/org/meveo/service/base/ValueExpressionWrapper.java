/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.base;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueExpressionWrapper {

    static ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

    private SimpleELResolver simpleELResolver;

    private ELContext context;

    private ValueExpression ve;

    static protected Logger log = LoggerFactory.getLogger(ValueExpressionWrapper.class);

    static HashMap<String, ValueExpressionWrapper> valueExpressionWrapperMap = new HashMap<String, ValueExpressionWrapper>();

    /**
     * EL expression variable - provider - 'prov'
     */
    public static final String VAR_PROVIDER = "prov";

    /**
     * EL expression variable - seller - 'seller'
     */
    public static final String VAR_SELLER = "seller";

    /**
     * EL expression variable - customer - 'c'
     */
    public static final String VAR_CUSTOMER_SHORT = "c";

    /**
     * EL expression variable - customer - 'cust'
     */
    public static final String VAR_CUSTOMER = "cust";

    /**
     * EL expression variable - customer account - 'ca'
     */
    public static final String VAR_CUSTOMER_ACCOUNT = "ca";

    /**
     * EL expression variable - billing account - 'ba'
     */
    public static final String VAR_BILLING_ACCOUNT = "ba";

    /**
     * EL expression variable - user account - 'ua'
     */
    public static final String VAR_USER_ACCOUNT = "ua";

    /**
     * EL expression variable - access - 'access'
     */
    public static final String VAR_ACCESS = "access";

    /**
     * EL expression variable - subscription - 'sub'
     */
    public static final String VAR_SUBSCRIPTION = "sub";

    /**
     * EL expression variable - tax category - 'taxCategory'
     */
    public static final String VAR_TAX_CATEGORY = "taxCategory";

    /**
     * EL expression variable - tax class - 'taxClass'
     */
    public static final String VAR_TAX_CLASS = "taxClass";

    /**
     * EL expression variable - invoice - 'iv'
     */
    public static final String VAR_INVOICE_SHORT = "iv";

    /**
     * EL expression variable - invoice - 'invoice'
     */
    public static final String VAR_INVOICE = "invoice";

    /**
     * EL expression variable - billing run - 'br'
     */
    public static final String VAR_BILLING_RUN = "br";

    /**
     * EL expression variable - service template - 'serviceTemplate'
     */
    public static final String VAR_SERVICE_TEMPLATE = "serviceTemplate";

    /**
     * EL expression variable - service instance - 'serviceInstance'
     */
    public static final String VAR_SERVICE_INSTANCE = "serviceInstance";

    /**
     * EL expression variable - product instance - 'productInstance'
     */
    public static final String VAR_PRODUCT_INSTANCE = "productInstance";

    /**
     * EL expression variable - offer - 'offer'
     */
    public static final String VAR_OFFER = "offer";

    /**
     * EL expression variable - price plan - 'pp'
     */
    public static final String VAR_PRICE_PLAN_SHORT = "pp";

    /**
     * EL expression variable - price plan - 'priceplan'
     */
    public static final String VAR_PRICE_PLAN = "priceplan";

    /**
     * EL expression variable - dpi - 'dpi'
     */
    public static final String VAR_DISCOUNT_PLAN_INSTANCE = "dpi";

    /**
     * EL expression variable - EDR - 'edr'
     */
    public static final String VAR_EDR = "edr";

    /**
     * EL expression variable - charge template - 'charge'
     */
    public static final String VAR_CHARGE_TEMPLATE_SHORT = "charge";

    /**
     * EL expression variable - charge template - 'chargeTemplate'
     */
    public static final String VAR_CHARGE_TEMPLATE = "chargeTemplate";

    /**
     * EL expression variable - charge instance - 'ci'
     */
    public static final String VAR_CHARGE_INSTANCE = "ci";

    /**
     * EL expression variable - wallet operation - 'op'
     */
    public static final String VAR_WALLET_OPERATION = "op";

    /**
     * EL expression variable - date - 'date'
     */
    public static final String VAR_DATE = "date";

    /**
     * EL expression variable - amount - 'amount'
     */
    public static final String VAR_AMOUNT = "amount";

    /**
     * Evaluate expression.
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values (optional)
     * @return A value that expression evaluated to
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBoolean(String expression, Map<Object, Object> contextMap) throws BusinessException {

        Object value = evaluateExpression(expression, contextMap, Boolean.class);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value ignoring exceptions
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values (optional)
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static boolean evaluateToBooleanIgnoreErrors(String expression, Map<Object, Object> contextMap) {
        try {
            return evaluateToBoolean(expression, contextMap);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}", expression, contextMap, e);
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value. Note: method needs to have a unique name as is evaluated from JSF pages.
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBooleanOneVariable(String expression, String variableName, Object variable) throws BusinessException {

        boolean result = evaluateToBooleanMultiVariable(expression, variableName, variable);
        return result;
    }

    /**
     * Evaluate expression to a boolean value ignoring exceptions
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static boolean evaluateToBooleanIgnoreErrors(String expression, String variableName, Object variable) {
        try {
            return evaluateToBooleanMultiVariable(expression, variableName, variable);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}/{}", expression, variableName, variable, e);
            return false;
        }
    }

    /**
     * Evaluate expression to a boolean value.
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception.
     */
    public static boolean evaluateToBooleanMultiVariable(String expression, Object... contextVarNameAndValue) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return true;
        }

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (contextVarNameAndValue != null) {
            for (int i = 0; i < contextVarNameAndValue.length; i = i + 2) {
                contextMap.put(contextVarNameAndValue[i], contextVarNameAndValue[i + 1]);
            }
        }
        Object value = evaluateExpression(expression, contextMap, Boolean.class);
        if (value instanceof Boolean) {
            return (boolean) value;
        } else {
            return false;
        }
    }

    /**
     * Evaluate expression to a String value ignoring exceptions. Converting to string if necessary.
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true. Failure to evaluate, return false;
     */
    public static String evaluateToStringIgnoreErrors(String expression, String variableName, Object variable) {
        try {
            return evaluateToStringMultiVariable(expression, variableName, variable);
        } catch (BusinessException e) {
            log.error("Failed to evaluate expression {} on variable {}/{}", expression, variableName, variable, e);
            return null;
        }
    }

    /**
     * Evaluate expression to a string value, converting to string if necessary.
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException business exception
     */
    public static String evaluateToStringMultiVariable(String expression, Object... contextVarNameAndValue) throws BusinessException {
        if (StringUtils.isBlank(expression)) {
            return null;
        }

        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        if (contextVarNameAndValue != null) {
            for (int i = 0; i < contextVarNameAndValue.length; i = i + 2) {
                contextMap.put(contextVarNameAndValue[i], contextVarNameAndValue[i + 1]);
            }
        }
        Object value = evaluateExpression(expression, contextMap, String.class);
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    /**
     * Evaluate expression.
     * 
     * @param <T>
     * 
     * @param expression Expression to evaluate
     * @param contextMap Context of values
     * @param resultClass An expected result class
     * @return A value that expression evaluated to
     * @throws BusinessException business exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluateExpression(String expression, Map<Object, Object> contextMap, Class<T> resultClass) throws BusinessException {

        Object result = null;
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = StringUtils.trim(expression);

        if (expression.indexOf("#{") < 0) {
            log.debug("the expression '{}' doesn't contain any EL", expression);
            if (resultClass.equals(String.class)) {
                return (T) expression;
            } else if (resultClass.equals(Double.class)) {
                return (T) Double.valueOf(expression);
            } else if (resultClass.equals(BigDecimal.class)) {
                return (T) new BigDecimal(expression);
            } else if (resultClass.equals(Boolean.class)) {
                if ("true".equalsIgnoreCase(expression)) {
                    return (T) Boolean.TRUE;
                } else {
                    return (T) Boolean.FALSE;
                }
            }
        }

        try {
            result = ValueExpressionWrapper.getValue(expression, contextMap, resultClass);
            log.trace("EL {} => {}", expression, result);

            return (T) result;

        } catch (Exception e) {
            log.warn("EL {} throw error with variables {}", expression, contextMap, e);
            throw new BusinessException("Error while evaluating expression " + expression + " : " + e.getMessage());
        }
    }

    private static Object getValue(String expression, Map<Object, Object> userMap, @SuppressWarnings("rawtypes") Class resultClass) {
        ValueExpressionWrapper result = null;
        if (valueExpressionWrapperMap.containsKey(expression)) {
            result = valueExpressionWrapperMap.get(expression);
        }
        if (result == null) {
            result = new ValueExpressionWrapper(expression, userMap, resultClass);
        }
        return result.getValue(userMap);
    }

    private ValueExpressionWrapper(String expression, Map<Object, Object> userMap, @SuppressWarnings("rawtypes") Class resultClass) {
        if (userMap != null && expression.contains("appProvider")) {
            Provider appProvider = ((ProviderService) EjbUtils.getServiceInterface("ProviderService")).getProvider();
            userMap.put("appProvider", appProvider);
        }
        simpleELResolver = new SimpleELResolver(userMap);
        final VariableMapper variableMapper = new SimpleVariableMapper();
        final MeveoFunctionMapper functionMapper = new MeveoFunctionMapper();
        final CompositeELResolver compositeELResolver = new CompositeELResolver();
        compositeELResolver.add(simpleELResolver);
        compositeELResolver.add(new ArrayELResolver());
        compositeELResolver.add(new ListELResolver());
        compositeELResolver.add(new BeanELResolver());
        compositeELResolver.add(new MapELResolver());
        context = new ELContext() {
            @Override
            public ELResolver getELResolver() {
                return compositeELResolver;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return functionMapper;
            }

            @Override
            public VariableMapper getVariableMapper() {
                return variableMapper;
            }
        };
        ve = expressionFactory.createValueExpression(context, expression, resultClass);
    }

    private Object getValue(Map<Object, Object> userMap) {
        simpleELResolver.setUserMap(userMap);
        return ve.getValue(context);
    }

    public static boolean collectionContains(String[] collection, String key) {
        return Arrays.asList(collection).contains(key);
    }
}