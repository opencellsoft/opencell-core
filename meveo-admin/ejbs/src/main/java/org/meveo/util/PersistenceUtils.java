package org.meveo.util;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.meveo.model.IEntity;

public class PersistenceUtils {

    @SuppressWarnings("unchecked")
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            return null;
            // throw new NullPointerException("Entity passed for initialization is null");
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public static Class<IEntity> getClassForHibernateObject(IEntity object) {
        if (object instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) object).getHibernateLazyInitializer();
            return lazyInitializer.getPersistentClass();
        } else {
            return (Class<IEntity>) object.getClass();
        }
    }
}
