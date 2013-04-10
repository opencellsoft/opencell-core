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
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OperationTypeEnum;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "CAT_CHARGE_TEMPLATE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CHARGE_TEMPLATE_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
public class ChargeTemplate extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "CREDIT_DEBIT_FLAG")
    private OperationTypeEnum type;
    
    @Column(name = "AMOUNT_EDITABLE")
    private Boolean amountEditable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_SUB_CATEGORY", nullable = false)
    @NotNull
    private InvoiceSubCategory invoiceSubCategory;

    @OneToMany(mappedBy = "chargeTemplate", fetch = FetchType.LAZY)
    private List<ChargeInstance> chargeInstances = new ArrayList<ChargeInstance>();

    public OperationTypeEnum getType() {
		return type;
	}

	public void setType(OperationTypeEnum type) {
		this.type = type;
	}

	public Boolean getAmountEditable() {
        return amountEditable;
    }

    public void setAmountEditable(Boolean amountEditable) {
        this.amountEditable = amountEditable;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public List<ChargeInstance> getChargeInstances() {
        return chargeInstances;
    }

    public void setChargeInstances(List<ChargeInstance> chargeInstances) {
        this.chargeInstances = chargeInstances;
    }

}
