package org.meveo.service.base;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.MapELResolver;

public class SimpleELResolver extends ELResolver  {


private ELResolver delegate = new MapELResolver();
private Map<Object, Object> userMap;

public SimpleELResolver(Map<Object, Object> userMap) {
  this.userMap = userMap;
}

@Override
public Object getValue(ELContext context, Object base, Object property) {
  if(base==null) {
    base = userMap;
  }
  return delegate.getValue(context, base, property);
}

@Override
public Class<?> getType(ELContext context, Object base, Object property) {
	if(base==null) {
		    base = userMap;
	}
	return delegate.getType(context, base, property);
}

@Override
public void setValue(ELContext context, Object base, Object property,
		Object value) {
	if(base==null) {
		    base = userMap;
	}
	delegate.setValue(context, base, property, value);
}

@Override
public boolean isReadOnly(ELContext context, Object base, Object property) {
	if(base==null) {
		    base = userMap;
	}
	return delegate.isReadOnly(context, base, property);
}

@Override
public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
		Object base) {
	if(base==null) {
	    base = userMap;
	}
	return delegate.getFeatureDescriptors(context, base);
}

@Override
public Class<?> getCommonPropertyType(ELContext context, Object base) {
	if(base==null) {
	    base = userMap;
	}
	return delegate.getCommonPropertyType(context, base);
}

public void setUserMap(Map<Object, Object> userMap2) {
    userMap=userMap2;
}

}