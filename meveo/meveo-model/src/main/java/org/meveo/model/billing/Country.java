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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.meveo.model.ProviderlessEntity;
import org.meveo.model.admin.Currency;

@Entity
@Table(name = "ADM_COUNTRY")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "BILLING_COUNTRY_SEQ")
public class Country extends ProviderlessEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "COUNTRY_CODE", length = 2)
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

}
