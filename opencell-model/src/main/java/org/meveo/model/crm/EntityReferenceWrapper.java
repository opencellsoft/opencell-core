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

package org.meveo.model.crm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.customEntities.CustomEntityInstance;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a custom field value type - reference to an Meveo entity identified by a classname and code. In case a class is a generic Custom Entity Template a classnameCode is
 * required to identify a concrete custom entity template by its code
 * 
 * @author Andrius Karpavicius
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityReferenceWrapper implements Serializable, IReferenceEntity {

    private static final long serialVersionUID = -4756870628233941711L;

    /**
     * Classname of an entity
     */
    private String classname;

    /**
     * Custom entity template code - applicable and required when reference is to Custom Entity Template type
     */
    private String classnameCode;

    /**
     * Entity code
     */
    private String code;

    /**
     * Entity id
     */
    private Long id;

    public EntityReferenceWrapper() {
    }

    /**
     * Constructor
     * 
     * @param entity Reference to store
     */
    public EntityReferenceWrapper(BusinessEntity entity) {
        super();
        if (entity == null) {
            return;
        }
        classname = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        if (entity instanceof CustomEntityInstance) {
            classnameCode = ((CustomEntityInstance) entity).getCetCode();
        }
        code = entity.getCode();
        id = (Long) ((IEntity) entity).getId();
    }

    /**
     * Constructor
     * 
     * @param entity Reference to store
     */
    public EntityReferenceWrapper(IReferenceEntity entity) {
        super();
        if (entity == null) {
            return;
        }
        classname = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        if (entity instanceof CustomEntityInstance) {
            classnameCode = ((CustomEntityInstance) entity).getCetCode();
        }
        code = entity.getReferenceCode();
        if (entity instanceof IEntity) {
            id = (Long) ((IEntity) entity).getId();
        }
    }

    /**
     * Constructor
     * 
     * @param classname Classname of an entity
     * @param classnameCode Custom entity template code - applicable and required when reference is to Custom Entity Template type
     * @param code Entity code
     */
    public EntityReferenceWrapper(String classname, String classnameCode, String code) {
        this.classname = classname;
        this.classnameCode = classnameCode;
        this.code = code;
    }

    /**
     * @return Classname of an entity
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname Classname of an entity
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return Custom entity template code - applicable and required when reference is to Custom Entity Template type
     */
    public String getClassnameCode() {
        return classnameCode;
    }

    /**
     * @param classnameCode Custom entity template code - applicable and required when reference is to Custom Entity Template type
     */
    public void setClassnameCode(String classnameCode) {
        this.classnameCode = classnameCode;
    }

    /**
     * @return Entity code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code Entity code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Entity identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Entity identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return code == null;
    }

    @Override
    public String toString() {
        return String.format("EntityReferenceWrapper [classname=%s, classnameCode=%s, code=%s, referenceCode=%s]", classname, classnameCode, code, getReferenceCode());
    }

    @Override
    public String getReferenceCode() {
        return getCode();
    }

    @Override
    public void setReferenceCode(Object value) {
        setCode(value.toString());
    }

    @Override
    public String getReferenceDescription() {
        return null;
    }

    /**
     * Convert fields to a map of values
     * 
     * @return A map of field values
     */
    public Map<String, String> toMap() {

        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("classname", classname);
        if (classnameCode != null) {
            map.put("classnameCode", classnameCode);
        }

        return map;
    }
}