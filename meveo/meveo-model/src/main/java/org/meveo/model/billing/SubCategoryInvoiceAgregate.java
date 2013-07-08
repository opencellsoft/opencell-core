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

}
