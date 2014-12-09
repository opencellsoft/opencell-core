package org.meveo.service.base;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

public class SimpleVariableMapper extends VariableMapper {

	  private Map<String, ValueExpression> expressions = new HashMap<String, ValueExpression>();
	  
	  @Override
	  public ValueExpression resolveVariable(String variable) {
	    return expressions.get(variable);
	  }

	  @Override
	  public ValueExpression setVariable(String variable, ValueExpression expression) {
	    return expressions.put(variable, expression);
	  }

}
