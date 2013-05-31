/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.meveo.model.admin.User;

@MappedSuperclass
public abstract class AuditableEntity extends EnableEntity implements IAuditable {

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

	public void updateAudit(User u) {
		if (auditable == null) {
			auditable = new Auditable(u);
		} else {
			auditable.updateWith(u);
		}
	}
}
