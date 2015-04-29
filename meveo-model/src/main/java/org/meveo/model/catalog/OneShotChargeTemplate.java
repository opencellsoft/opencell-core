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
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.meveo.model.MultilanguageEntity;

@Entity
@MultilanguageEntity
@Table(name = "CAT_ONE_SHOT_CHARGE_TEMPL")
@NamedQueries({			
@NamedQuery(name = "oneShotChargeTemplate.getNbrOneShotWithNotPricePlan", 
	           query = "select count (*) from OneShotChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) and o.provider=:provider"),
	           
@NamedQuery(name = "oneShotChargeTemplate.getOneShotWithNotPricePlan", 
	           query = "from OneShotChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) and o.provider=:provider"),
	           	              
@NamedQuery(name = "oneShotChargeTemplate.getNbrSubscriptionChrgNotAssociated", 
	           query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateSubscription serv) "
	           		+ "OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType and o.provider=:provider"),
	           	              
@NamedQuery(name = "oneShotChargeTemplate.getSubscriptionChrgNotAssociated", 
	 	           query = "from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateSubscription serv) "
	 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
	 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType and o.provider=:provider"),
	 	           		
@NamedQuery(name = "oneShotChargeTemplate.getNbrTerminationChrgNotAssociated", 
		 	           query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateTermination serv) "
		 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
		 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType and o.provider=:provider"),
		 	           		
@NamedQuery(name = "oneShotChargeTemplate.getTerminationChrgNotAssociated", 
		 	           query = "from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateTermination serv) "
		 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
		 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType and o.provider=:provider")           	                  	         
	})
public class OneShotChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 1L;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;
    
    @Column(name = "IMMEDIATE_INVOICING")
    private Boolean immediateInvoicing = false;

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
