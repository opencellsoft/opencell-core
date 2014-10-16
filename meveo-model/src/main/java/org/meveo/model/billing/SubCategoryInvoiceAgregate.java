/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("F")
public class SubCategoryInvoiceAgregate extends InvoiceAgregate {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoiceSubCategory")
	private InvoiceSubCategory invoiceSubCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_CATEGORY_TAX")
	private Tax subCategoryTax;

	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "CATEGORY_INVOICE_AGREGATE")
	private CategoryInvoiceAgregate categoryInvoiceAgregate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAX_INVOICE_AGREGATE")
	private TaxInvoiceAgregate taxInvoiceAgregate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WALLET_ID")
	private WalletInstance wallet;

	@OneToMany(mappedBy = "invoiceAgregateF", fetch = FetchType.LAZY)
	private List<RatedTransaction> ratedtransactions = new ArrayList<RatedTransaction>();

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public Tax getSubCategoryTax() {
		return subCategoryTax;
	}

	public void setSubCategoryTax(Tax subCategoryTax) {
		this.subCategoryTax = subCategoryTax;
	}

	public CategoryInvoiceAgregate getCategoryInvoiceAgregate() {
		return categoryInvoiceAgregate;
	}

	public void setCategoryInvoiceAgregate(CategoryInvoiceAgregate categoryInvoiceAgregate) {
		this.categoryInvoiceAgregate = categoryInvoiceAgregate;
		if (categoryInvoiceAgregate != null) {
			categoryInvoiceAgregate.getSubCategoryInvoiceAgregates().add(this);
		}
	}

	public TaxInvoiceAgregate getTaxInvoiceAgregate() {
		return taxInvoiceAgregate;
	}

	public void setTaxInvoiceAgregate(TaxInvoiceAgregate taxInvoiceAgregate) {

		if (taxInvoiceAgregate != null) {
			taxInvoiceAgregate.getSubCategoryInvoiceAgregates().add(this);
		}
		this.taxInvoiceAgregate = taxInvoiceAgregate;
	}

	public List<RatedTransaction> getRatedtransactions() {
		return ratedtransactions;
	}

	public void setRatedtransactions(List<RatedTransaction> ratedtransactions) {
		this.ratedtransactions = ratedtransactions;
	}

	public WalletInstance getWallet() {
		return wallet;
	}

	public void setWallet(WalletInstance wallet) {
		this.wallet = wallet;
	}
	
	

}
