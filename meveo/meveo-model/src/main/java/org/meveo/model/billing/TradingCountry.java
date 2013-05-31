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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "BILLING_TRADING_COUNTRY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_TRADING_COUNTRY_SEQ")
public class TradingCountry extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "tradingCountry", fetch = FetchType.LAZY)
	private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID")
	private Country country;

	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;

	@Transient
	String countryCode;

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public List<InvoiceSubcategoryCountry> getInvoiceSubcategoryCountries() {
		return invoiceSubcategoryCountries;
	}

	public void setInvoiceSubcategoryCountries(
			List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
		this.invoiceSubcategoryCountries = invoiceSubcategoryCountries;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCountryCode() {
		return country.getCountryCode();
	}

	public String toString() {
		return "" + country;
	}

}
