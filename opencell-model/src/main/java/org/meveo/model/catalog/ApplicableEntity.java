package org.meveo.model.catalog;/*
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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;

/**
 * Entity accessibility rules
 */
@Embeddable
public class ApplicableEntity implements Serializable {

    private static final long serialVersionUID = 84222776645282176L;

    /**
     * Accessible entity code.
     */
    @Column(name = "code", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String code;

    /**
     * Accessible entity type/class.
     */
    @Column(name = "entity_class", length = 255)
    @Size(max = 255)
    private String entityClass;

    public ApplicableEntity() {
    }

    public ApplicableEntity(BusinessCFEntity businessEntity) {
        this.setCode(businessEntity.getCode());
        this.setEntityClass(ReflectionUtils.getCleanClassName(businessEntity.getClass().getName()));
    }

    public ApplicableEntity(ApplicableEntity applicableEntity) {
        this.setCode(applicableEntity.getCode());
        this.setEntityClass(applicableEntity.getEntityClass());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String readableEntityClass() {
        if (entityClass != null) {
            return ReflectionUtils.getHumanClassName(entityClass);
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        boolean isSecuredEntity = obj instanceof ApplicableEntity;
        boolean isBusinessEntity = obj instanceof BusinessEntity;
        if (!isSecuredEntity && !isBusinessEntity) {
            return false;
        }

        String thatCode = null;
        String thatClass = null;
        if (isSecuredEntity) {
            thatCode = ((ApplicableEntity) obj).getCode();
            thatClass = ((ApplicableEntity) obj).getEntityClass();
        }
        if (isBusinessEntity) {
            thatCode = ((BusinessEntity) obj).getCode();
            thatClass = ReflectionUtils.getCleanClassName(obj.getClass().getName());
        }

        thatCode = thatClass + "-_-" + thatCode;
        String thisCode = this.getEntityClass() + "-_-" + this.getCode();

        if (!thisCode.equals(thatCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCode(), this.getEntityClass());
    }
}
