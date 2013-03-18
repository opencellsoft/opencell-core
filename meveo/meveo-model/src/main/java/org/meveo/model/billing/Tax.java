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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_TAX")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_TAX_SEQ")
public class Tax extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "ACCOUNTING_CODE", length = 255)
    private String accountingCode;

    @Column(name = "PERCENT", precision = 19, scale = 8)
    private BigDecimal percent;

    @Column(name = "PR_DESCRIPTION", length = 50)
    private String prDescription;

   

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}


	public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

}
