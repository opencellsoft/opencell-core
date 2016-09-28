package org.meveo.service.base;

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
     * Evaluate expression to a boolean value
     * 
     * @param expression Expression to evaluate
     * @param variableName Variable name to give to a variable in context
     * @param variable Variable to make available in context
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException
     */
    public static boolean evaluateToBoolean(String expression, String variableName, Object variable) throws BusinessException {
       
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
     * Evaluate expression to a boolean value
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException
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
     * Evaluate expression to a String value ignoring exceptions. Converting to string if necessary
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
     * Evaluate expression to a string value, converting to string if necessary
     * 
     * @param expression Expression to evaluate
     * @param contextVarNameAndValue An array of context variables and their names in the following order: variable 1 name, variable 1, variable 2 name, variable2, etc..
     * @return A boolean value expression evaluates to. An empty expression evaluates to true;
     * @throws BusinessException
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
     * Evaluate expression
     * 
     * @param expression Expression to evaluate
     * @param userMap Context of values
     * @param resultClass An expected result class
     * @return A value that expression evaluated to
     * @throws BusinessException
     */
    public static Object evaluateExpression(String expression, Map<Object, Object> userMap, @SuppressWarnings("rawtypes") Class resultClass) throws BusinessException {
        Object result = null;
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = StringUtils.trim(expression);

        if (expression.indexOf("#{") < 0) {
            log.debug("the expression '{}' doesn't contain any EL", expression);
            if (resultClass.equals(String.class)) {
                return expression;
            } else if (resultClass.equals(Double.class)) {
                return Double.parseDouble(expression);
            } else if (resultClass.equals(Boolean.class)) {
                if ("true".equalsIgnoreCase(expression)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        }
        try {
            result = ValueExpressionWrapper.getValue(expression, userMap, resultClass);
            log.trace("EL {} => {}", expression, result);

        } catch (Exception e) {
            log.warn("EL {} throw error with variables {}", expression, userMap, e);
            throw new BusinessException("Error while evaluating expression " + expression + " : " + e.getMessage());
        }
        return result;
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
}