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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_INVOICE_SUB_CAT")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_SUB_CAT_SEQ")
public class InvoiceSubCategory extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "ACCOUNTING_CODE", length = 255)
    private String accountingCode;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;
    
    @OneToMany(mappedBy = "invoiceSubCategory", fetch = FetchType.LAZY)
    private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries=new ArrayList<InvoiceSubcategoryCountry>();
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_CATEGORY_ID")
    private InvoiceCategory invoiceCategory;


	public List<InvoiceSubcategoryCountry> getInvoiceSubcategoryCountries() {
		return invoiceSubcategoryCountries;
	}

	public void setInvoiceSubcategoryCountries(
			List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
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



}
