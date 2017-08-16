/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@MultilanguageEntity(key = "menu.invoiceCategories", group="InvoiceCategory")
@ExportIdentifier({ "code"})
@Table(name = "billing_invoice_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_invoice_cat_seq"), })
@CustomFieldEntity(cftCodePrefix = "INV_CAT")
@NamedQueries({			
@NamedQuery(name = "invoiceCategory.getNbrInvoiceCatNotAssociated", 
	           query = "select count(*) from InvoiceCategory v where v.id not in (select sub.invoiceCategory.id from InvoiceSubCategory sub where sub.invoiceCategory.id is not null)"),
	           
@NamedQuery(name = "invoiceCategory.getInvoiceCatNotAssociated", 
	           query = "from InvoiceCategory v where v.id not in (select sub.invoiceCategory.id from InvoiceSubCategory sub where sub.invoiceCategory.id is not null) ")         	                  	         
})
public class InvoiceCategory extends BusinessCFEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "invoiceCategory", fetch = FetchType.LAZY)
	private List<InvoiceSubCategory> invoiceSubCategories;

	@Column(name = "sort_index")
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