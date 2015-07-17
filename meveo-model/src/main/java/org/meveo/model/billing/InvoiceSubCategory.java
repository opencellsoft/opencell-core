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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.MultilanguageEntity;

@Entity
@MultilanguageEntity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_INVOICE_SUB_CAT", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_SUB_CAT_SEQ")
@NamedQueries({			
@NamedQuery(name = "invoiceSubCategory.getNbrInvoiceSubCatNotAssociated", 
	           query = "select count(*) from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null)"
	           		+ " and v.provider=:provider"),
	           
@NamedQuery(name = "invoiceSubCategory.getInvoiceSubCatNotAssociated", 
               query = "from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null)"
   		            + " and v.provider=:provider")	           	                  	         
})
public class InvoiceSubCategory extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "ACCOUNTING_CODE", length = 255)
	private String accountingCode;

	@Column(name = "DISCOUNT")
	private BigDecimal discount;

	@OneToMany(mappedBy = "invoiceSubCategory", fetch = FetchType.LAZY)
	private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = new ArrayList<InvoiceSubcategoryCountry>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_CATEGORY_ID")
	private InvoiceCategory invoiceCategory;

	public List<InvoiceSubcategoryCountry> getInvoiceSubcategoryCountries() {
		return invoiceSubcategoryCountries;
	}

	public void setInvoiceSubcategoryCountries(List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
		this.invoiceSubcategoryCountries = invoiceSubcategoryCountries;
	}

	public String getAccountingCode() {
		return accountingCode;
	}

	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}

	public InvoiceCategory getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

}
