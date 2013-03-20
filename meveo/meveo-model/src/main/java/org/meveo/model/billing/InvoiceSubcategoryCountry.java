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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;

/**
 * InvoiceSubcategoryCountry entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "BILLING_INVOIC_SUBCAT_COUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOIC_SUBCAT_COUNT_SEQ")

public class InvoiceSubcategoryCountry  extends AuditableEntity{
	private static final long serialVersionUID = 1L;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_SUBCATEGORY_ID")
	private InvoiceSubCategory invoiceSubCategory;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_CODE")
	private CountryCom countryCom;
	

	@Column(name = "DISCOUNT_CODE", length = 20)
	private String discountCode;
	
	
	@Column(name = "TAX_CODE", length = 20)
	private String taxCode;
	
		

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	
	}


	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public CountryCom getCountryCom() {
		return countryCom;
	}


	public void setCountryCom(CountryCom countryCom) {
		this.countryCom = countryCom;
	}
	


	public String getDiscountCode() {
		return discountCode;
	}


	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}


	public String getTaxCode() {
		return taxCode;
	}


	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	
}
