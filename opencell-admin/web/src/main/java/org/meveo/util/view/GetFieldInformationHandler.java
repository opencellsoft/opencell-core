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

package org.meveo.util.view;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.faces.application.Application;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.EnumBuilder;
import org.meveo.commons.utils.MeveoEnum;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.IEntity;
import org.meveo.model.annotation.ImageType;
import org.meveo.util.view.FieldInformation.FieldNumberTypeEnum;
import org.meveo.util.view.FieldInformation.FieldTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tag handler to get meta information about an entity field
 * 
 * @author Andrius Karpavicius
 * 
 */
public class GetFieldInformationHandler extends TagHandler {

    private static Logger log = LoggerFactory.getLogger(GetFieldInformationHandler.class);

    private String backingBean;
    private String entity;
    private String entityClassName;
    private String defaultEntityFromBean;
    private String fieldName;
    private String childFieldName;
    private String varName;

    /**
     * Tag configuration. Accepts the following attributes:
     * <ul>
     * <li>backingBean - BaseBean instance with entity field. Used to access "entity" if entity parameter is not passed</li>
     * <li>entity - Entity object to read field metadata from</li>
     * <li>entityClass - Explicit entity class for cases when it can not be determined (super entity with fields in child entity
     * <li>defaultEntityFromBean - does entity correspond to backingBean.entity as was set by default in hftl:formField tag</li>
     * <li>fieldName - name of a field. Can contain "." in a name</li>
     * <li>childFieldName - name of a secondary field.</li>
     * <li>var - name of a variable to post information to</li>
     * </ul>
     * 
     * In terms of field resolution the following is used: entity.fieldName.childFieldName or backingBea.clazz.fieldName.childFieldName
     * 
     * @param config Tag configuration
     */
    public GetFieldInformationHandler(TagConfig config) {
        super(config);

        // if EL expression was passed as attribute, getAttribute() return only a EL expression, not an actual resolution of EL value
        if (getAttribute("backingBean") != null) {
            backingBean = getAttribute("backingBean").getValue();
        }

        if (getAttribute("entity") != null) {
            entity = getAttribute("entity").getValue();
        }

        if (getAttribute("entityClass") != null) {
            entityClassName = getAttribute("entityClass").getValue();
        }

        if (getAttribute("defaultEntityFromBean") != null) {
            defaultEntityFromBean = getAttribute("defaultEntityFromBean").getValue();
        }

        fieldName = getRequiredAttribute("fieldName").getValue();

        if (getAttribute("childFieldName") != null) {
            childFieldName = getAttribute("childFieldName").getValue();
        }
        varName = getRequiredAttribute("var").getValue();

    }

    /**
     * Tag resolution/application
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void apply(FaceletContext context, UIComponent parent) throws IOException {
        Class entityClass = null;

        if (entityClassName != null) {
            String entityClassNameVal = (String) executeExpressionInUIContext(context, entityClassName);
            if (entityClassNameVal != null) {

                try {
                    entityClass = Class.forName(entityClassNameVal);
                } catch (ClassNotFoundException e) {
                    log.error("Invalid classname {}", entityClassNameVal);
                }
            }
        }

        // Either entity or backing bean must be set. Resolve the values

        // // Code is ok, but need to better test before release.
        // // When entity is set, but it was determined by default from backingBean.entity in hftl:formField tag, use backingBean.getClazz() to determine entity's class. This
        // solves
        // // the problem of dynamic dialogs #1816.
        //
        // boolean isDefaultEntityFromBean = false;
        // if (defaultEntityFromBean != null) {
        // isDefaultEntityFromBean = (boolean) executeExpressionInUIContext(context, defaultEntityFromBean);
        // }
        //
        // if (entity != null && !isDefaultEntityFromBean) {

        if (entityClass == null && entity != null) {
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
            field = ReflectionUtils.getFieldThrowException(entityClass, fullFieldName);
        } catch (NoSuchFieldException e) {
            // log.error("Not able to access field information for {} field of {} class backing bean {} entity {}", fullFieldName, entityClass.getName(), backingBean, entity);
            context.setAttribute(varName, new FieldInformation());
            return;
        }
        Class<?> fieldClassType = field.getType();

        FieldInformation fieldInfo = new FieldInformation();

        fieldInfo.required = field.isAnnotationPresent(NotNull.class) || (field.isAnnotationPresent(Column.class) && !((Column) field.getAnnotation(Column.class)).nullable())
                || (field.isAnnotationPresent(JoinColumn.class) && !((JoinColumn) field.getAnnotation(JoinColumn.class)).nullable());

        if (fieldClassType == String.class) {
            if (field.isAnnotationPresent(ImageType.class)) {
                fieldInfo.fieldType = FieldTypeEnum.Image;

            } else {
                fieldInfo.fieldType = FieldTypeEnum.Text;

                if (field.isAnnotationPresent(Size.class)) {
                    int maxLength = field.getAnnotation(Size.class).max();
                    if (maxLength > 0) {
                        fieldInfo.maxLength = maxLength;
                    }
                }
            }

        } else if (fieldClassType == Boolean.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("boolean"))) {
            fieldInfo.fieldType = FieldTypeEnum.Boolean;

        } else if (fieldClassType == Date.class) {
            fieldInfo.fieldType = FieldTypeEnum.Date;

        } else if (fieldClassType.isEnum() || fieldClassType.isAnnotationPresent(MeveoEnum.class)) {
            fieldInfo.fieldType = FieldTypeEnum.Enum;
            fieldInfo.enumClassname = field.getType().getName();

            Object[] objArr = null;
             if (fieldClassType.isEnum()) {
                 objArr = field.getType().getEnumConstants();
                 Arrays.sort(objArr, new Comparator<Object>() {
                     @Override
                     public int compare(Object o1, Object o2) {
                         return o1.toString().compareTo(o2.toString());
                     }
                 });
             } else if( fieldClassType.isAnnotationPresent(MeveoEnum.class) ) {
                 MeveoEnum meveoEnum = fieldClassType.getAnnotation(MeveoEnum.class);
                 fieldInfo.enumClassname = meveoEnum.identifier().getName();
                 objArr = EnumBuilder.values(fieldInfo.enumClassname);
             }

            fieldInfo.enumListValues = objArr;

        } else if (IEntity.class.isAssignableFrom(fieldClassType)) {
            fieldInfo.fieldType = FieldTypeEnum.Entity;

        } else if (fieldClassType == List.class || fieldClassType == Set.class) {
            fieldInfo.fieldType = FieldTypeEnum.List;
            fieldInfo.fieldGenericsType = ReflectionUtils.getFieldGenericsType(field);

        } else if (fieldClassType == Map.class || fieldClassType == HashMap.class) {
            fieldInfo.fieldType = FieldTypeEnum.Map;
            fieldInfo.fieldGenericsType = ReflectionUtils.getFieldGenericsType(field);

        } else if (fieldClassType == Integer.class || fieldClassType == Long.class || fieldClassType == Byte.class || fieldClassType == Short.class
                || fieldClassType == Double.class || fieldClassType == Float.class || fieldClassType == BigDecimal.class
                || (fieldClassType.isPrimitive() && (fieldClassType.getName().equals("int") || fieldClassType.getName().equals("long") || fieldClassType.getName().equals("byte")
                        || fieldClassType.getName().equals("short") || fieldClassType.getName().equals("double") || fieldClassType.getName().equals("float")))) {

            if (fieldClassType == Integer.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("int"))) {
                fieldInfo.numberConverter = "jakarta.faces.Integer";
                fieldInfo.numberType = FieldNumberTypeEnum.Integer;
            } else if (fieldClassType == Long.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("long"))) {
                fieldInfo.numberConverter = "jakarta.faces.Long";
                fieldInfo.numberType = FieldNumberTypeEnum.Long;
            } else if (fieldClassType == Byte.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("byte"))) {
                fieldInfo.numberConverter = "jakarta.faces.Byte";
                fieldInfo.numberType = FieldNumberTypeEnum.Byte;
            } else if (fieldClassType == Short.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("short"))) {
                fieldInfo.numberConverter = "jakarta.faces.Short";
                fieldInfo.numberType = FieldNumberTypeEnum.Short;
            } else if (fieldClassType == Double.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("double"))) {
                fieldInfo.numberConverter = "jakarta.faces.Double";
                fieldInfo.numberType = FieldNumberTypeEnum.Double;
            } else if (fieldClassType == Float.class || (fieldClassType.isPrimitive() && fieldClassType.getName().equals("float"))) {
                fieldInfo.numberConverter = "jakarta.faces.Float";
                fieldInfo.numberType = FieldNumberTypeEnum.Float;
            } else if (fieldClassType == BigDecimal.class) {
                fieldInfo.numberConverter = "jakarta.faces.BigDecimal";
                fieldInfo.numberType = FieldNumberTypeEnum.BigDecimal;
            }

            fieldInfo.fieldType = FieldTypeEnum.Number;

        } else if (fieldClassType == DatePeriod.class) {

            fieldInfo.fieldType = FieldTypeEnum.DatePeriod;
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
}