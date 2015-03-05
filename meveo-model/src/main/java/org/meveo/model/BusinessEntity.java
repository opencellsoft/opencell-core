/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@MappedSuperclass
public class BusinessEntity extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", nullable = false, length = 60)
	// TODO : Create sql script to ad index. @Index(name = "CODE_IDX")
	@Size(max = 60, min = 1)
	@NotNull
	protected String code;

	@Column(name = "DESCRIPTION", nullable = true, length = 100)
	@Size(max = 100)
	protected String description;

	@Transient
	protected boolean appendGeneratedCode = false;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the appendGeneratedCode
	 */
	public boolean isAppendGeneratedCode() {
		return appendGeneratedCode;
	}

	/**
	 * @param appendGeneratedCode
	 *            the appendGeneratedCode to set
	 */
	public void setAppendGeneratedCode(boolean appendGeneratedCode) {
		this.appendGeneratedCode = appendGeneratedCode;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof BusinessEntity)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        BusinessEntity other = (BusinessEntity) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
            // return true;
        }

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return super.toString() + ", code=" + code;
	}
}
