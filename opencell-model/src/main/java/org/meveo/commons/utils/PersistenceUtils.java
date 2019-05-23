package org.meveo.commons.utils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.meveo.model.IEntity;

import java.lang.reflect.Modifier;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

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

    public static <T> void initializeAllProperties(T entity) {
        ReflectionUtils.getAllFields(entity.getClass()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> {
            field.setAccessible(true);
            try {
                Hibernate.initialize(field.get(entity));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
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
