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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.meveo.model.admin.User;

/**
 * Base class for entities that does not have providers.
 * 
 * @author Ignas Lelys
 * @created Apr 14, 2011
 * 
 */
@MappedSuperclass
public abstract class ProviderlessEntity implements Serializable, IEntity, IAuditable {

	private static final long serialVersionUID = 1L;

	@Id
	// @GeneratedValue(generator = "ID_GENERATOR")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@Embedded
	private Auditable auditable;

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
		return super.hashCode();
	}

	/**
	 * Equals method must be overridden in concrete Entity class. Entities
	 * shouldn't be compared only by ID, because if entity is not persisted its
	 * ID is null.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		throw new IllegalStateException("Equals method was not overriden!");
	}

}
