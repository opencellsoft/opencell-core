package org.meveo.api.rest.impl;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Iterator;

public class ConstraintViolationMock implements ConstraintViolation {

    private Path propertyPath;
    private Object invalidValue;
    private String message;

    public ConstraintViolationMock(String propertyPath, Object invalidValue, String message) {
        this.propertyPath = new PathMock(propertyPath);
        this.invalidValue = invalidValue;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    @Override
    public Object getRootBean() {
        return null;
    }

    @Override
    public Class getRootBeanClass() {
        return ConstraintViolationMock.class;
    }

    @Override
    public Object getLeafBean() {
        return null;
    }

    @Override
    public Object[] getExecutableParameters() {
        return new Object[0];
    }

    @Override
    public Object getExecutableReturnValue() {
        return null;
    }

    @Override
    public Path getPropertyPath() {
        return propertyPath;
    }

    @Override
    public Object getInvalidValue() {
        return invalidValue;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
    }

    @Override
    public Object unwrap(Class aClass) {
        return null;
    }

    private class PathMock implements Path {


        private String toString;

        public PathMock(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }

        @Override
        public Iterator<Node> iterator() {
            return null;
        }
    }
}
