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
import java.util.Date;

import org.meveo.security.MeveoUser;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Tracks record/entity create/update information
 * 
 * @author Andrius Karpavicius
 */
@Embeddable
public class Auditable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Record/entity creation timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created;

    /**
     * Last record/entity update timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    /**
     * Username of a user that created the record/entity
     */
    @Column(name = "creator", updatable = false, length = 100)
    private String creator;

    /**
     * Username of a user that last updated the record/entity
     */
    @Column(name = "updater", length = 100)
    private String updater;

    public Auditable() {
    }

    public Auditable(MeveoUser creator) {
        super();
        this.creator = creator.getUserName();
        this.created = new Date();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Date getLastModified() {
        return (updated != null) ? updated : created;
    }

    public String getLastUser() {
        return (updater != null) ? updater : creator;
    }

    public void updateWith(MeveoUser currentUser) {
        this.updated = new Date();
        this.updater = currentUser.getUserName();

        // Make sure that creator and created fields are set in case entity was imported or entered by some other means
        if (this.creator == null) {
            this.creator = currentUser.getUserName();
        }
        if (this.created == null) {
            this.created = this.updated;
        }
    }
    
    public int compareByUpdated(Auditable other) {
    	 if (this.updated == null && other.updated == null) {
             return 0;
         } else if (this.updated != null && other.updated == null) {
             return 1;
         } else if (this.updated == null && other.updated != null) {
             return -1;
         } else if (this.updated != null) {
             return this.updated.compareTo(other.updated);
         }
         return 0;
    }

    /**
     * Is current user a creator of this entity
     * 
     * @param currentUser Current user
     * @return True if current user is a creator of this entity
     */
    public boolean isCreator(MeveoUser currentUser) {
        return currentUser.getUserName().equals(this.creator);
    }
}
