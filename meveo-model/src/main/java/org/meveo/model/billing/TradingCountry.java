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

import java.util.List;

import javax.persistence.Cacheable;
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
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
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
	
	public boolean equals(Object other){
		if(other==null || !(other instanceof TradingCountry)){
			return false;
		}
		TradingCountry o = (TradingCountry) other;
		if(o.getProvider()==null || !o.getProvider().equals(this.getProvider())){
			return false;
		}
		return (o.country!=null) && o.country.equals(this.country);
	}

}
