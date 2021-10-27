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

package org.meveo.validation.constraint;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AttributeTypeValidator implements ConstraintValidator<AttributeTypeValidation, Attribute> {


    @Override
    public void initialize(AttributeTypeValidation constraintAnnotation) {

    }

    @Override
    public boolean isValid(Attribute attribute, ConstraintValidatorContext context) {
        if (attribute == null) {
            return false;
        }
        HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                HibernateConstraintValidatorContext.class );
        hibernateContext.disableDefaultConstraintViolation();
        if (attribute.getAttributeType() != null && attribute.getAttributeType().equals(AttributeTypeEnum.LIST_MULTIPLE_NUMERIC)) {
           if (attribute.getAllowedValues() == null || attribute.getAllowedValues().isEmpty()) {
               return true;
           }
           for (String value : attribute.getAllowedValues()) {
               if (!value.matches("\\d+")) {
                   hibernateContext
                           .addExpressionVariable( "validatedValue", value )
                           .buildConstraintViolationWithTemplate( "${validatedValue} is not a valid number" )
                           .addConstraintViolation();
                   return false;
               }
           }
           return true;
        }
        return true;
    }
}
