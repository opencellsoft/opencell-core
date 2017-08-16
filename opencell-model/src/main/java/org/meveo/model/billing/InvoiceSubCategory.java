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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@MultilanguageEntity(key = "menu.invoiceSubCategories", group = "InvoiceSubCategory")
@ExportIdentifier({ "code"})
@Table(name = "billing_invoice_sub_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_invoice_sub_cat_seq"), })
@CustomFieldEntity(cftCodePrefix = "INV_SUB_CAT")
@NamedQueries({
		@NamedQuery(name = "invoiceSubCategory.getNbrInvoiceSubCatNotAssociated", query = "select count(*) from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null)"
				+ " and v.id not in (select inv.invoiceSubCategory.id from InvoiceSubcategoryCountry inv where inv.invoiceSubCategory.id is not null)"),

		@NamedQuery(name = "invoiceSubCategory.getInvoiceSubCatNotAssociated", query = "from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null) "
				+ " and v.id not in (select inv.invoiceSubCategory.id from InvoiceSubcategoryCountry inv where inv.invoiceSubCategory.id is not null)") })
public class InvoiceSubCategory extends BusinessCFEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "accounting_code", length = 255)
	@Size(max = 255)
	private String accountingCode;

	@Column(name = "discount")
	private BigDecimal discount;

	@OneToMany(mappedBy = "invoiceSubCategory", fetch = FetchType.LAZY)
	private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = new ArrayList<InvoiceSubcategoryCountry>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_category_id")
	private InvoiceCategory invoiceCategory;
	
	@ManyToOne()
	@JoinColumn(name = "tax_script_instance_id")
	private ScriptInstance taxScript;

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

	@Override
	public ICustomFieldEntity[] getParentCFEntities() {
		return new ICustomFieldEntity[] { invoiceCategory };
	}

	@Override
	public int hashCode() {
		return id != null ? id.intValue() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof InvoiceSubCategory)) {
            return false;
        }
		
		InvoiceSubCategory other = (InvoiceSubCategory) obj;
		if (code == null) {
			if (other.getCode() != null)
				return false;
		} else if (!code.equals(other.getCode()))
			return false;
		
		return true;
	}

	public ScriptInstance getTaxScript() {
		return taxScript;
	}

	public void setTaxScript(ScriptInstance taxScript) {
		this.taxScript = taxScript;
	}

}
