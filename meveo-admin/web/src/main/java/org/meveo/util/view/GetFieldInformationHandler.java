package org.meveo.util.view;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.IEntity;
import org.meveo.util.view.FieldInformation.FieldNumberTypeEnum;
import org.meveo.util.view.FieldInformation.FieldTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFieldInformationHandler extends TagHandler {

    private static Logger log = LoggerFactory.getLogger(GetFieldInformationHandler.class);

    private String backingBean;
    private String entity;
    private String fieldName;
    private String childFieldName;
    private String varName;

    public GetFieldInformationHandler(TagConfig config) {
        super(config);

        // if EL expression was passed as attribute, getAttribute() return only a EL expression, not an actual resolution of EL value
        if (getAttribute("backingBean") != null) {
            backingBean = getAttribute("backingBean").getValue();
        }
        if (getAttribute("entity") != null) {
            entity = getAttribute("entity").getValue();
        }

        fieldName = getRequiredAttribute("fieldName").getValue();

        if (getAttribute("childFieldName") != null) {
            childFieldName = getAttribute("childFieldName").getValue();
        }
        varName = getRequiredAttribute("var").getValue();

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        Class entityClass = null;
        // Either entity or backing bean must be set. Resolve the values
        if (entity != null) {
            Object entityObj = executeExpressionInUIContext(context, entity);
            if (entityObj != null) {
                entityClass = entityObj.getClass();
            }
        }

        if (entityClass == null && backingBean != null) {
            BaseBean backingBeanObj = (BaseBean) executeExpressionInUIContext(context, backingBean);
            if (backingBeanObj != null) {
                entityClass = backingBeanObj.getClazz();
            }

        }

        String fullFieldName = (String) executeExpressionInUIContext(context, fieldName);
        if (childFieldName != null) {
            String childFieldNameValue = (String) executeExpressionInUIContext(context, childFieldName);
            if (childFieldNameValue != null) {
                fullFieldName = fullFieldName + "." + childFieldNameValue;
            }
        }

        // log.error("AKK determining field type for {}", fullFieldName);
        Field field = null;
        try {
            field = getBeanFieldThrowException(entityClass, fullFieldName);
        } catch (SecurityException | NoSuchFieldException | IllegalStateException e) {
            //log.error("Not able to access field information for {} field of {} class backing bean {} entity {}", fullFieldName, entityClass.getName(), backingBean, entity);
            context.setAttribute(varName, new FieldInformation());
            return;
        }
        Class<?> fieldClassType = field.getType();

        FieldInformation fieldInfo = new FieldInformation();
        if (fieldClassType == String.class) {
            fieldInfo.fieldType = FieldTypeEnum.Text;

        } else if (fieldClassType == Boolean.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("boolean"))) {
            fieldInfo.fieldType = FieldTypeEnum.Boolean;

        } else if (fieldClassType == Date.class) {
            fieldInfo.fieldType = FieldTypeEnum.Date;

        } else if (fieldClassType.isEnum()) {
            fieldInfo.fieldType = FieldTypeEnum.Enum;
            fieldInfo.enumClassname = field.getType().getName();

            Object[] objArr = field.getType().getEnumConstants();
            Arrays.sort(objArr, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            fieldInfo.enumListValues = objArr;

        } else if (IEntity.class.isAssignableFrom(fieldClassType)) {
            fieldInfo.fieldType = FieldTypeEnum.Entity;

        } else if (fieldClassType == List.class || fieldClassType == Set.class) {
            fieldInfo.fieldType = FieldTypeEnum.List;
            fieldInfo.fieldGenericsType = getFieldGenericsType(field);

        } else if (fieldClassType == Map.class || fieldClassType == HashMap.class) {
            fieldInfo.fieldType = FieldTypeEnum.Map;
            fieldInfo.fieldGenericsType = getFieldGenericsType(field);

        } else if (fieldClassType == Integer.class
                || fieldClassType == Long.class
                || fieldClassType == Byte.class
                || fieldClassType == Short.class
                || fieldClassType == Double.class
                || fieldClassType == Float.class
                || fieldClassType == BigDecimal.class
                || (fieldClassType.isPrimitive() && (fieldClassType.getName().equals("int") || fieldClassType.getName().equals("long") || fieldClassType.getName().equals("byte")
                        || fieldClassType.getName().equals("short") || fieldClassType.getName().equals("double") || fieldClassType.getName().equals("float")))) {

            if (fieldClassType == Integer.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("int"))) {
                fieldInfo.numberConverter = "javax.faces.Integer";
                fieldInfo.numberType = FieldNumberTypeEnum.Integer;
            } else if (fieldClassType == Long.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("long"))) {
                fieldInfo.numberConverter = "javax.faces.Long";
                fieldInfo.numberType = FieldNumberTypeEnum.Long;
            } else if (fieldClassType == Byte.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("byte"))) {
                fieldInfo.numberConverter = "javax.faces.Byte";
                fieldInfo.numberType = FieldNumberTypeEnum.Byte;
            } else if (fieldClassType == Short.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("short"))) {
                fieldInfo.numberConverter = "javax.faces.Short";
                fieldInfo.numberType = FieldNumberTypeEnum.Short;
            } else if (fieldClassType == Double.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("double"))) {
                fieldInfo.numberConverter = "javax.faces.Double";
                fieldInfo.numberType = FieldNumberTypeEnum.Double;
            } else if (fieldClassType == Float.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("float"))) {
                fieldInfo.numberConverter = "javax.faces.Float";
                fieldInfo.numberType = FieldNumberTypeEnum.Float;
            } else if (fieldClassType == BigDecimal.class) {
                fieldInfo.numberConverter = "javax.faces.BigDecimal";
                fieldInfo.numberType = FieldNumberTypeEnum.BigDecimal;
            }

            fieldInfo.fieldType = FieldTypeEnum.Number;

        }

        context.setAttribute(varName, fieldInfo);

        // context.getVariableMapper().resolveVariable(name));
    }

    private Object executeExpressionInUIContext(final FaceletContext faceletContext, final String expression) {

        final FacesContext facesContext = faceletContext.getFacesContext();

        final ELContext elContext = facesContext.getELContext();
        final Application application = facesContext.getApplication();

        Object result = executeExpressionInElContext(application, faceletContext, expression);
        if (null == result) {
            result = executeExpressionInElContext(application, elContext, expression);
        }
        return result;
    }

    private Object executeExpressionInElContext(Application application, ELContext elContext, String expression) {
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ValueExpression exp = expressionFactory.createValueExpression(elContext, expression, Object.class);
        return exp.getValue(elContext);
    }

    private Field getBeanFieldThrowException(Class<?> c, String fieldName) throws SecurityException, NoSuchFieldException {

        Field field = getBeanField(c, fieldName);
        if (field == null) {
            throw new IllegalStateException("No field with name '" + fieldName + "' was found. EntityClass " + c);
        }
        return field;
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

    /**
     * Determine a generics type of a field (eg. for Set<String> field should return String)
     * 
     * @param fieldName Field name
     * @param childFieldName child field name in case of field hierarchy
     * @return A class
     */
    @SuppressWarnings("rawtypes")
    private Class getFieldGenericsType(Field field) {

        if (field.getGenericType() instanceof ParameterizedType) {
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