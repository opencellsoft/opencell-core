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

import org.meveo.model.AuditableEntity;

/**
 * InvoiceSubcategoryCountry entity.
 */
@Entity
@Table(name = "BILLING_INV_SUB_CAT_COUNTRY")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INV_SUB_CAT_COUNTRY_SEQ")
public class InvoiceSubcategoryCountry extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_SUB_CATEGORY_ID")
	private InvoiceSubCategory invoiceSubCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_COUNTRY_ID")
	private TradingCountry tradingCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAX_ID")
	private Tax tax;
	
	@Column(name = "FILTER_EL", length = 2000)
	private String filterEL;

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;

	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public TradingCountry getTradingCountry() {
		return tradingCountry;
	}

	public void setTradingCountry(TradingCountry tradingCountry) {
		this.tradingCountry = tradingCountry;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}
	
	

	public String getFilterEL() {
		return filterEL;
	}

	public void setFilterEL(String filterEL) {
		this.filterEL = filterEL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		InvoiceSubcategoryCountry other = (InvoiceSubcategoryCountry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
