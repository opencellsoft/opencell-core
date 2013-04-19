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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_INVOICE_CAT")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "BILLING_INVOICE_CAT_SEQ")
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
