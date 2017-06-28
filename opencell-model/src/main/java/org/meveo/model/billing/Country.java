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
package org.meveo.model.billing;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.Currency;

@Entity
@ExportIdentifier("countryCode")
@Table(name = "adm_country")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "adm_country_seq"), })
public class Country extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "country_code", length = 10)
	@Size(max = 10)
	private String countryCode;

	@Column(name = "description_en", length = 100)
	@Size(max = 100)
	private String descriptionEn;
	
	@Column(name = "description_fr", length = 100)
	@Size(max = 100)
	private String descriptionFr;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency_id")
	private Currency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "language_id")
	private Language language;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	
	public String getDescription_ENG() {
		return getDescriptionEn();
	}
	
	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public String getDescription_FRA() {
		return getDescriptionFr();
	}
	
	public String getDescriptionFr() {
		return descriptionFr;
	}

	public void setDescriptionFr(String descriptionFr) {
		this.descriptionFr = descriptionFr;
	}
	
	public String getDescription(String languageCode)  {
		
		Method method = null;
		try {
			method = this.getClass().getMethod("getDescription_"+languageCode);
			String description = (String)method.invoke(this);
			return description;
		} catch (NoSuchMethodException | SecurityException e) {
			return descriptionEn;
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			return descriptionEn;
		} 
		
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
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Country)) {
            return false;
        }
        
		Country o = (Country) obj;
		return (o.countryCode!=null) && o.countryCode.equals(this.countryCode);
	}
}
