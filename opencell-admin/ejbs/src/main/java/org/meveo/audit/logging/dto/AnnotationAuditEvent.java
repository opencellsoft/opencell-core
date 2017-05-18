package org.meveo.audit.logging.dto;

import java.lang.reflect.Method;

/**
 * @author Edward P. Legaspi
 **/
public class AnnotationAuditEvent extends AuditEvent {

	private Class<?> clazz;
	Method method;
	Object[] paramValues;

	public AnnotationAuditEvent() {

	}

	public AnnotationAuditEvent(Class<?> clazz, Method method, Object[] paramValues) {
		super();
		this.clazz = clazz;
		this.method = method;
		this.paramValues = paramValues;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getParamValues() {
		return paramValues;
	}

	public void setParamValues(Object[] paramValues) {
		this.paramValues = paramValues;
	}

}
