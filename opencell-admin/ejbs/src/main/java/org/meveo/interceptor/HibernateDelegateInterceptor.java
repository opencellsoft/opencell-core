package org.meveo.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Tis interceptor allows to to intercept Hibernate persistent objects and inspect and/or manipulate their properties before it is saved, updated, deleted or loaded.
 *
 * @author Abdellatif BARI
 * @since 5.3
 */
public class HibernateDelegateInterceptor extends EmptyInterceptor {

    private static Interceptor interceptor;

    public static void setInterceptor(Interceptor interceptor) {
        HibernateDelegateInterceptor.interceptor = interceptor;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return HibernateDelegateInterceptor.interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        HibernateDelegateInterceptor.interceptor.beforeTransactionCompletion(tx);
    }

}