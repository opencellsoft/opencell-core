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

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.MapELResolver;

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
    this.userMap  = userMap2;
}
}
