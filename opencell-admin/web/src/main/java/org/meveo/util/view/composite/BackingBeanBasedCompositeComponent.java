package org.meveo.util.view.composite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
public class BackingBeanBasedCompositeComponent extends UINamingContainer {

    private static final String BOOLEAN_TRUE_STRING = "true";

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Get backing bean attribute either from parent component (search panel, thats where it usually should be defined) or from searchField component attributes (same with
     * formPanel and formField).
     */
    @SuppressWarnings("unchecked")
    public BaseBean<? extends IEntity> getBackingBeanFromParentOrCurrent() {

        BaseBean<? extends IEntity> backingBean = (BaseBean<? extends IEntity>) getStateHelper().get("backingBean");
        if (backingBean == null) {
            backingBean = (BaseBean<? extends IEntity>) getAttributes().get("backingBean");

            if (backingBean == null) {
                UIComponent parent = getCompositeComponentParent(this);
                if (parent != null && parent instanceof BackingBeanBasedCompositeComponent) {
                    backingBean = ((BackingBeanBasedCompositeComponent) parent).getBackingBeanFromParentOrCurrent();
                }
            }
            if (backingBean == null) {
                throw new IllegalStateException("No backing bean was set in parent or current composite component!");
            } else {
                getStateHelper().put("backingBean", backingBean);
            }
        }
        return backingBean;
    }

    /**
     * Helper method to get entity from backing bean.
     * 
     * @return entity.
     */
    public Object getEntityFromBackingBeanOrAttribute() {
        Object entity = getStateHelper().get("entity");

        if (entity == null) {
            entity = (Object) getAttributes().get("entity");

            if (entity == null) {
                UIComponent parent = getCompositeComponentParent(this);
                if (parent != null && parent instanceof BackingBeanBasedCompositeComponent) {
                    entity = ((BackingBeanBasedCompositeComponent) parent).getEntityFromBackingBeanOrAttribute();
                }
            }

            if (entity == null && this instanceof FormPanelCompositeComponent) {
                try {
                    entity = getBackingBeanFromParentOrCurrent().getEntity();
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).error("Failed to instantiate a entity", e);
                }
            }
        }

        return entity;
    }

    /**
     * Helper method to get entity instance to query field definitions.
     */
    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        if (entityClass == null) {
            entityClass = getBackingBeanFromParentOrCurrent().getClazz();
        }
        return entityClass;
    }

    /**
     * Helper method to get entity instance to query field definitions.
     */
    @SuppressWarnings("rawtypes")
    public Class getEntityClassFromEntity() {
        if (entityClass == null) {
            entityClass = getEntityFromBackingBeanOrAttribute().getClass();
        }
        return entityClass;
    }

    /**
     * Return date pattern to use for rendered date/calendar fields. If time attribute was set to true then this methods returns date/time pattern, otherwise only date without time
     * pattern.
     */
    public String getDatePattern() {
        ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
        if (BOOLEAN_TRUE_STRING.equals(getAttributes().get("time"))) {
            return paramBeanFactory.getInstance().getDateTimeFormat();
        } else {
            return paramBeanFactory.getInstance().getDateFormat();
        }
    }

    public boolean isText(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        return field.getType() == String.class;
    }

    public boolean isText(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isText(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Field field = getBeanField(entityField.getType(), childFieldName);

        if (field != null) {
            return field.getType() == String.class;
        } else {
            return false;
        }
    }

    public boolean isBoolean(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Class<?> type = field.getType();
        return type == Boolean.class || (type.isPrimitive() && type.getName().equals("boolean"));
    }

    public boolean isBoolean(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isBoolean(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Boolean.class || (type.isPrimitive() && type.getName().equals("boolean"));
        } else {
            return false;
        }
    }

    public boolean isDate(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        return field.getType() == Date.class;
    }

    public boolean isDate(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isDate(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            return field.getType() == Date.class;
        } else {
            return false;
        }
    }

    public boolean isEnum(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        return field.getType().isEnum();
    }

    public boolean isEnum(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isEnum(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Field field = getBeanField(entityField.getType(), childFieldName);

        if (field != null) {
            return field.getType().isEnum();
        } else {
            return false;
        }
    }

    public boolean isNumber(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();

        return type == Integer.class || type == Long.class || type == Byte.class || type == Short.class || type == Double.class || type == Float.class
                || field.getType() == BigDecimal.class || (type.isPrimitive() && (type.getName().equals("int") || type.getName().equals("long") || type.getName().equals("byte")
                        || type.getName().equals("short") || type.getName().equals("double") || type.getName().equals("float")));

    }

    public boolean isNumber(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isNumber(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Integer.class || type == Long.class || type == Byte.class || type == Short.class || type == Double.class || type == Float.class
                    || field.getType() == BigDecimal.class || (type.isPrimitive() && (type.getName().equals("int") || type.getName().equals("long") || type.getName().equals("byte")
                            || type.getName().equals("short") || type.getName().equals("double") || type.getName().equals("float")));
        } else {
            return false;
        }
    }

    public boolean isInteger(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Integer.class || (type.isPrimitive() && type.getName().equals("int"));
    }

    public boolean isInteger(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isInteger(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Integer.class || (type.isPrimitive() && type.getName().equals("int"));
        } else {
            return false;
        }
    }

    public boolean isLong(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Long.class || (type.isPrimitive() && type.getName().equals("long"));
    }

    public boolean isLong(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isLong(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Long.class || (type.isPrimitive() && type.getName().equals("long"));
        } else {
            return false;
        }
    }

    public boolean isByte(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Byte.class || (type.isPrimitive() && type.getName().equals("byte"));
    }

    public boolean isByte(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isByte(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Byte.class || (type.isPrimitive() && type.getName().equals("byte"));
        } else {
            return false;
        }
    }

    public boolean isShort(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Short.class || (type.isPrimitive() && type.getName().equals("short"));
    }

    public boolean isShort(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isShort(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Short.class || (type.isPrimitive() && type.getName().equals("short"));
        } else {
            return false;
        }
    }

    public boolean isDouble(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Double.class || (type.isPrimitive() && type.getName().equals("double"));
    }

    public boolean isDouble(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isDouble(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Double.class || (type.isPrimitive() && type.getName().equals("double"));
        } else {
            return false;
        }
    }

    public boolean isFloat(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Float.class || (type.isPrimitive() && type.getName().equals("float"));
    }

    public boolean isFloat(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isFloat(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == Float.class || (type.isPrimitive() && type.getName().equals("float"));
        } else {
            return false;
        }
    }

    public boolean isBigDecimal(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        return field.getType() == BigDecimal.class;
    }

    public boolean isBigDecimal(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isBigDecimal(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);

        Field field = getBeanField(entityField.getType(), childFieldName);
        if (field != null) {
            Class<?> type = field.getType();
            return type == BigDecimal.class;
        } else {
            return false;
        }
    }

    public boolean isEntity(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        return IEntity.class.isAssignableFrom(field.getType());
    }

    public boolean isEntity(String fieldName, String childFieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (StringUtils.isEmpty(childFieldName)) {
            return isEntity(fieldName, determineFromEntityClass);
        }

        Field entityField = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Field field = getBeanField(entityField.getType(), childFieldName);

        if (field != null) {
            return IEntity.class.isAssignableFrom(field.getType());
        } else {
            return false;
        }
    }

    public boolean isList(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == List.class || type == Set.class;
    }

    public boolean isMap(String fieldName, boolean determineFromEntityClass)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Field field = getBeanFieldThrowException(determineFromEntityClass ? getEntityClass() : getEntityFromBackingBeanOrAttribute().getClass(), fieldName);
        Class<?> type = field.getType();
        return type == Map.class || type == HashMap.class;
    }

    public Object[] getEnumConstants(String fieldName) throws SecurityException, NoSuchFieldException {

        Field field = getBeanFieldThrowException(getEntityClassFromEntity(), fieldName);
        Object[] objArr = field.getType().getEnumConstants();
        Arrays.sort(objArr, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return objArr;
    }

    @SuppressWarnings("rawtypes")
    private Field getBeanField(Class<?> c, String fieldName) {

        Field field = null;

        if (fieldName.contains(".")) {
            Class iterationClazz = c;
            StringTokenizer tokenizer = new StringTokenizer(fieldName, ".");
            while (tokenizer.hasMoreElements()) {
                String iterationFieldName = tokenizer.nextToken();
                field = getBeanField(iterationClazz, iterationFieldName);
                if (field != null) {
                    iterationClazz = field.getType();
                } else {
                    log.error("No field {} in {}", iterationFieldName, iterationClazz);
                    return null;
                }
            }

        } else {

            try {
                field = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                if (field == null && c.getSuperclass() != null) {
                    return getBeanField(c.getSuperclass(), fieldName);
                }
            }

        }

        return field;
    }

    private Field getBeanFieldThrowException(Class<?> c, String fieldName) throws SecurityException, NoSuchFieldException {

        Field field = getBeanField(c, fieldName);
        if (field == null) {
            throw new IllegalStateException("No field with name '" + fieldName + "' was found. EntityClass " + c);
        }
        return field;
    }

    /**
     * Delete item from a collection of values
     * 
     * @param values Collection of values
     * @param itemIndex An index of an item to remove
     */
    @SuppressWarnings("rawtypes")
    public void deleteItemFromCollection(Collection values, int itemIndex) {

        int index = 0;
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (itemIndex == index) {
                iterator.remove();
                return;
            }
            index++;
        }
    }

    /**
     * Change value in a collection. Collection to update an item index are passed as attributes
     * 
     * @param event Value change event
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final void updateItemInCollection(ValueChangeEvent event) {

        Collection values = (Collection) event.getComponent().getAttributes().get("values");

        values.remove(event.getOldValue());
        values.add(event.getNewValue());

        // Unpredictable results when changing several values at a time, as Set does not guarantee same value oder - could be used only in Ajax and only with refresh
        // int itemIndex = (int) event.getComponent().getAttributes().get("itemIndex");
        // log.error("AKK changing value from {} to {} in index {} values {}", event.getOldValue(), event.getNewValue(), itemIndex, values.toArray());
        // ArrayList newValues = new ArrayList();
        // newValues.addAll(values);
        //
        // newValues.remove(itemIndex);
        // newValues.add(itemIndex, event.getNewValue());
        // values.clear();
        // values.addAll(newValues);
        // log.error("AKK end changing value from {} to {} in index {} values {}", event.getOldValue(), event.getNewValue(), itemIndex, values.toArray());
    }

    /**
     * Add a new blank item to collection. Instantiate a new item based on parametized collection type.
     * 
     * @param values A collection of values
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addItemToCollection(Collection values, Class itemClass) {

        try {
            values.add(itemClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate a new item of {} class", itemClass.getName());
        }
    }

    /**
     * Determine a generics type of a field (eg. for Set&lt;String&gt; field should return String)
     * 
     * @param fieldName Field name
     * @param childFieldName child field name in case of field hierarchy
     * @return A class
     */
    @SuppressWarnings("rawtypes")
    public Class getFieldGenericsType(String fieldName, String childFieldName) {

        Field field = getBeanField(getEntityClassFromEntity(), fieldName);

        if (childFieldName == null) {
            field = getBeanField(field.getType(), childFieldName);
        }

        if (field != null && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) field.getGenericType();
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                Class fieldArgClass = (Class) fieldArgType;
                return fieldArgClass;
            }

        }
        return null;
    }
}