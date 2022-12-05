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
package org.meveo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

import org.hibernate.annotations.JavaTypeRegistration;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.persistence.CustomFieldJsonDataType;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

/**
 * Base class for all entity classes.
 */
@JavaTypeRegistration(javaType = CustomFieldValues.class, descriptorClass = CustomFieldJsonDataType.class)
@MappedSuperclass
public abstract class BaseEntity implements Serializable, IEntity, IJPAVersionedEntity {
    private static final long serialVersionUID = 1L;

    public static final int NB_PRECISION = 23;
    public static final int NB_DECIMALS = 12;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Record/entity identifier
     */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    @JsonProperty
    protected Long id;

    /**
     * Modification version number
     */
    @Version
    @Column(name = "version")
    protected Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isTransient() {
        return id == null;
    }

    @Override
    public int hashCode() {
        return 961 + (this.getClass().getName() + id).hashCode();
    }

    /**
     * Equals method must be overridden in concrete Entity class. Entities shouldn't be compared only by ID, because if entity is not persisted its ID is null.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        throw new IllegalStateException("Equals method was not overriden in " + getClass().getName());
    }

    @Override
    public String toString() {
        return String.format("id=%s", id);
    }

    /**
     * Clean up code/identifier value. Replace spaces and '-' with '_'.
     * 
     * @param codeOrId Code or identifier value
     * @return Modifier code/identifier value
     */
    public static String cleanUpCodeOrId(Object codeOrId) {

        if (codeOrId == null) {
            return null;
        }

        if (codeOrId instanceof Long) {
            return codeOrId.toString();
        } else if (codeOrId instanceof BigDecimal) {
            return Long.toString(((BigDecimal) codeOrId).longValue());
        } else if (codeOrId instanceof BigInteger) {
            return ((BigInteger) codeOrId).toString();
        } else {
            codeOrId = ((String) codeOrId).replace(' ', '_');
            codeOrId = ((String) codeOrId).replace('-', '_');
            return (String) codeOrId;
        }
    }

    /**
     * Clean up code/identifier value (Replace spaces with '_') and lowercase it
     * 
     * @param codeOrId Code or identifier value
     * @return Modifier code/identifier value
     */
    public static String cleanUpAndLowercaseCodeOrId(Object codeOrId) {

        if (codeOrId == null) {
            return null;
        }

        codeOrId = cleanUpCodeOrId(codeOrId).toLowerCase();
        return (String) codeOrId;
    }
}