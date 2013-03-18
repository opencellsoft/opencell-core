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
 * InvoiceCategoryCountry entity.
 * 
 * @author Marouane ALAMI
 * @created 2013.03.07
 */

@Entity
@Table(name = "BILLING_INVOIC_CAT_COUNTR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOIC_CAT_COUNTR_SEQ")

public class InvoiceCategoryCountry  extends AuditableEntity{
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_CATEGORY_ID")
	private InvoiceCategory invoiceCategory;
	
	
	@Column(name = "COUNTRY_CODE", length = 2)
	private String countryCode;
	
	
	
	
	@Column(name = "DISCOUNT_CODE", length = 20)
	private String discountCode;
	

	


	public InvoiceCategory getInvoiceCategory() {
		return invoiceCategory;
	}


	public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}



	public String getDiscountCode() {
		return discountCode;
	}


	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}



	
	
}
