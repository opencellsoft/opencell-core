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

package org.meveo.audit.logging.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;

/**
 * @author Edward P. Legaspi
 **/
public class AnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> wrapped;
	private final Set<Annotation> annotations;

	public AnnotatedTypeWrapper(AnnotatedType<T> wrapped, Set<Annotation> annotations) {
		this.wrapped = wrapped;
		this.annotations = new HashSet<>(annotations);
	}

	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return wrapped.getAnnotation(annotationType);
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public Type getBaseType() {
		return wrapped.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return wrapped.getTypeClosure();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		for (Annotation annotation : annotations) {
			if (annotationType.isInstance(annotation)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<AnnotatedConstructor<T>> getConstructors() {
		return wrapped.getConstructors();
	}

	@Override
	public Set<AnnotatedField<? super T>> getFields() {
		return wrapped.getFields();
	}

	@Override
	public Class<T> getJavaClass() {
		return wrapped.getJavaClass();
	}

	@Override
	public Set<AnnotatedMethod<? super T>> getMethods() {
		return wrapped.getMethods();
	}

}
