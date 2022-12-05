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

import java.util.HashSet;
import java.util.Set;

import org.meveo.model.audit.AuditableFieldHistory;
import org.meveo.security.MeveoUser;

import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * Base class for entities that track creation/modification of the record
 *
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity implements IAuditable {

    private static final long serialVersionUID = 1L;

    /**
     * Create/update timestamp information
     */
    @Embedded
    protected Auditable auditable;

    /**
     * Is historized
     */
    @Transient
    private boolean historized;

    /**
     * Is notified
     */
    @Transient
    private boolean notified;

    /**
     * Auditable entity fields
     */
    @Transient
    Set<AuditableFieldHistory> auditableFields = new HashSet<>();

    public AuditableEntity() {
    }

    public AuditableEntity(Auditable auditable) {
        this.auditable = auditable;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public void updateAudit(MeveoUser u) {
        if (auditable == null) {
            auditable = new Auditable(u);
        } else {
            auditable.updateWith(u);
        }
    }

    /**
     * Gets the historized
     *
     * @return the historized
     */
    public boolean isHistorized() {
        return historized;
    }

    /**
     * Sets the historized.
     *
     * @param historized the new historized
     */
    public void setHistorized(boolean historized) {
        this.historized = historized;
    }

    /**
     * Gets the notified
     *
     * @return the notified
     */
    public boolean isNotified() {
        return notified;
    }

    /**
     * Sets the notified.
     *
     * @param notified the new notified
     */
    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    /**
     * Gets the auditable fields
     *
     * @return the auditable fields
     */
    public Set<AuditableFieldHistory> getAuditableFields() {
        return auditableFields;
    }

    /**
     * Sets the auditable fields.
     *
     * @param auditableFields the new auditable fields
     */
    public void setAuditableFields(Set<AuditableFieldHistory> auditableFields) {
        this.auditableFields = auditableFields;
    }
}
