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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.meveo.model.crm.CustomFieldFields;
import org.meveo.model.crm.CustomFieldTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class BusinessCFEntity extends BusinessEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = -6054446440106807337L;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CFF_ID")
    private CustomFieldFields cfFields;

    @Override
    public CustomFieldFields getCfFields() {
        return cfFields;
    }

    public void setCfFields(CustomFieldFields cfFields) {
        this.cfFields = cfFields;
    }

    @Override
    public void initCustomFields() {
        cfFields = new CustomFieldFields();
    }

    @Override
    public Object getCFValue(String cfCode) {
        if (cfFields != null && cfFields.getCustomFields().containsKey(cfCode)) {
            return cfFields.getCustomFields().get(cfCode).getValue();
        }
        return null;
    }

    @Override
    public Object getCFValue(String cfCode, Date date) {
        if (cfFields != null && cfFields.getCustomFields().containsKey(cfCode)) {
            return cfFields.getCustomFields().get(cfCode).getValue(date);
        }
        return null;
    }

    public void setCFValue(String cfCode, Object value) {
        setCFValue(cfCode, value, null);
    }

    public void setCFValue(String cfCode, Object value, CustomFieldTemplate cft) {

        if (cfFields == null) {
            initCustomFields();
        }

        cfFields.setCFValue(cfCode, value, cft);
    }

    public void setCFValue(String cfCode, Object value, Date valueDate, CustomFieldTemplate cft) {

        if (cfFields == null) {
            initCustomFields();
        }

        cfFields.setCFValue(cfCode, value, valueDate, cft);
    }

    public void setCFValue(String cfCode, Object value, Date valueDateFrom, Date valueDateTo, CustomFieldTemplate cft) {

        if (cfFields == null) {
            initCustomFields();
        }

        cfFields.setCFValue(cfCode, value, valueDateFrom, valueDateTo, cft);
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

        try {
            if (cfFields != null && cfFields.getCustomFields().containsKey(cfCode)) {
                return cfFields.getCustomFields().get(cfCode).getValue();

            } else if (getParentCFEntity() != null) {
                return getParentCFEntity().getInheritedCFValue(cfCode);
            }
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to access inherited CF values", e);
        }

        return null;
    }

    @Override
    public Object getInheritedCFValue(String cfCode, Date date) {

        Object value = null;

        if (cfFields != null && cfFields.getCustomFields().containsKey(cfCode)) {
            value = cfFields.getCustomFields().get(cfCode).getValue(date);
        }
        if (value == null && getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode, date);
        }
        return null;
    }
}