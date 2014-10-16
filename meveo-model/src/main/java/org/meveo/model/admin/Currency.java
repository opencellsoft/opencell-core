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
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.ProviderlessEntity;

/**
 * Currency entity.
 */
@Entity
@Table(name = "ADM_CURRENCY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_CURRENCY_SEQ")
public class Currency extends ProviderlessEntity {

	private static final long serialVersionUID = 1L;

	/** Currency code e.g. EUR for euros. */
	@Column(name = "CURRENCY_CODE", length = 3, unique = true)
	private String currencyCode;

	/** Currency name. */
	@Column(name = "DESCRIPTION_EN")
	private String descriptionEn;

	/** Flag field that indicates if it is system currency. */
	@Column(name = "SYSTEM_CURRENCY")
	private Boolean systemCurrency;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public Boolean getSystemCurrency() {
		return systemCurrency;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Currency other = (Currency) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		return true;
	}

	public String toString() {
		return currencyCode;
	}

	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		return result;
	}

	public void setSystemCurrency(Boolean systemCurrency) {
		this.systemCurrency = systemCurrency;
	}
}
