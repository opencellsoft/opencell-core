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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "CAT_ONE_SHOT_CHARGE_TEMPL")
public class OneShotChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 1L;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;
    
    @Column(name = "IMMEDIATE_INVOICING")
    private Boolean immediateInvoicing = true;

    public OneShotChargeTemplateTypeEnum getOneShotChargeTemplateType() {
        return oneShotChargeTemplateType;
    }

    public void setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum oneShotChargeTemplateType) {
        this.oneShotChargeTemplateType = oneShotChargeTemplateType;
    }

	public Boolean getImmediateInvoicing() {
		return immediateInvoicing;
	}

	public void setImmediateInvoicing(Boolean immediateInvoicing) {
		this.immediateInvoicing = immediateInvoicing;
	}

    

}
