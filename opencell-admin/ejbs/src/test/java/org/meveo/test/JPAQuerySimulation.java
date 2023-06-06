package org.meveo.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public class JPAQuerySimulation<E> implements TypedQuery<E> {

    @Override
    public int executeUpdate() {
        return 0;
    }

    @Override
    public int getMaxResults() {
        return 0;
    }

    @Override
    public int getFirstResult() {
        return 0;
    }

    @Override
    public Map<String, Object> getHints() {
        return null;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return null;
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return null;
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return false;
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return null;
    }

    @Override
    public Object getParameterValue(String name) {
        return null;
    }

    @Override
    public Object getParameterValue(int position) {
        return null;
    }

    @Override
    public FlushModeType getFlushMode() {
        return null;
    }

    @Override
    public LockModeType getLockMode() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public List<E> getResultList() {
        return null;
    }

    @Override
    public E getSingleResult() {
        return null;
    }

    @Override
    public TypedQuery<E> setMaxResults(int maxResult) {
        return this;
    }

    @Override
    public TypedQuery<E> setFirstResult(int startPosition) {
        return this;
    }

    @Override
    public TypedQuery<E> setHint(String hintName, Object value) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(Parameter param, Object value) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(Parameter param, Calendar value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(Parameter param, Date value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(String name, Object value) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(String name, Calendar value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(String name, Date value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(int position, Object value) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(int position, Calendar value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setParameter(int position, Date value, TemporalType temporalType) {
        return this;
    }

    @Override
    public TypedQuery<E> setFlushMode(FlushModeType flushMode) {
        return this;
    }

    @Override
    public TypedQuery<E> setLockMode(LockModeType lockMode) {
        return this;
    }
}