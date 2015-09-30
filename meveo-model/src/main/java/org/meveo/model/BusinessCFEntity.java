/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class BusinessCFEntity extends BusinessEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = -6054446440106807337L;

    @Override
    public Object getCFValue(String cfCode) {
        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode).getValue();
        }
        return null;
    }

    @Override
    public Object getCFValue(String cfCode, Date date) {
        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode).getValue(date);
        }
        return null;
    }

    public void setCFValue(String cfCode, Object value) {
        setCFValue(cfCode, value, null);
    }

    public void setCFValue(String cfCode, Object value, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                cfi = new CustomFieldInstance();
                cfi.setCode(cfCode);
            }

            String relationshipFieldname = this.getClass().getAnnotation(CustomFieldEntity.class).accountLevel().getRelationFieldname();
            try {
                FieldUtils.getField(CustomFieldInstance.class, relationshipFieldname, true).set(cfi, this);

            } catch (IllegalArgumentException | IllegalAccessException e) {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Failed to access CFI to entity relationship field CustomFieldInstance.{}", relationshipFieldname);
                return;
            }
            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value);
    }

    public void setCFValue(String cfCode, Object value, Date valueDate, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                // cfi = new CustomFieldInstance();
                // cfi.setCode(cfCode);
                // cfi.setVersionable(true);
                throw new RuntimeException("Can not determine a period for Custom Field value if no calendar is provided");
            }

            String relationshipFieldname = this.getClass().getAnnotation(CustomFieldEntity.class).accountLevel().getRelationFieldname();
            try {
                FieldUtils.getField(CustomFieldInstance.class, relationshipFieldname, true).set(cfi, this);

            } catch (IllegalArgumentException | IllegalAccessException e) {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Failed to access CFI to entity relationship field CustomFieldInstance.{}", relationshipFieldname);
                return;
            }
            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value, valueDate);
    }

    public void setCFValue(String cfCode, Object value, Date valueDateFrom, Date valueDateTo, CustomFieldTemplate cft) {

        CustomFieldInstance cfi = getCustomFields().get(cfCode);
        if (cfi == null) {
            if (value == null) {
                return;
            }
            if (cft != null) {
                cfi = CustomFieldInstance.fromTemplate(cft);
            } else {
                cfi = new CustomFieldInstance();
                cfi.setCode(cfCode);
                cfi.setVersionable(true);
            }

            String relationshipFieldname = this.getClass().getAnnotation(CustomFieldEntity.class).accountLevel().getRelationFieldname();
            try {
                FieldUtils.getField(CustomFieldInstance.class, relationshipFieldname, true).set(cfi, this);

            } catch (IllegalArgumentException | IllegalAccessException e) {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Failed to access CFI to entity relationship field CustomFieldInstance.{}", relationshipFieldname);
                return;
            }
            this.getCustomFields().put(cfCode, cfi);
        }
        cfi.setValue(value, valueDateFrom, valueDateTo);
    }

    @Override
    public Object getInheritedOnlyCFValue(String cfCode) {
        if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode);
        }
        return null;
    }

    @Override
    public Object getInheritedOnlyCFValue(String cfCode, Date date) {

        if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode, date);
        }
        return null;
    }

    @Override
    public Object getInheritedCFValue(String cfCode) {

        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode).getValue();

        } else if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode);
        }
        return null;
    }

    @Override
    public Object getInheritedCFValue(String cfCode, Date date) {

        Object value = null;

        if (getCustomFields().containsKey(cfCode)) {
            value = getCustomFields().get(cfCode).getValue(date);
        }
        if (value == null && getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode, date);
        }
        return null;
    }
}