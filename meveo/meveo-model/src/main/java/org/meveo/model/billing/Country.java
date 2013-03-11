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

package org.meveo.model.billing;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jboss.seam.annotations.AutoCreate;
import org.meveo.model.AuditableEntity;

/**
 * Country entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "COUNTRY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_COUNTRY_SEQ")

public class Country  extends AuditableEntity{
	private static final long serialVersionUID = 1L;

	@Column(name = "COUNTRY_CODE", length = 2)
	private String countryCode;


	@Column(name = "DESCRIPTION_EN", length = 100)
	private String descriptionEn;


	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;


	@Column(name = "LANGUAGE_CODE", length = 3)
	private String languageCode;


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


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public String getLanguageCode() {
		return languageCode;
	}


	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}




}
