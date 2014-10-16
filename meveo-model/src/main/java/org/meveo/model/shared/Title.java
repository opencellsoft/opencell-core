/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.model.shared;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Cacheable
@Table(name = "ADM_TITLE", uniqueConstraints = @UniqueConstraint(columnNames = { "PROVIDER_ID",
		"CODE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_TITLE_SEQ")
public class Title extends AuditableEntity {

	// MR("Title.mr", false),
	// MISS("Title.miss", false),
	// MRS("Title.mrs", false),
	// SARL("Title.SARL", true),
	// SA("Title.SA", true);

	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", nullable = false, length = 10)
	@Size(max = 10, min = 1)
	@NotNull
	private String code;

	@Column(name = "IS_COMPANY")
	private Boolean isCompany = Boolean.FALSE;

	@Transient
	private String descriptionKey;

	public Title() {

	}

	public Title(String code, boolean isCompany) {
		this.code = code;
		this.isCompany = isCompany;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(Boolean isCompany) {
		this.isCompany = isCompany;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Title other = (Title) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	public String getDescriptionKey() {
		return "Title." + code;
	}

}
