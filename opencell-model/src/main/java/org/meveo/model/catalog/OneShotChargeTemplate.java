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
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.MultilanguageEntity;

@Entity
@MultilanguageEntity(key = "menu.charges", group = "ChargeTemplate")
@Table(name = "cat_one_shot_charge_templ")
@NamedQueries({			
@NamedQuery(name = "oneShotChargeTemplate.getNbrOneShotWithNotPricePlan", 
	           query = "select count (*) from OneShotChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null)"),
	           
@NamedQuery(name = "oneShotChargeTemplate.getOneShotWithNotPricePlan", 
	           query = "from OneShotChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null)"),
	           	              
@NamedQuery(name = "oneShotChargeTemplate.getNbrSubscriptionChrgNotAssociated", 
	           query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateSubscription serv) "
	           		+ "OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType"),
	           	              
@NamedQuery(name = "oneShotChargeTemplate.getSubscriptionChrgNotAssociated", 
	 	           query = "from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateSubscription serv) "
	 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
	 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType"),
	 	           		
@NamedQuery(name = "oneShotChargeTemplate.getNbrTerminationChrgNotAssociated", 
		 	           query = "select count (*) from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateTermination serv) "
		 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
		 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType "),
		 	           		
@NamedQuery(name = "oneShotChargeTemplate.getTerminationChrgNotAssociated", 
		 	           query = "from  OneShotChargeTemplate o where (o.id not in (select serv.chargeTemplate from ServiceChargeTemplateTermination serv) "
		 	           		+ " OR o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null))"
		 	           		+ " and  oneShotChargeTemplateType=:oneShotChargeTemplateType")           	                  	         
	})
public class OneShotChargeTemplate extends ChargeTemplate {
	
	@Transient
	public static final String CHARGE_TYPE = "ONESHOT";

    private static final long serialVersionUID = 1L;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OneShotChargeTemplateTypeEnum oneShotChargeTemplateType;
    
    @Type(type="numeric_boolean")
    @Column(name = "immediate_invoicing")
    private Boolean immediateInvoicing = false;
    
    @Column(name = "filter_expression", length = 2000)
	@Size(max = 2000)
	private String filterExpression = null;

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

	public String getChargeType() {
		return CHARGE_TYPE;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

}
