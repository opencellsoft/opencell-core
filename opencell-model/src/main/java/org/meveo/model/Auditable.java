/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.security.MeveoUser;

@Embeddable
public class Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false, updatable = false)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date updated;

	@Column(name = "creator", updatable = false, length = 100)
	private String creator;

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
