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
package org.meveo.admin.action.catalog;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link ChargeInstance} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ChargeTemplateBean extends BaseBean<ChargeTemplate> {
	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link OneShotChargeTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private ChargeTemplateServiceAll chargeTemplateService;
	
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;

	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	
	

	private String pricePlanCode;
	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ChargeTemplateBean() {
		super(ChargeTemplate.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("chargeTemplate")
	public ChargeTemplate init() {
		return initEntity();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ChargeTemplate> getPersistenceService() {
		return chargeTemplateService;
	}
	
	public String getPricePlanCode() {
		Long  seq=pricePlanMatrixService.getLastPricePlan(entity.getCode(),entity.getProvider())+1;
		pricePlanCode="PP_"+entity.getCode()+"_"+seq;
		return pricePlanCode;
	}
 
     public String getBackView(){
    	  RecurringChargeTemplate recurring= recurringChargeTemplateService.findById(entity.getId());
    	  if(recurring!=null){
    		return "recurringChargeTemplateDetail";
    	}else{
    	  OneShotChargeTemplate oneShot= oneShotChargeTemplateService.findById(entity.getId());
    	  if(oneShot!=null){
      		return "oneShotChargeTemplateDetail";
      	 }
    	  else{
         	return "usageChargeTemplateDetail";
         }
    	}
     }
	
	
	public void setPricePlanCode(String pricePlanCode) {
		this.pricePlanCode = pricePlanCode;
	}
	
	

}
