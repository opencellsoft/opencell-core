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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "BILLING_INVOICE_CAT", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CAT_SEQ")
public class InvoiceCategory extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "invoiceCategory", fetch = FetchType.LAZY)
	private List<InvoiceSubCategory> invoiceSubCategories;

	@Column(name = "SORT_INDEX")
	private Integer sortIndex;

	public List<InvoiceSubCategory> getInvoiceSubCategories() {
		return invoiceSubCategories;
	}

	public void setInvoiceSubCategories(List<InvoiceSubCategory> invoiceSubCategories) {
		this.invoiceSubCategories = invoiceSubCategories;
	}

	public Integer getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

}
