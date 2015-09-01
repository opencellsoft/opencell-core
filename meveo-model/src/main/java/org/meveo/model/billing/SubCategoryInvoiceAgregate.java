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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("F")
public class SubCategoryInvoiceAgregate extends InvoiceAgregate {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoiceSubCategory")
	private InvoiceSubCategory invoiceSubCategory;

	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BILLING_INVOICE_AGREGATE_TAXES", joinColumns = @JoinColumn(name = "SUB_CAT_INVOICE_AGGREGAT_ID"), inverseJoinColumns = @JoinColumn(name = "TAX_ID"))
	private Set<Tax> subCategoryTaxes = new HashSet<Tax>();


	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "CATEGORY_INVOICE_AGREGATE")
	private CategoryInvoiceAgregate categoryInvoiceAgregate;

	@OneToMany(mappedBy = "subCategoryInvoiceAggregate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<TaxInvoiceAgregate> taxInvoiceAggregates = new HashSet<TaxInvoiceAgregate>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WALLET_ID")
	private WalletInstance wallet;

	@OneToMany(mappedBy = "invoiceAgregateF", fetch = FetchType.LAZY)
	private List<RatedTransaction> ratedtransactions = new ArrayList<RatedTransaction>();
	
	@Column(name = "DISCOUNT_PLAN_CODE", length = 50)
	private String discountPlanCode;
	
	@Column(name = "DISCOUNT_PLAN_ITEM_CODE", length = 50)
	private String discountPlanItemCode;
	
	
	
	@Column(name = "DISCOUNT_PERCENT", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal discountPercent;

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
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

	
	public Set<TaxInvoiceAgregate> getTaxInvoiceAggregates() {
		return taxInvoiceAggregates;
	}

	public void setTaxInvoiceAggregates(Set<TaxInvoiceAgregate> taxInvoiceAggregates) {
		this.taxInvoiceAggregates = taxInvoiceAggregates;
	}
	
	public void addTaxInvoiceAggregate(TaxInvoiceAgregate taxInvoiceAggregate) {
		if(taxInvoiceAggregates==null){
			taxInvoiceAggregates=new HashSet<TaxInvoiceAgregate>();
		}
		if(taxInvoiceAggregate!=null){
			taxInvoiceAggregates.add(taxInvoiceAggregate);
		}
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

	
	
	public Set<Tax> getSubCategoryTaxes() {
		return subCategoryTaxes;
	}

	public void setSubCategoryTaxes(Set<Tax> subCategoryTaxes) {
		this.subCategoryTaxes = subCategoryTaxes;
	}

	public void addSubCategoryTax(Tax subCategoryTax) {
		if(subCategoryTaxes==null){
			subCategoryTaxes=new HashSet<Tax>();
		}
		if(subCategoryTax!=null){
			subCategoryTaxes.add(subCategoryTax);
		}
	}

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}

	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}

	public String getDiscountPlanItemCode() {
		return discountPlanItemCode;
	}

	public void setDiscountPlanItemCode(String discountPlanItemCode) {
		this.discountPlanItemCode = discountPlanItemCode;
	}



	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}
	
	
	

}
