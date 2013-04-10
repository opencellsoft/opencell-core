package org.meveo.util.view.composite;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.IEntity;
import org.slf4j.LoggerFactory;

/**
 * @author Ignas
 * 
 */
public class BackingBeanBasedCompositeComponent extends UINamingContainer {

    private static final String BOOLEAN_TRUE_STRING = "true";

    private BaseBean<? extends IEntity> backingBean;

    private Object entity;

    private Class entityClass;

    ParamBean paramBean = ParamBean.getInstance("meveo-admin.properties");

    /**
     * Get backing bean attribute either from parent component (search panel, thats where it usually should be defined) or from searchField component attributes (same with
     * formPanel and formField).
     */
    @SuppressWarnings("unchecked")
    public BaseBean<? extends IEntity> getBackingBeanFromParentOrCurrent() {
        if (backingBean == null) {
            UIComponent parent = getCompositeComponentParent(this);
            if (parent != null) {
                backingBean = (BaseBean<? extends IEntity>) parent.getAttributes().get("backingBean");
            }
            if (backingBean == null) {
                backingBean = (BaseBean<? extends IEntity>) getAttributes().get("backingBean");
            }
            if (backingBean == null) {
                throw new IllegalStateException("No backing bean was set in parent or current composite component!");
            }
        }
        return backingBean;
    }

    /**
     * Helper method to get entity from backing bean.
     */
    public Object getEntityFromBackingBeanOrAttribute() {
        if (entity == null) {
            entity = (Object) getAttributes().get("entity");

            UIComponent parent = getCompositeComponentParent(this);
            if (entity == null && parent != null) {
                entity = (Object) parent.getAttributes().get("entity");
            }
            if (entity == null) {
                try {
                    entity = getBackingBeanFromParentOrCurrent().getEntity();
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).error("Failed to instantiate a entity");
                }
            }
        }
        return entity;
    }

    /**
     * Helper method to get entity instance to query field definitions.
     */
    public Class getEntityClass() {
        if (entityClass == null) {
            entityClass = getBackingBeanFromParentOrCurrent().getClazz();
        }
        return entityClass;
    }

    /**
     * Return date pattern to use for rendered date/calendar fields. If time attribute was set to true then this methods returns date/time pattern, otherwise only date without time
     * pattern.
     */
    public String getDatePattern() {
        if (getAttributes().get("time").equals(BOOLEAN_TRUE_STRING)) {
            return paramBean.getProperty("meveo.dateTimeFormat");
        } else {
            return paramBean.getProperty("meveo.dateFormat");
        }
    }

    public boolean isText(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            return field.getType() == String.class;
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isBoolean(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Boolean.class || (type.isPrimitive() && type.getName().equals("boolean"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isDate(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            return field.getType() == Date.class;
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isEnum(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            return field.getType().isEnum();
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isInteger(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Integer.class || (type.isPrimitive() && type.getName().equals("int"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isLong(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Long.class || (type.isPrimitive() && type.getName().equals("long"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isByte(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Byte.class || (type.isPrimitive() && type.getName().equals("byte"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isShort(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Short.class || (type.isPrimitive() && type.getName().equals("short"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isDouble(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Double.class || (type.isPrimitive() && type.getName().equals("double"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isFloat(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Float.class || (type.isPrimitive() && type.getName().equals("float"));
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isBigDecimal(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            return field.getType() == BigDecimal.class;
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isEntity(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            return IEntity.class.isAssignableFrom(field.getType());
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public boolean isList(String fieldName, Object entity) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(entity != null ? entity.getClass() : getEntityClass(), fieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == List.class || type == Set.class;
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    public Object[] getEnumConstants(String fieldName) throws SecurityException, NoSuchFieldException {
        Field field = getBeanField(getEntityClass(), fieldName);
        if (field != null && field.getType().isEnum()) {
            return field.getType().getEnumConstants();
        }
        throw new IllegalStateException("No field with name '" + fieldName + "' was found");
    }

    private Field getBeanField(Class<?> c, String fieldName) throws SecurityException, NoSuchFieldException {
        Field field = null;
        try {
            field = c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (field == null && c.getSuperclass() != null) {
                return getBeanField(c.getSuperclass(), fieldName);
            }
        }

        return field;
    }

    public BaseBean<? extends IEntity> getBackingBean() {
        return backingBean;
    }

    public void setBackingBean(BaseBean<? extends IEntity> backingBean) {
        this.backingBean = backingBean;
    }
}
