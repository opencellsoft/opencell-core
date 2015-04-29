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
package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.ProviderlessEntity;
import org.meveo.model.admin.Currency;

@Entity
@ExportIdentifier("countryCode")
@Table(name = "ADM_COUNTRY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_COUNTRY_SEQ")
public class Country extends ProviderlessEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "COUNTRY_CODE", length = 10)
	private String countryCode;

	@Column(name = "DESCRIPTION_EN", length = 100)
	private String descriptionEn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCY_ID")
	private Currency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LANGUAGE_ID")
	private Language language;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String toString() {
		return countryCode;
	}

	@Override
	public boolean equals(Object other){
		if(other==null || !(other instanceof Country)){
			return false;
		}
		Country o = (Country) other;
		return (o.countryCode!=null) && o.countryCode.equals(this.countryCode);
	}
}
