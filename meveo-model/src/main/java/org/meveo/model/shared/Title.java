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
