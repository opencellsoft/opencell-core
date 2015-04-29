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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("T")
public class TaxInvoiceAgregate extends InvoiceAgregate {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAX_ID")
	private Tax tax;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUb_CAT_INVOICE_AGREGATE_ID")
	SubCategoryInvoiceAgregate subCategoryInvoiceAggregate;

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public SubCategoryInvoiceAgregate getSubCategoryInvoiceAggregate() {
		return subCategoryInvoiceAggregate;
	}

	public void setSubCategoryInvoiceAggregate(
			SubCategoryInvoiceAgregate subCategoryInvoiceAggregate) {
		this.subCategoryInvoiceAggregate = subCategoryInvoiceAggregate;
	}



}
