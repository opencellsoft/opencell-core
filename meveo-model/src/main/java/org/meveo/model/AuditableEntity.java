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

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.meveo.security.MeveoUser;

@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity implements IAuditable {

	private static final long serialVersionUID = 1L;

	@Embedded
	private Auditable auditable;

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
}
