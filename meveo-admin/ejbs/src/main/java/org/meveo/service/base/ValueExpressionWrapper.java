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

import org.meveo.admin.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueExpressionWrapper {

    static ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
    
    SimpleELResolver simpleELResolver;
    ELContext context;
    ValueExpression ve;

    static protected Logger log = LoggerFactory.getLogger(ValueExpressionWrapper.class);
    
    static HashMap<String,ValueExpressionWrapper> valueExpressionWrapperMap = new HashMap<String,ValueExpressionWrapper>();
    
    
    public static Object evaluateExpression(String expression, Map<Object, Object> userMap,
            @SuppressWarnings("rawtypes") Class resultClass) throws BusinessException {
        Object result = null;
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        expression = StringUtils.trim(expression);
        
        if (expression.indexOf("#{") < 0) {
            log.debug("the expression '{}' doesnt contain any EL", expression);
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
            log.warn("EL {} throw error", expression, e);
            throw new BusinessException("Error while evaluating expression " + expression + " : " + e.getMessage());
        }
        return result;
    }
    
    private static Object getValue(String expression,Map<Object, Object> userMap,@SuppressWarnings("rawtypes") Class resultClass){
        ValueExpressionWrapper result=null;
        if(valueExpressionWrapperMap.containsKey(expression)){
            result= valueExpressionWrapperMap.get(expression);
        }
        if(result==null){
            result=new ValueExpressionWrapper(expression, userMap, resultClass);
        }
        return result.getValue(userMap);
    }
    
    private ValueExpressionWrapper(String expression,Map<Object, Object> userMap,@SuppressWarnings("rawtypes") Class resultClass){
        simpleELResolver = new SimpleELResolver(userMap);
        final VariableMapper variableMapper = new SimpleVariableMapper();
        final FunctionMapper functionMapper = new SimpleFunctionMapper();
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
